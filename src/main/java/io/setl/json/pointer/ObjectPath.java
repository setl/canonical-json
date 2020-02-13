package io.setl.json.pointer;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ObjectPath implements PathElement {

  protected final String key;

  protected PathElement child;

  protected String path;


  /**
   * New path element that steps down through an object.
   *
   * @param path  the path for logging
   * @param key   the key in the object to select
   * @param child the descendants of this element
   */
  public ObjectPath(String path, String key, PathElement child) {
    this.path = path;
    this.key = key;
    this.child = child;
  }


  public ObjectPath(String key) {
    this.key = key;
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
  public void buildPath(StringBuilder builder) {
    builder.append('/').append(getEscapedKey());
    path = builder.toString();
    getChild().buildPath(builder);
  }


  @Override
  public boolean contains(PathElement other) {
    PathElement otherChild = other.getChild();
    if (otherChild == null) {
      // other is a terminal, this is not, so other is less specific than this
      return false;
    }

    if (other.isArrayType()) {
      // only matches if it is a wildcard match
      return "-".equals(key) && child.contains(otherChild);
    }

    // keys must match
    return Objects.equals(key, other.getKey()) && child.contains(otherChild);
  }


  @Override
  public boolean containsValue(JsonArray target) {
    return false;
  }


  @Override
  public boolean containsValue(JsonObject target) {
    return doContains(target.get(key));
  }


  @Override
  public void copy(@Nonnull JsonArray source, @Nullable JsonArray target) {
    if (!"-".equals(key)) {
      // the key cannot exist in an array.
      return;
    }

    int targetSize = target.size();
    for (int i = 0; i < source.size(); i++) {
      JsonValue sourceValue = source.get(i);
      JsonValue targetValue = (i < targetSize) ? target.get(i) : null;
      switch (sourceValue.getValueType()) {
        case OBJECT:
          if (targetValue == null || targetValue.getValueType() != ValueType.OBJECT) {
            targetValue = new JObject();
            if (i < targetSize) {
              target.set(i, targetValue);
            } else {
              target.add(targetValue);
            }
          }
          child.copy((JsonObject) sourceValue, (JsonObject) targetValue);
          break;

        case ARRAY:
          if (targetValue == null || targetValue.getValueType() != ValueType.ARRAY) {
            targetValue = new JArray();
            if (i < targetSize) {
              target.set(i, targetValue);
            } else {
              target.add(targetValue);
            }
          }
          child.copy((JsonArray) sourceValue, (JsonArray) targetValue);
          break;

        default:
          // do nothing
          break;
      }
    }
  }


  @Override
  public void copy(@Nonnull JsonObject source, @Nullable JsonObject target) {
    JsonValue sourceValue = source.get(key);
    if (sourceValue == null) {
      return;
    }

    JsonValue targetValue = target.get(key);
    switch (sourceValue.getValueType()) {
      case OBJECT:
        if (targetValue == null || targetValue.getValueType() != ValueType.OBJECT) {
          targetValue = new JObject();
          target.put(key, targetValue);
        }
        child.copy((JsonObject) sourceValue, (JsonObject) targetValue);
        break;

      case ARRAY:
        if (targetValue == null || targetValue.getValueType() != ValueType.ARRAY) {
          targetValue = new JArray();
          target.put(key, targetValue);
        }
        child.copy((JsonArray) sourceValue, (JsonArray) targetValue);
        break;

      default:
        // do nothing
        break;
    }
  }


  protected void doAdd(JsonValue jv, JsonValue value) {
    switch (jv.getValueType()) {
      case OBJECT:
        child.add((JsonObject) jv, value);
        break;
      case ARRAY:
        child.add((JsonArray) jv, value);
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
        return child.containsValue((JsonObject) jv);
      case ARRAY:
        return child.containsValue((JsonArray) jv);
      default:
        return false;
    }
  }


  protected JsonValue doGetValue(JsonValue jv) {
    switch (jv.getValueType()) {
      case OBJECT:
        return child.getValue((JsonObject) jv);
      case ARRAY:
        return child.getValue((JsonArray) jv);
      default:
        throw badPath(jv.getValueType());
    }
  }


  protected void doRemove(JsonValue jv) {
    switch (jv.getValueType()) {
      case OBJECT:
        child.remove((JsonObject) jv);
        break;
      case ARRAY:
        child.remove((JsonArray) jv);
        break;
      default:
        throw badPath(jv.getValueType());
    }
  }


  protected void doReplace(JsonValue jv, JsonValue value) {
    switch (jv.getValueType()) {
      case OBJECT:
        child.replace((JsonObject) jv, value);
        break;
      case ARRAY:
        child.replace((JsonArray) jv, value);
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
  public PathElement getChild() {
    return child;
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
    throw notObject();
  }


  @Override
  public JsonValue getValue(JsonObject target) {
    return doGetValue(get(target));
  }


  @Override
  public boolean isArrayType() {
    return false;
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


  @Override
  public void setChild(PathElement child) {
    this.child = child;
  }


  @Override
  public void setPath(String path) {
    this.path = path;
  }

}
