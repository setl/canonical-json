package io.setl.json.pointer;

import java.util.Objects;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;

/**
 * A terminal entry in a path which references an object key.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class ObjectTerminal implements PathElement {

  protected final String key;

  protected String path;


  public ObjectTerminal(String path, String key) {
    this.path = path;
    this.key = key;
  }


  public ObjectTerminal(String key) {
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
  public void buildPath(StringBuilder builder) {
    builder.append('/').append(getEscapedKey());
    path = builder.toString();
    getChild().buildPath(builder);
  }


  @Override
  public boolean contains(PathElement other) {
    if (other.isArrayType()) {
      // only matches if it is a wildcard match
      return "-".equals(key);
    }

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
  public PathElement getChild() {
    return null;
  }


  @Override
  public String getEscapedKey() {
    return JPointer.escapeKey(key);
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
  public String getPath() {
    return path;
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
  public void setChild(PathElement child) {
    throw new UnsupportedOperationException();
  }


  @Override
  public void setPath(String path) {
    this.path = path;
  }


}
