package io.setl.json.parser;

import java.util.NoSuchElementException;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser.Event;

import io.setl.json.primitive.CJNull;
import io.setl.json.primitive.CJString;

/**
 * Walk through a JSON Object structure.
 *
 * @author Simon Greatrix on 15/01/2020.
 */
class ObjectWalker extends WalkingParser {

  final JsonObject object;

  private final String[] keys;

  private boolean isKey = false;


  ObjectWalker(WalkingParser delegate, JsonObject object) {
    super(delegate, object.size(), Event.END_OBJECT);
    this.object = object;
    keys = object.keySet().toArray(new String[0]);
  }


  @Override
  protected boolean checkNextImpl() {
    if (isKey) {
      isKey = false;
      return true;
    }

    index++;
    isKey = true;
    return index < size;
  }


  @Override
  protected Event fetchNextImpl() {
    if (isKey) {
      return Event.KEY_NAME;
    }
    return eventForType(object.get(keys[index]));
  }


  @Override
  public JsonValue getValue() {
    if (index < 0) {
      throw new IllegalStateException("Next has not been called");
    }
    if (index >= size) {
      throw new NoSuchElementException();
    }
    if (isKey) {
      return CJString.create(keys[index]);
    }
    JsonValue jv = object.get(keys[index]);
    return (jv != null) ? jv : CJNull.NULL;
  }


  @Override
  JsonValue primaryObject() {
    return object;
  }

}
