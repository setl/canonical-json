package io.setl.json.pointer;

import javax.json.JsonArray;
import javax.json.JsonValue;

import io.setl.json.exception.PointerIndexException;
import io.setl.json.pointer.JsonExtendedPointer.ResultOfAdd;

/**
 * A terminal entry in a path which references an array index, or an object key which looks like a positive integer.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
class ArrayTerminal extends ObjectTerminal {

  private final int index;

  ArrayTerminal(String path, String key) {
    super(path, key);
    index = Integer.parseInt(key);
  }


  @Override
  public void add(JsonArray target, JsonValue value) {
    try {
      target.add(index, value);
    } catch (IndexOutOfBoundsException e) {
      throw badIndex(target.size());
    }
  }


  private PointerIndexException badIndex(int size) {
    return new PointerIndexException("Array index too large.", path, size);
  }


  @Override
  public boolean contains(PathElement other) {
    // indices must match
    return index == other.getIndex();
  }


  @Override
  public boolean containsValue(JsonArray target) {
    return index < target.size();
  }


  @Override
  public void copy(JsonArray source, JsonArray target) {
    while (target.size() <= index) {
      target.add(null);
    }
    if (source.size() <= index) {
      return;
    }

    JsonValue value = source.get(index);
    target.set(index, value);
  }


  @Override
  public int getIndex() {
    return index;
  }


  @Override
  public JsonValue getValue(JsonArray target) {
    try {
      return target.get(index);
    } catch (IndexOutOfBoundsException e) {
      throw badIndex(target.size());
    }
  }


  @Override
  public boolean isArrayType() {
    return true;
  }


  @Override
  public JsonValue optValue(JsonArray target) {
    if (0 <= index && index < target.size()) {
      return target.get(index);
    }
    return null;
  }


  @Override
  public void remove(JsonArray target) {
    try {
      target.remove(index);
    } catch (IndexOutOfBoundsException e) {
      throw badIndex(target.size());
    }
  }


  @Override
  public void replace(JsonArray target, JsonValue value) {
    try {
      target.set(index, value);
    } catch (IndexOutOfBoundsException e) {
      throw badIndex(target.size());
    }
  }


  @Override
  public ResultOfAdd testAdd(JsonArray target) {
    return (0 < index && index <= target.size()) ? ResultOfAdd.CREATE : ResultOfAdd.FAIL;
  }

}
