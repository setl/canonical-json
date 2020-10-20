package io.setl.json.parser;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.Primitive;
import io.setl.json.io.Input;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import io.setl.json.primitive.numbers.NumberParser;
import io.setl.json.primitive.numbers.PNumber;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JParser extends BaseIterator<JsonParser.Event> implements JsonParser {

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
  public static boolean isWhite(int r) {
    return (r == ' ' || r == '\n' || r == '\r' || r == '\t');
  }


  /**
   * Get a safe representation of the input for logging.
   *
   * @param r the character, or -1 for EOF
   *
   * @return a representation
   */
  public static String safe(int r) {
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

  /**
   * The last event returned from <code>next</code>.
   */
  private Event lastEvent = null;

  /**
   * The next event identified be <code>hasNext</code>.
   */
  private Event nextEvent = null;

  /**
   * Have we seen our first root value?.
   */
  private boolean seenFirstRoot = false;

  /**
   * Expect a single root value?.
   */
  private boolean singleRoot = true;

  /**
   * Object identifier for iterators. Every Array and Object has its own identifier.
   */
  private StructureTag structureTag = new StructureTag(null);

  /**
   * The last value loaded.
   */
  private Primitive value = PNull.NULL;


  public JParser(Reader reader) {
    input = new Input(reader);
  }


  /**
   * Advance the parser.
   */
  protected boolean checkNext() {
    if (depth == -1) {
      doNextInRoot();
    } else if (isObject[depth]) {
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
    return nextEvent != null;
  }


  private void checkNumber() {
    if (value.getValueType() != ValueType.NUMBER) {
      throw new IllegalStateException("Current value is a " + value.getValueType() + " not a number.");
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
      iteratorFetchNext();
      if (lastEvent == Event.END_ARRAY) {
        break;
      }
      jArray.add(doValue(recursion));
    }
    return jArray;
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
    if (singleRoot && seenFirstRoot && r != -1) {
      throw new JsonParsingException(String.format("Saw %s after root value.", safe(r)), input.getLocation());
    }

    switch (r) {
      case -1:
        nextEvent = null;
        return;

      case '\"':
        parseString();
        seenFirstRoot = true;
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
        seenFirstRoot = true;
        return;

      case 'n':
        parseNull();
        seenFirstRoot = true;
        return;

      default:
        if (r == '-' || ('0' <= r && r <= '9')) {
          parseNumber(r);
          seenFirstRoot = true;
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
      ensureNextInObject();
      iteratorFetchNext();
      if (lastEvent == Event.END_OBJECT) {
        break;
      }
      if (lastEvent != Event.KEY_NAME) {
        throw new JsonParsingException("Encountered " + lastEvent + " when only key name was valid.", input.getLocation());
      }
      String key = getString();

      ensureNextInObject();
      iteratorFetchNext();
      if (lastEvent == Event.KEY_NAME || lastEvent == Event.END_ARRAY || lastEvent == Event.END_OBJECT) {
        throw new IllegalStateException("Invalid event generated during parsing: " + lastEvent);
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
    structureTag = structureTag.parent;
    depth--;
    nextEvent = endObject ? Event.END_OBJECT : Event.END_ARRAY;
    if (depth == -1) {
      seenFirstRoot = true;
    }
    expectingKey = depth >= 0 && isObject[depth];
  }


  private void ensureNextInObject() {
    if (!hasNext()) {
      throw new JsonParsingException("Object was not terminated.", input.getLocation());
    }
  }


  @Override
  protected Event fetchNext() {
    lastEvent = nextEvent;
    nextEvent = null;
    return lastEvent;
  }


  @Override
  public JArray getArray() {
    return doArray(0);
  }


  @Override
  public Stream<JsonValue> getArrayStream() {
    checkState(Event.START_ARRAY);
    BaseIterator<JsonValue> iter = new ArrayIterator(this::getTag, this);
    return iter.asStream();
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


  /**
   * Get the last event returned from "next".
   *
   * @return the last event
   */
  public Event getLastEvent() {
    return lastEvent;
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
    BaseIterator<Entry<String, JsonValue>> iter = new ObjectIterator(this::getTag, this);
    return iter.asStream();
  }


  @Override
  public String getString() {
    if (value.getValueType() != ValueType.STRING) {
      throw new IllegalStateException("Current value is a " + value.getValueType() + " not a string");
    }
    return ((PString) value).getString();
  }


  protected StructureTag getTag() {
    return structureTag;
  }


  @Override
  public Primitive getValue() {
    return doValue(0);
  }


  @Override
  public Stream<JsonValue> getValueStream() {
    if (depth != -1) {
      throw new IllegalStateException("Parser is within a JSON structure");
    }
    BaseIterator<JsonValue> iterator = new ValueIterator(this::getTag, this);
    return iterator.asStream();
  }


  @Override
  public boolean isIntegralNumber() {
    checkNumber();
    return ((PNumber) value).isIntegral();
  }


  void iteratorFetchNext() {
    hasNextCalled = false;
    lastEvent = nextEvent;
    nextEvent = null;
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
    value = numberParser.parse(r);
    nextEvent = Event.VALUE_NUMBER;
  }


  /**
   * Parse a string from the input.
   */
  private void parseString() {
    StringParser parser = new StringParser(input);
    String val = parser.parse();
    value = PString.create(val);
  }


  public void setRequireSingleRoot(boolean singleRoot) {
    this.singleRoot = singleRoot;
  }


  @Override
  public void skipArray() {
    if (depth == -1 || isObject[depth]) {
      // not in an array, so do nothing
      return;
    }
    int endDepth = depth - 1;
    while (true) {
      if (!hasNext()) {
        throw new JsonParsingException("Array was not terminated", input.getLocation());
      }
      Event event = next();
      if (event == Event.END_ARRAY && depth == endDepth) {
        break;
      }
    }
  }


  @Override
  public void skipObject() {
    if (depth == -1 || !isObject[depth]) {
      // not in an object, so do nothing
      return;
    }
    int endDepth = depth - 1;
    while (true) {
      if (!hasNext()) {
        throw new JsonParsingException("Object was not terminated", input.getLocation());
      }
      Event event = next();
      if (event == Event.END_OBJECT && depth == endDepth) {
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
    structureTag = new StructureTag(structureTag);
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
