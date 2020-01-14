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


  /**
   * Check if input represents whitespace.
   *
   * @param r the input
   *
   * @return true if whitespace
   */
  static boolean isWhite(int r) {
    return (r == ' ' || r == '\n' || r == '\r' || r == '\t');
  }


  static String safe(int r) {
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
    if (depth == -1) {
      doNextInRoot();
      return;
    }
    if (isObject[depth]) {
      if (expectingKey) {
        expectingKey = false;
        doNextKeyName();
      } else {
        expectingKey = true;
        doNextInObject();
      }
    } else {
      doNextInArray();
    }
  }


  private void doNextInArray() {
    int r = skipWhite();

    // do we expect a normal post-value symbol?
    switch (r) {
      case ',':
        if (lastEvent == Event.START_ARRAY) {
          throw new JsonParsingException("Saw ',' immediately after " + lastEvent, input.getLocation());
        }
        r = skipWhite();
        break;
      case ']':
        endStructure(false);
        return;
      case -1:
        throw new JsonParsingException("Saw EOF when there were " + (depth + 1) + " unclosed structures", input.getLocation());
      default:
        if (lastEvent != Event.START_ARRAY && lastEvent != Event.START_OBJECT) {
          throw new JsonParsingException(String.format("Expected a legitimate post-value character, but saw %s", safe(r)), input.getLocation());
        }
    }

    doNextInContainer(r);
  }


  private void doNextInContainer(int r) {
    if (r == '\"') {
      parseString();
      nextEvent = Event.VALUE_STRING;
      return;
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
      return;
    }
    if (r == 'n') {
      parseNull();
      return;
    }
    if (r == '-' || ('0' <= r && r <= '9')) {
      parseNumber(r);
      return;
    }
    throw new JsonParsingException(String.format("Invalid input: %s", safe(r)), input.getLocation());
  }


  private void doNextInObject() {
    int r = skipWhite();

    // If the last event was a key-name, then the only legitimate follow-on is a ':' and a value.
    if (r != ':') {
      throw new JsonParsingException(String.format("A ':' is required after a field name. Saw %s", safe(r)), input.getLocation());
    }

    doNextInContainer(skipWhite());
  }


  private void doNextInRoot() {
    int r = skipWhite();

    switch (r) {
      case -1:
        nextEvent = null;
        isRunning = false;
        return;

      case '\"':
        parseString();
        checkSingleRoot();
        nextEvent = Event.VALUE_STRING;
        return;

      case '[':
        nextEvent = Event.START_ARRAY;
        startStructure(false);
        return;

      case '{':
        nextEvent = Event.START_OBJECT;
        startStructure(true);
        expectingKey = true;
        return;

      case 't': // falls through
      case 'f':
        parseBoolean(r);
        checkSingleRoot();
        return;

      case 'n':
        parseNull();
        checkSingleRoot();
        return;

      default:
        if (r == '-' || ('0' <= r && r <= '9')) {
          parseNumber(r);
          checkSingleRoot();
          return;
        }
        throw new JsonParsingException(String.format("Invalid input: %s", safe(r)), input.getLocation());
    }
  }


  private void doNextKeyName() {
    int r = skipWhite();

    // do we expect a normal post-value symbol?
    switch (r) {
      case ',':
        if (lastEvent == Event.START_OBJECT) {
          throw new JsonParsingException("Saw ',' immediately after " + lastEvent, input.getLocation());
        }
        r = skipWhite();
        break;
      case '}':
        endStructure(true);
        return;
      case -1:
        throw new JsonParsingException("Saw EOF when there were " + (depth + 1) + " unclosed structures", input.getLocation());
      default:
        if (lastEvent != Event.START_OBJECT) {
          throw new JsonParsingException(String.format("Expected a legitimate post-value character, but saw %s", safe(r)), input.getLocation());
        }
    }

    // Expecting either a key-name or a value. Either way, a string is legitimate.
    if (r == '\"') {
      parseString();
      nextEvent = Event.KEY_NAME;
      return;
    }

    throw new JsonParsingException(String.format("Expecting a key-name, but saw %s", safe(r)), input.getLocation());
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
    if (recursion > MAX_RECURSION_DEPTH) {
      throw new JsonParsingException("Json structure has exceeded the configured maximum nesting depth of " + MAX_RECURSION_DEPTH, input.getLocation());
    }
    if (lastEvent == Event.KEY_NAME || lastEvent == Event.END_ARRAY || lastEvent == Event.END_OBJECT) {
      throw new IllegalStateException("Parser is not at start of value, but at " + lastEvent);
    }

    try {
      if (lastEvent == Event.START_ARRAY) {
        return doArray(recursion + 1);
      }
      if (lastEvent == Event.START_OBJECT) {
        return doObject(recursion + 1);
      }
    } catch (StackOverflowError e) {
      throw new JsonParsingException(
          "Json structure was less than configured maximum nesting depth of " + MAX_RECURSION_DEPTH + " but parsing failed at " + depth,
          input.getLocation()
      );
    }

    return value;
  }


  private void endStructure(boolean endObject) {
    depth--;
    nextEvent = endObject ? Event.END_OBJECT : Event.END_ARRAY;
    checkSingleRoot();
    expectingKey = depth >= 0 && isObject[depth];
  }


  @Override
  public JArray getArray() {
    return doArray(0);
  }


  @Override
  public Stream<JsonValue> getArrayStream() {
    checkState(Event.START_ARRAY);
    Iterator<JsonValue> iter = new Iterator<>() {
      boolean hasNextCalled = false;

      boolean nextExists = false;


      public boolean hasNext() {
        if (hasNextCalled) {
          return nextExists;
        }
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
        if (hasNext()) {
          hasNextCalled = false;
          return getValue();
        }
        throw new NoSuchElementException();
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
    Iterator<Entry<String, JsonValue>> iter = new Iterator<>() {
      boolean hasNextCalled = false;

      String key;

      boolean nextExists = false;


      public boolean hasNext() {
        if (hasNextCalled) {
          return nextExists;
        }
        hasNextCalled = true;
        nextExists = false;

        if (!JParser.this.hasNext()) {
          throw new JsonParsingException("Object was not terminated.", input.getLocation());
        }

        Event event = JParser.this.next();
        if (event == Event.END_OBJECT) {
          return false;
        }
        if (event != Event.KEY_NAME) {
          throw new JsonParsingException("Saw event " + event + " when only KEY_NAME was valid.", input.getLocation());
        }
        key = getString();

        if (!JParser.this.hasNext()) {
          throw new JsonParsingException("Object was not terminated.", input.getLocation());
        }
        return true;
      }


      public Entry<String, JsonValue> next() {
        if (!hasNext()) {
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
   */
  private void matchLiteral(char[] literal) {
    for (char c : literal) {
      int r = input.read();
      if (r == -1) {
        throw new JsonParsingException("Encountered EOF when parsing literal.", input.getLocation());
      }
      if (r != c) {
        throw new JsonParsingException(
            "Invalid character in literal. Saw " + safe(r) + " when expecting '" + c + "'",
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
    NumberParser numberParser = new NumberParser(input);
    BigDecimal val = numberParser.parse(r);
    value = new PNumber(val);
    nextEvent = Event.VALUE_NUMBER;
  }


  /**
   * Parse a string from the input.
   */
  private void parseString() {
    StringParser parser = new StringParser(input);
    String val = parser.parse();
    value = new PString(val);
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
