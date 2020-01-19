package io.setl.json.parser;

import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
public class JStructureParser extends BaseIterator<Event> implements JsonParser {

  private WalkingParser delegate;

  private Event firstEvent;


  public JStructureParser(JsonObject obj) {
    delegate = new ObjectWalker(null, obj);
    firstEvent = Event.START_OBJECT;
  }


  public JStructureParser(JsonArray array) {
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
      firstEvent = null;
      return event;
    }
    Event event = delegate.next();
    switch (event) {
      case END_ARRAY: // falls through
      case END_OBJECT:
        delegate = delegate.getParent();
        break;
      case START_ARRAY:
        delegate = new ArrayWalker(delegate, delegate.getArray());
        break;
      case START_OBJECT:
        delegate = new ObjectWalker(delegate, delegate.getObject());
        break;
      default:
        // do nothing
        break;
    }

    return event;
  }


  @Override
  public JsonArray getArray() {
    return delegate.getArray();
  }


  @Override
  public Stream<JsonValue> getArrayStream() {
    return delegate.getArrayStream();
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
    return delegate.getLocation();
  }


  @Override
  public long getLong() {
    return delegate.getLong();
  }


  @Override
  public JsonObject getObject() {
    return delegate.getObject();
  }


  @Override
  public Stream<Entry<String, JsonValue>> getObjectStream() {
    return delegate.getObjectStream();
  }


  @Override
  public String getString() {
    return delegate.getString();
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
    delegate.skipArray();
  }


  @Override
  public void skipObject() {
    delegate.skipObject();
  }

}
