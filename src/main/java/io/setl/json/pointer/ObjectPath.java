package io.setl.json.pointer;

import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
class ObjectPath implements PathElement {

  protected final String key;

  protected final PathElement next;

  protected final String path;


  ObjectPath(String path, String key, PathElement next) {
    this.path = path;
    this.key = key;
    this.next = next;
  }


  @Override
  public void add(JsonArray target, JsonValue value) {
    throw notObject();
  }


  @Override
  public void add(JsonObject target, JsonValue value) {
    doAdd(get(target), value);
  }


  private PointerMismatchException badPath(ValueType actual) {
    return new PointerMismatchException("Path does not exist", path, actual);
  }


  @Override
  public boolean containsValue(JsonArray target) {
    return false;
  }


  @Override
  public boolean containsValue(JsonObject target) {
    return doContains(target.get(key));
  }


  protected void doAdd(JsonValue jv, JsonValue value) {
    switch (jv.getValueType()) {
      case OBJECT:
        next.add((JsonObject) jv, value);
        break;
      case ARRAY:
        next.add((JsonArray) jv, value);
        break;
      default:
        throw badPath(jv.getValueType());
    }
  }


  protected boolean doContains(JsonValue jv) {
    if (jv == null) {
      return false;
    }
    switch (jv.getValueType()) {
      case OBJECT:
        return next.containsValue((JsonObject) jv);
      case ARRAY:
        return next.containsValue((JsonArray) jv);
      default:
        return false;
    }
  }


  protected JsonValue doGetValue(JsonValue jv) {
    switch (jv.getValueType()) {
      case OBJECT:
        return next.getValue((JsonObject) jv);
      case ARRAY:
        return next.getValue((JsonArray) jv);
      default:
        throw badPath(jv.getValueType());
    }
  }


  protected void doRemove(JsonValue jv) {
    switch (jv.getValueType()) {
      case OBJECT:
        next.remove((JsonObject) jv);
        break;
      case ARRAY:
        next.remove((JsonArray) jv);
        break;
      default:
        throw badPath(jv.getValueType());
    }
  }


  protected void doReplace(JsonValue jv, JsonValue value) {
    switch (jv.getValueType()) {
      case OBJECT:
        next.replace((JsonObject) jv, value);
        break;
      case ARRAY:
        next.replace((JsonArray) jv, value);
        break;
      default:
        throw badPath(jv.getValueType());
    }
  }


  private JsonValue get(JsonObject target) {
    JsonValue jv = target.get(key);
    if (jv == null) {
      throw new NoSuchValueException(path);
    }
    return jv;
  }


  @Override
  public JsonValue getValue(JsonArray target) {
    throw notObject();
  }


  @Override
  public JsonValue getValue(JsonObject target) {
    return doGetValue(get(target));
  }


  private PointerMismatchException notObject() {
    return new PointerMismatchException("JSON object required", path, ValueType.OBJECT, ValueType.ARRAY);
  }


  @Override
  public void remove(JsonArray target) {
    throw notObject();
  }


  @Override
  public void remove(JsonObject target) {
    doRemove(get(target));
  }


  @Override
  public void replace(JsonArray target, JsonValue value) {
    throw notObject();
  }


  @Override
  public void replace(JsonObject target, JsonValue value) {
    doReplace(get(target), value);
  }
}
