package io.setl.json.parser;

import io.setl.json.io.Location;
import java.math.BigDecimal;
import java.util.stream.Stream;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
abstract class WalkingParser extends BaseIterator<Event> implements JsonParser {

  protected final int size;

  private final WalkingParser parent;

  protected int index = -1;

  private boolean doingContent = true;

  private Event finalEvent;


  protected WalkingParser(WalkingParser parent, int size, Event finalEvent) {
    this.parent = parent;
    this.size = size;
    this.finalEvent = finalEvent;
  }


  @Override
  protected boolean checkNext() {
    if (doingContent) {
      doingContent = checkNextImpl();
      return true;
    }
    return finalEvent != null;
  }


  protected abstract boolean checkNextImpl();


  private void checkType(JsonValue value, ValueType expected) {
    if (value.getValueType() != expected) {
      throw new IllegalStateException("Value is of type " + value.getValueType() + " not " + expected);
    }
  }


  @Override
  public void close() {
    hasNextCalled = true;
    nextExists = false;
  }


  protected Event eventForType(JsonValue value) {
    switch (value.getValueType()) {
      case OBJECT:
        return Event.START_OBJECT;
      case ARRAY:
        return Event.START_ARRAY;
      case STRING:
        return Event.VALUE_STRING;
      case NUMBER:
        return Event.VALUE_NUMBER;
      case TRUE:
        return Event.VALUE_TRUE;
      case FALSE:
        return Event.VALUE_FALSE;
      case NULL:
        return Event.VALUE_NULL;
      default:
        throw new IllegalStateException("Unknown value type: " + value.getValueType());
    }
  }


  @Override
  protected Event fetchNext() {
    if (doingContent) {
      return fetchNextImpl();
    }
    Event event = finalEvent;
    finalEvent = null;
    return event;
  }


  protected abstract Event fetchNextImpl();



  @Override
  public BigDecimal getBigDecimal() {
    return getNumber().bigDecimalValue();
  }


  @Override
  public int getInt() {
    return getNumber().intValue();
  }


  @Override
  public JsonLocation getLocation() {
    return Location.UNSET;
  }


  @Override
  public long getLong() {
    return getNumber().longValue();
  }


  protected JsonNumber getNumber() {
    JsonValue value = getValue();
    checkType(value, ValueType.NUMBER);
    return (JsonNumber) value;
  }


  WalkingParser getParent() {
    return parent;
  }


  @Override
  public String getString() {
    JsonValue value = getValue();
    if (value.getValueType() == ValueType.NUMBER) {
      // We have a primitive, and its toString will return the canonical form of the number
      return value.toString();
    }
    checkType(value, ValueType.STRING);
    return ((JsonString) value).getString();
  }


  @Override
  public Stream<JsonValue> getValueStream() {
    throw new IllegalStateException("Within a JSON structure");
  }


  @Override
  public boolean isIntegralNumber() {
    return getNumber().isIntegral();
  }


  abstract JsonValue primaryObject();

}
