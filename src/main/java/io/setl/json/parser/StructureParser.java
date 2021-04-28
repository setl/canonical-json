package io.setl.json.parser;

import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import io.setl.json.io.Location;

/**
 * Parse a JSON structure.
 *
 * @author Simon Greatrix on 15/01/2020.
 */
public class StructureParser extends BaseIterator<Event> implements JsonParser {

  private WalkingParser delegate;

  private Event firstEvent;

  /** The last event returned by next(). */
  private Event lastEvent = null;

  /** Tag for the current structure. */
  private StructureTag structureTag = new StructureTag(null);


  public StructureParser(JsonObject obj) {
    delegate = new ObjectWalker(null, obj);
    firstEvent = Event.START_OBJECT;
  }


  public StructureParser(JsonArray array) {
    delegate = new ArrayWalker(null, array);
    firstEvent = Event.START_ARRAY;
  }


  @Override
  protected boolean checkNext() {
    if (firstEvent != null) {
      return true;
    }
    return (delegate != null) && delegate.hasNext();
  }


  private void checkState(Event required) {
    if (required != lastEvent) {
      throw new IllegalStateException("State must be " + required + ", not: " + lastEvent);
    }
  }


  @Override
  public void close() {
    hasNextCalled = true;
    nextExists = false;
    delegate = null;
  }


  @Override
  protected Event fetchNext() {
    if (firstEvent != null) {
      Event event = firstEvent;
      lastEvent = event;
      firstEvent = null;
      return event;
    }
    Event event = delegate.next();
    switch (event) {
      case END_ARRAY: // falls through
      case END_OBJECT:
        structureTag = structureTag.parent;
        delegate = delegate.getParent();
        break;
      case START_ARRAY:
        delegate = new ArrayWalker(delegate, delegate.getArray());
        structureTag = new StructureTag(structureTag);
        break;
      case START_OBJECT:
        delegate = new ObjectWalker(delegate, delegate.getObject());
        structureTag = new StructureTag(structureTag);
        break;
      default:
        // do nothing
        break;
    }
    lastEvent = event;
    return event;
  }


  @Override
  public JsonArray getArray() {
    checkState(Event.START_ARRAY);
    delegate = delegate.getParent();
    return delegate.getArray();
  }


  @Override
  public Stream<JsonValue> getArrayStream() {
    checkState(Event.START_ARRAY);
    return new ArrayIterator(this::getTag, this).asStream();
  }


  @Override
  public BigDecimal getBigDecimal() {
    return delegate.getBigDecimal();
  }


  @Override
  public int getInt() {
    return delegate.getInt();
  }


  @Override
  public JsonLocation getLocation() {
    return Location.UNSET;
  }


  @Override
  public long getLong() {
    return delegate.getLong();
  }


  @Override
  public JsonObject getObject() {
    checkState(Event.START_OBJECT);
    delegate = delegate.getParent();
    return delegate.getObject();
  }


  @Override
  public Stream<Entry<String, JsonValue>> getObjectStream() {
    checkState(Event.START_OBJECT);
    return new ObjectIterator(this::getTag, this).asStream();
  }


  @Override
  public String getString() {
    return delegate.getString();
  }


  protected StructureTag getTag() {
    return structureTag;
  }


  @Override
  public JsonValue getValue() {
    return delegate.getValue();
  }


  @Override
  public Stream<JsonValue> getValueStream() {
    if (firstEvent != null) {
      return Stream.<JsonValue>builder().add(delegate.primaryObject()).build();
    }
    throw new IllegalStateException("Not in root context");
  }


  @Override
  public boolean isIntegralNumber() {
    return delegate.isIntegralNumber();
  }


  @Override
  public void skipArray() {
    if (delegate.primaryObject().getValueType() == ValueType.ARRAY) {
      delegate = delegate.getParent();
      structureTag = structureTag.parent;
    }
  }


  @Override
  public void skipObject() {
    if (delegate.primaryObject().getValueType() == ValueType.OBJECT) {
      delegate = delegate.getParent();
      structureTag = structureTag.parent;
    }
  }

}
