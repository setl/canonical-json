package io.setl.json.pointer;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.exception.PointerIndexException;
import io.setl.json.pointer.JsonExtendedPointer.ResultOfAdd;

/**
 * Part of a pointer path that goes through an array.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
class ArrayPath extends ObjectPath {

  /** Index in an array. */
  protected final int index;


  ArrayPath(String path, String key, PathElement next) {
    super(path, key, next);
    index = Integer.parseInt(key);
  }


  @Override
  public void add(JsonArray target, JsonValue value) {
    doAdd(get(target), value);
  }


  @Override
  public boolean contains(PathElement other) {
    PathElement otherChild = other.getChild();
    if (otherChild == null) {
      // other is a terminal, this is not, so other is less specific than this
      return false;
    }

    // indices must match
    return index == other.getIndex() && child.contains(otherChild);
  }


  @Override
  public boolean containsValue(JsonArray target) {
    if (target.size() <= index) {
      return false;
    }
    return doContains(get(target));
  }


  @Override
  public void copy(JsonArray source, JsonArray target) {
    while (target.size() <= index) {
      target.add(null);
    }

    if (source.size() <= index) {
      // element does not exist in source
      return;
    }

    JsonValue sourceValue = source.get(index);
    JsonValue targetValue = target.get(index);
    switch (sourceValue.getValueType()) {
      case OBJECT:
        if (targetValue == null || targetValue.getValueType() != ValueType.OBJECT) {
          targetValue = new CJObject();
          target.set(index, targetValue);
        }
        child.copy((JsonObject) sourceValue, (JsonObject) targetValue);
        break;

      case ARRAY:
        if (targetValue == null || targetValue.getValueType() != ValueType.ARRAY) {
          targetValue = new CJArray();
          target.set(index, targetValue);
        }
        child.copy((JsonArray) sourceValue, (JsonArray) targetValue);
        break;

      default:
        // do nothing
        break;

    }
  }


  private JsonValue get(JsonArray target) {
    try {
      return target.get(index);
    } catch (IndexOutOfBoundsException e) {
      throw new PointerIndexException("No such item.", path, target.size());
    }
  }


  @Override
  public int getIndex() {
    return index;
  }


  @Override
  public JsonValue getValue(JsonArray target) {
    return doGetValue(get(target));
  }


  @Override
  public boolean isArrayType() {
    return true;
  }


  @Override
  public JsonValue optValue(JsonArray target) {
    if (0 <= index && index < target.size()) {
      return doOptValue(target.get(index));
    }
    return null;
  }


  @Override
  public void remove(JsonArray target) {
    doRemove(get(target));
  }


  @Override
  public void replace(JsonArray target, JsonValue value) {
    doReplace(get(target), value);
  }


  @Override
  public ResultOfAdd testAdd(JsonArray target) {
    if (0 <= index && index < target.size()) {
      return doTestAdd(target.get(index));
    }
    return ResultOfAdd.FAIL;
  }

}
