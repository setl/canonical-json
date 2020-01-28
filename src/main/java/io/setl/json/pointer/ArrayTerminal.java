package io.setl.json.pointer;

import io.setl.json.exception.PointerIndexException;
import javax.json.JsonArray;
import javax.json.JsonValue;

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
    return new PointerIndexException("Array index to large", path, size);
  }


  @Override
  public boolean containsValue(JsonArray target) {
    return index < target.size();
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
}
