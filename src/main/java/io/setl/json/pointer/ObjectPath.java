package io.setl.json.pointer;

import java.util.Objects;
import javax.annotation.Nonnull;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;
import io.setl.json.pointer.JsonExtendedPointer.ResultOfAdd;

/**
 * Part of a pointer path that goes through an object.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
class ObjectPath implements PathElement {

  protected final PathElement child;

  protected final String key;

  protected final String path;

  ObjectPath(String path, String key, PathElement child) {
    this.path = path;
    this.key = key;
    this.child = child;
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
  public void copy(JsonArray source, JsonArray target) {
    if (!"-".equals(key)) {
      // the key cannot exist in an array.
      return;
    }

    int s = source.size();
    while (target.size() < s) {
      target.add(null);
    }

    for (int i = 0; i < s; i++) {
      JsonValue sourceValue = source.get(i);
      JsonValue targetValue = target.get(i);
      switch (sourceValue.getValueType()) {
        case OBJECT:
          if (targetValue.getValueType() != ValueType.OBJECT) {
            targetValue = new CJObject();
            target.set(i, targetValue);
          }
          child.copy((JsonObject) sourceValue, (JsonObject) targetValue);
          break;

        case ARRAY:
          if (targetValue.getValueType() != ValueType.ARRAY) {
            targetValue = new CJArray();
            target.set(i, targetValue);
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
  public void copy(@Nonnull JsonObject source, @Nonnull JsonObject target) {
    JsonValue sourceValue = source.get(key);
    if (sourceValue == null) {
      return;
    }

    JsonValue targetValue = target.get(key);
    switch (sourceValue.getValueType()) {
      case OBJECT:
        if (targetValue == null || targetValue.getValueType() != ValueType.OBJECT) {
          targetValue = new CJObject();
          target.put(key, targetValue);
        }
        child.copy((JsonObject) sourceValue, (JsonObject) targetValue);
        break;

      case ARRAY:
        if (targetValue == null || targetValue.getValueType() != ValueType.ARRAY) {
          targetValue = new CJArray();
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


  protected JsonValue doOptValue(JsonValue jv) {
    switch (jv.getValueType()) {
      case OBJECT:
        return child.optValue((JsonObject) jv);
      case ARRAY:
        return child.optValue((JsonArray) jv);
      default:
        return null;
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


  protected ResultOfAdd doTestAdd(JsonValue jv) {
    switch (jv.getValueType()) {
      case OBJECT:
        return child.testAdd((JsonObject) jv);
      case ARRAY:
        return child.testAdd((JsonArray) jv);
      default:
        return ResultOfAdd.FAIL;
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
  public int getIndex() {
    return -1;
  }


  @Override
  public String getKey() {
    return key;
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
  public JsonValue optValue(JsonArray target) {
    return null;
  }


  @Override
  public JsonValue optValue(JsonObject target) {
    JsonValue jv = target.get(key);
    if (jv == null) {
      return null;
    }
    return doOptValue(jv);
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
  public ResultOfAdd testAdd(JsonArray target) {
    return ResultOfAdd.FAIL;
  }


  @Override
  public ResultOfAdd testAdd(JsonObject target) {
    JsonValue jv = target.get(key);
    if (jv == null) {
      return ResultOfAdd.FAIL;
    }
    return doTestAdd(jv);
  }


}
