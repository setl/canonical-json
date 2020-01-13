package io.setl.json.parser;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.JType;
import io.setl.json.Primitive;
import io.setl.json.io.Input;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PNumber;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JParser implements JsonParser {

  /**
   * Letters for the "false" literal.
   */
  private static final char[] LITERAL_FALSE = new char[]{'a', 'l', 's', 'e'};

  /**
   * Letters for the "null" literal.
   */
  private static final char[] LITERAL_NULL = new char[]{'u', 'l', 'l'};

  /**
   * Letters for the "true" literal.
   */
  private static final char[] LITERAL_TRUE = new char[]{'r', 'u', 'e'};

  private static final int MAX_RECURSION_DEPTH = Integer.getInteger(JParser.class.getPackageName() + ".maxRecursion", 1_000);


  private static JsonParsingException badNumber(StringBuilder buf, int r, Input input) {
    return new JsonParsingException(
        "Invalid character in JSON number: \"" + buf.toString() + "\" was followed by " + safe(r),
        input.getLocation()
    );
  }


  /**
   * Check if input represents whitespace.
   *
   * @param r the input
   *
   * @return true if whitespace
   */
  private static boolean isWhite(int r) {
    return (r == ' ' || r == '\n' || r == '\r' || r == '\t');
  }


  private static String safe(int r) {
    if (r == -1) {
      return "EOF";
    }
    if (32 <= r && r < 127) {
      return "'" + ((char) r) + "'";
    }
    return String.format("character 0x%04x", r);
  }


  /**
   * The input.
   */
  private final Input input;

  /**
   * Depth of nesting containers from document root.
   */
  private int depth = -1;

  private boolean expectingKey = false;

  /**
   * Are the nesting containers arrays or objects?. True for objects.
   */
  private boolean[] isObject = new boolean[16];

  private boolean isRunning = true;

  /**
   * The last event returned from <code>next</code>.
   */
  private Event lastEvent = null;

  /**
   * The next event identified be <code>hasNext</code>.
   */
  private Event nextEvent = null;

  /**
   * Expect a single root value?.
   */
  private boolean singleRoot = true;

  /**
   * The last value loaded.
   */
  private Primitive value = PNull.NULL;


  public JParser(Reader reader) {
    input = new Input(reader);
  }


  private void checkNumber() {
    if (value.getType() != JType.NUMBER) {
      throw new IllegalStateException("Current value is a " + value.getValueType() + " not a number.");
    }
  }


  private void checkSingleRoot() {
    if (depth >= 0 || !singleRoot) {
      // Not in root, or don't care
      return;
    }

    // can only have whitespace and then EOF
    int r = skipWhite();
    if (r != -1) {
      throw new JsonParsingException(String.format("Saw %s after root value.", safe(r)), input.getLocation());
    }
  }


  private void checkState(Event required) {
    if (required != lastEvent) {
      throw new IllegalStateException("State must be " + required + ", not: " + lastEvent);
    }
  }


  @Override
  public void close() {
    input.close();
  }


  private JArray doArray(int recursion) {
    checkState(Event.START_ARRAY);
    JArray jArray = new JArray();
    while (true) {
      if (!hasNext()) {
        throw new JsonParsingException("Array was not terminated.", input.getLocation());
      }
      Event event = next();
      if (event == Event.END_ARRAY) {
        break;
      }
      jArray.add(doValue(recursion));
    }
    return jArray;
  }


  /**
   * Advance the parser.
   */
  private void doNext() {
    int r = skipWhite();

    // If the last event was a key-name, then the only legitimate follow-on is a ':' and a value.
    if (lastEvent == Event.KEY_NAME) {
      if (r != ':') {
        throw new JsonParsingException(String.format("A ':' is required after a field name. Saw %s", safe(r)), input.getLocation());
      }
      r = skipWhite();
    } else {

      // do we expect a normal post-value symbol?
      switch (r) {
        case ':':
          throw new JsonParsingException("A ':' can only occur after a field name.", input.getLocation());
        case ',':
          if (depth == -1) {
            throw new JsonParsingException("Saw ',' at the root level", input.getLocation());
          }
          if (lastEvent == Event.START_OBJECT || lastEvent == Event.START_ARRAY) {
            throw new JsonParsingException("Saw ',' immediately after " + lastEvent, input.getLocation());
          }
          r = skipWhite();
          break;
        case ']':
          if (depth >= 0 && !isObject[depth]) {
            depth--;
            nextEvent = Event.END_ARRAY;
            checkSingleRoot();
            return;
          } else {
            throw new JsonParsingException("Saw ']' when the current container was not an array.", input.getLocation());
          }
        case '}':
          if (depth >= 0 && isObject[depth]) {
            depth--;
            nextEvent = Event.END_OBJECT;
            checkSingleRoot();
            return;
          } else {
            throw new JsonParsingException("Saw '}' when the current container was not an object.", input.getLocation());
          }
        case -1:
          if (depth == -1) {
            nextEvent = null;
            isRunning = false;
            return;
          }
          throw new JsonParsingException("Saw EOF when there were " + (depth + 1) + " unclosed structures", input.getLocation());
        default:
          if (depth >= 0 && lastEvent != Event.START_ARRAY && lastEvent != Event.START_OBJECT) {
            throw new JsonParsingException(String.format("Expected a legitimate post-value character, but saw %s", safe(r)), input.getLocation());
          }
      }
    }

    // Expecting either a key-name or a value. Either way, a string is legitimate.
    if (r == '\"') {
      parseString();
      checkSingleRoot();
      nextEvent = expectingKey ? Event.KEY_NAME : Event.VALUE_STRING;
      if (expectingKey) {
        expectingKey = false;
      } else {
        expectingKey = depth >= 0 && isObject[depth];
      }
      return;
    }

    if (expectingKey) {
      throw new JsonParsingException(String.format("Expecting a key-name, but saw %s", safe(r)), input.getLocation());
    }

    if (r == '[') {
      nextEvent = Event.START_ARRAY;
      startStructure(false);
      return;
    }
    if (r == '{') {
      nextEvent = Event.START_OBJECT;
      startStructure(true);
      expectingKey = true;
      return;
    }

    if (r == 't' || r == 'f') {
      parseBoolean(r);
      expectingKey = depth >= 0 && isObject[depth];
      checkSingleRoot();
      return;
    }
    if (r == 'n') {
      parseNull();
      expectingKey = depth >= 0 && isObject[depth];
      checkSingleRoot();
      return;
    }
    if (r == '-' || ('0' <= r && r <= '9')) {
      parseNumber(r);
      expectingKey = depth >= 0 && isObject[depth];
      checkSingleRoot();
      return;
    }
    throw new JsonParsingException(String.format("Invalid input: %s", safe(r)), input.getLocation());
  }


  private JObject doObject(int recursion) {
    checkState(Event.START_OBJECT);
    JObject jObject = new JObject();
    while (true) {
      if (!hasNext()) {
        throw new JsonParsingException("Object was not terminated.", input.getLocation());
      }
      Event event = next();
      if (event == Event.END_OBJECT) {
        break;
      }
      if (event != Event.KEY_NAME) {
        throw new JsonParsingException("Encountered " + event + " when only key name was valid.", input.getLocation());
      }
      String key = getString();

      event = next();
      if (event == Event.KEY_NAME || event == Event.END_ARRAY || event == Event.END_OBJECT) {
        throw new IllegalStateException("Invalid event generated during parsing: " + event);
      }
      jObject.put(key, doValue(recursion));
    }

    return jObject;
  }


  private Primitive doValue(int recursion) {
    if (lastEvent == Event.KEY_NAME || lastEvent == Event.END_ARRAY || lastEvent == Event.END_OBJECT) {
      throw new IllegalStateException("Parser is not at start of value, but at " + lastEvent);
    }

    if (lastEvent == Event.START_ARRAY) {
      return doArray(recursion + 1);
    }
    if (lastEvent == Event.START_OBJECT) {
      return doObject(recursion + 1);
    }

    return value;
  }


  @Override
  public JArray getArray() {
    return doArray(0);
  }


  @Override
  public Stream<JsonValue> getArrayStream() {
    checkState(Event.START_ARRAY);
    Iterator<JsonValue> iter = new Iterator<JsonValue>() {
      boolean hasNextCalled = false;

      boolean nextExists = false;


      public boolean hasNext() {
        hasNextCalled = true;
        nextExists = false;
        if (!JParser.this.hasNext()) {
          throw new JsonParsingException("Array was not terminated.", input.getLocation());
        }
        if (JParser.this.next() == Event.END_ARRAY) {
          return false;
        }
        nextExists = true;
        return true;
      }


      public JsonValue next() {
        if (!hasNextCalled) {
          hasNext();
        }
        hasNextCalled = false;
        if (!nextExists) {
          throw new NoSuchElementException();
        }
        return getValue();
      }
    };
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, 0), false);
  }


  @Override
  public BigDecimal getBigDecimal() {
    checkNumber();
    return ((PNumber) value).bigDecimalValue();
  }


  @Override
  public int getInt() {
    checkNumber();
    return ((PNumber) value).intValue();
  }


  @Override
  public JsonLocation getLocation() {
    return input.getLocation();
  }


  @Override
  public long getLong() {
    checkNumber();
    return ((PNumber) value).longValue();
  }


  @Override
  public JObject getObject() {
    return doObject(0);
  }


  @Override
  public Stream<Entry<String, JsonValue>> getObjectStream() {
    checkState(Event.START_OBJECT);
    Iterator<Entry<String, JsonValue>> iter = new Iterator<Entry<String, JsonValue>>() {
      boolean hasNextCalled = false;

      String key;

      boolean nextExists = false;


      public boolean hasNext() {
        hasNextCalled = true;
        nextExists = false;
        if (!JParser.this.hasNext()) {
          throw new JsonParsingException("Object was not terminated.", input.getLocation());
        }
        if (JParser.this.next() == Event.END_OBJECT) {
          return false;
        }
        nextExists = true;
        key = getString();
        hasNext();
        return true;
      }


      public Entry<String, JsonValue> next() {
        if (!hasNextCalled) {
          hasNext();
        }
        if (!nextExists) {
          throw new NoSuchElementException();
        }
        return new AbstractMap.SimpleEntry<>(key, getValue());
      }
    };
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, 0), false);
  }


  @Override
  public String getString() {
    if (value.getType() != JType.STRING) {
      throw new IllegalStateException("Current value is a " + value.getValueType() + " not a string");
    }
    return ((PString) value).getString();
  }


  @Override
  public Primitive getValue() {
    return doValue(0);
  }


  @Override
  public Stream<JsonValue> getValueStream() {
    return null;
  }


  @Override
  public boolean hasNext() {
    if (nextEvent == null && isRunning) {
      doNext();
    }
    return nextEvent != null;
  }


  @Override
  public boolean isIntegralNumber() {
    checkNumber();
    return ((PNumber) value).isIntegral();
  }


  /**
   * Match a literal.
   *
   * @param literal the literal to match (excluding first character)
   *
   * @throws IOException if literal is not matched
   */
  private void matchLiteral(char[] literal) {
    for (int i = 0; i < literal.length; i++) {
      int r = input.read();
      if (r == -1) {
        throw new JsonParsingException("Encountered EOF when parsing literal.", input.getLocation());
      }
      if (r != literal[i]) {
        throw new JsonParsingException(
            "Invalid character in literal. Saw " + safe(r) + " when expecting '" + literal[i] + "'",
            input.getLocation()
        );
      }
    }
  }


  @Override
  public Event next() {
    lastEvent = nextEvent;
    if (hasNext()) {
      Event event = nextEvent;
      nextEvent = null;
      return event;
    }
    throw new NoSuchElementException();
  }


  /**
   * Parse a boolean from the input.
   *
   * @param r the initial character of the literal
   */
  private void parseBoolean(int r) {
    if (r == 't') {
      // match true
      matchLiteral(LITERAL_TRUE);
      value = PTrue.TRUE;
      nextEvent = Event.VALUE_TRUE;
      return;
    }

    // match false
    matchLiteral(LITERAL_FALSE);
    value = PFalse.FALSE;
    nextEvent = Event.VALUE_FALSE;
  }


  /**
   * Parse a null from the input.
   */
  private void parseNull() {
    matchLiteral(LITERAL_NULL);
    value = PNull.NULL;
    nextEvent = Event.VALUE_NULL;
  }


  /**
   * Parse a number from the input.
   *
   * @param r the initial character of the number
   */
  private void parseNumber(int r) {
    StringBuilder buf = new StringBuilder();
    // process first character
    int s;
    if (r == '-') {
      buf.append('-');
      s = 0;
    } else if (r == '0') {
      buf.append('0');
      s = 1;
    } else {
      buf.append((char) r);
      s = 2;
    }

    // read rest of number
    while (true) {
      r = input.read();
      if (r == -1 || isWhite(r) || r == ',' || r == ']' || r == '}') {
        // Check for an invalid number
        if (s == 0 || s == 10 || s == 20 || s == 21) {
          throw new JsonParsingException("Incomplete JSON number: \"" + buf.toString() + "\"", input.getLocation());
        }
        break;
      }

      switch (s) {
        case 0:
          // seen leading minus sign, must be followed by digit
          if (r == '0') {
            buf.append('0');
            s = 1;
          } else if ('1' <= r && r <= '9') {
            buf.append((char) r);
            s = 2;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 1:
          // seen leading zero, must be followed by '.', 'e', or 'E'
          if (r == '.') {
            // start fractional part
            buf.append('.');
            s = 10;
          } else if (r == 'e' || r == 'E') {
            // start exponent part
            buf.append((char) r);
            s = 20;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 2:
          // seen leading 1 to 9, can be followed by any digit, '.', 'e', or 'E'
          if ('0' <= r && r <= '9') {
            // carry on with integer part
            buf.append((char) r);
          } else if (r == '.') {
            // start fractional part
            buf.append('.');
            s = 10;
          } else if (r == 'e' || r == 'E') {
            // start exponent part
            buf.append((char) r);
            s = 20;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 10: // falls through
          // seen '.', read fractional part
          if ('0' <= r && r <= '9') {
            // carry on with fractional part
            buf.append((char) r);
            s = 11;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 11:
          // seen '.' and a digit, read fractional part
          if ('0' <= r && r <= '9') {
            // carry on with fractional part
            buf.append((char) r);
          } else if (r == 'e' || r == 'E') {
            // start exponent part
            buf.append((char) r);
            s = 20;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 20:
          // seen 'e' or 'E', read exponent part. Can be '+', '-' or '0' to '9'
          if (r == '+' || r == '-') {
            buf.append((char) r);
            s = 21;
          } else if ('0' <= r && r <= '9') {
            buf.append('+').append((char) r);
            s = 22;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 21:
          // fall thru
          // seen 'e' or 'E' followed by '+' or '-'. Must have a digit next. Cannot end in this state.
        case 22:
          // reading exponent, must be all digits
          if ('0' <= r && r <= '9') {
            buf.append((char) r);
            s = 22;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        default:
          throw new InternalError("Impossible parser state");
      }
    }
    if (r != -1) {
      input.unread(r);
    }
    Number val;
    try {
      val = new BigDecimal(buf.toString());
    } catch (NumberFormatException nfe) {
      throw new JsonParsingException("Number in JSON is too extreme to process: \"" + buf.toString() + "\"", input.getLocation());
    }
    value = new PNumber(val);
    nextEvent = Event.VALUE_NUMBER;
  }


  /**
   * Parse a string from the input.
   */
  private void parseString() {
    int s = 0;
    int u = 0;
    StringBuilder buf = new StringBuilder();
    boolean notDone = true;
    while (notDone) {
      int r = input.read();
      switch (s) {
        case 0:
          // regular character probable
          if (r == '"') {
            notDone = false;
            break;
          }
          if (r == '\\') {
            s = 1;
            break;
          }
          if (r == -1) {
            throw new JsonParsingException("Unterminated string", input.getLocation());
          }
          if (r < 32) {
            throw new JsonParsingException(String.format("JSON strings must not contain C0 control codes, including 0x%04x", r), input.getLocation());
          }
          buf.append((char) r);
          break;

        case 1:
          // expecting an escape sequence
          switch (r) {
            case '"':
              buf.append('\"');
              break;
            case '\\':
              buf.append('\\');
              break;
            case '/':
              buf.append('/');
              break;
            case 'b':
              buf.append('\b');
              break;
            case 'f':
              buf.append('\f');
              break;
            case 'n':
              buf.append('\n');
              break;
            case 'r':
              buf.append('\r');
              break;
            case 't':
              buf.append('\t');
              break;
            case 'u':
              u = 0;
              s = 2;
              break;
            default:
              throw new JsonParsingException(String.format("Invalid escape sequence '\\' followed by %s", safe(r)), input.getLocation());
          }
          if (s == 1) {
            s = 0;
          }
          break;

        case 2: // fall thru
        case 3: // fall thru
        case 4: // fall thru
        case 5: // fall thru
          // Unicode escape
          u = u * 16;
          if ('0' <= r && r <= '9') {
            u += r - '0';
          } else if ('a' <= r && r <= 'f') {
            u += r - 'a' + 10;
          } else if ('A' <= r && r <= 'F') {
            u += r - 'A' + 10;
          } else {
            throw new JsonParsingException(String.format("Invalid hex character in \\u escape. Saw %s", safe(r)), input.getLocation());
          }
          s++;
          if (s == 6) {
            s = 0;
            buf.append((char) u);
          }
          break;
        default:
          throw new InternalError("Impossible parser state");
      }
    }

    // finally create the string
    value = new PString(buf.toString());
  }


  public void setRequireSingleRoot(boolean singleRoot) {
    this.singleRoot = singleRoot;
  }


  @Override
  public void skipArray() {
    if (depth == -1 || !isObject[depth]) {
      // not in an array, so do nothing
      return;
    }
    int startDepth = depth;
    while (true) {
      if (!hasNext()) {
        throw new JsonParsingException("Array was not terminated", input.getLocation());
      }
      Event event = next();
      if (event == Event.END_ARRAY && depth == startDepth) {
        break;
      }
    }
  }


  @Override
  public void skipObject() {
    if (depth == -1 || isObject[depth]) {
      // not in an object, so do nothing
      return;
    }
    int startDepth = depth;
    while (true) {
      if (!hasNext()) {
        throw new JsonParsingException("Object was not terminated", input.getLocation());
      }
      Event event = next();
      if (event == Event.END_OBJECT && depth == startDepth) {
        break;
      }
    }
  }


  /**
   * Skip whitespace and return the first non-white character. Whitespace is not allowed in the canonical form.
   *
   * @return the first non-white character
   */
  private int skipWhite() {
    // whitespace allowed
    int r;
    do {
      r = input.read();
    }
    while (isWhite(r));
    return r;
  }


  private void startStructure(boolean startObject) {
    depth++;
    int size = isObject.length;
    if (size == depth) {
      int newSize = size * 2;
      boolean[] tmp = isObject;
      isObject = new boolean[newSize];
      System.arraycopy(tmp, 0, isObject, 0, size);
    }
    isObject[depth] = startObject;
  }

}
