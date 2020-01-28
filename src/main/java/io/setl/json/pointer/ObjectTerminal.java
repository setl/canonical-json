package io.setl.json.pointer;

import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

/**
 * A terminal entry in a path which references an object key.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
class ObjectTerminal implements PathElement {

  protected final String key;

  protected final String path;


  ObjectTerminal(String path, String key) {
    this.path = path;
    this.key = key;
  }


  @Override
  public void add(JsonArray target, JsonValue value) {
    throw needObject();
  }


  @Override
  public void add(JsonObject target, JsonValue value) {
    target.put(key, value);
  }


  @Override
  public boolean containsValue(JsonArray target) {
    throw needObject();
  }


  @Override
  public boolean containsValue(JsonObject target) {
    return target.containsKey(key);
  }


  @Override
  public JsonValue getValue(JsonArray target) {
    throw needObject();
  }


  @Override
  public JsonValue getValue(JsonObject target) {
    JsonValue jv = target.get(key);
    if (jv == null) {
      throw new NoSuchValueException(path);
    }
    return jv;
  }


  private PointerMismatchException needObject() {
    return new PointerMismatchException("JSON object required", path, ValueType.OBJECT, ValueType.ARRAY);
  }


  @Override
  public void remove(JsonArray target) {
    throw needObject();
  }


  @Override
  public void remove(JsonObject target) {
    JsonValue jv = target.remove(key);
    if (jv == null) {
      throw new NoSuchValueException(path);
    }
  }


  @Override
  public void replace(JsonArray target, JsonValue value) {
    throw needObject();
  }


  @Override
  public void replace(JsonObject target, JsonValue value) {
    JsonValue jv = target.put(key, value);
    if (jv == null) {
      throw new NoSuchValueException(path);
    }
  }
}
