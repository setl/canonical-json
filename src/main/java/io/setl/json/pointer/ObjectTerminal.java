package io.setl.json.pointer;

import java.util.Objects;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;
import io.setl.json.pointer.JsonExtendedPointer.ResultOfAdd;

/**
 * A terminal entry in a path which references an object key.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
class ObjectTerminal implements PathElement {

  protected final String key;

  protected final String path;


  public ObjectTerminal(String path, String key) {
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
  public boolean contains(PathElement other) {
    // keys must match
    return Objects.equals(key, other.getKey());
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
  public void copy(JsonArray source, JsonArray target) {
    // No way this can match.
  }


  @Override
  public void copy(JsonObject source, JsonObject target) {
    JsonValue value = source.get(key);
    if (value != null) {
      target.put(key, value);
    }
  }


  @Override
  public PathElement getChild() {
    return null;
  }


  @Override
  public int getIndex() {
    return -1;
  }


  @Override
  public String getKey() {
    return key;
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


  @Override
  public boolean isArrayType() {
    return false;
  }


  private PointerMismatchException needObject() {
    return new PointerMismatchException("JSON object required", path, ValueType.OBJECT, ValueType.ARRAY);
  }


  @Override
  public JsonValue optValue(JsonArray target) {
    return null;
  }


  @Override
  public JsonValue optValue(JsonObject target) {
    return target.get(key);
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


  @Override
  public ResultOfAdd testAdd(JsonArray target) {
    return ResultOfAdd.FAIL;
  }


  @Override
  public ResultOfAdd testAdd(JsonObject target) {
    return target.containsKey(key) ? ResultOfAdd.UPDATE : ResultOfAdd.CREATE;
  }

}
