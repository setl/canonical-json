package io.setl.json.pointer;

import javax.json.JsonArray;
import javax.json.JsonValue;

import io.setl.json.exception.PointerIndexException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ArrayPath extends ObjectPath {

  protected final int index;


  public ArrayPath(String path, String key, PathElement next) {
    super(path, key, next);
    index = Integer.parseInt(key);
  }


  public ArrayPath(int index) {
    super(Integer.toString(index));
    this.index = index;
  }

  @Override
  public String getEscapedKey() {
    return key;
  }

  @Override
  public int getIndex() {
    return index;
  }


  @Override
  public void add(JsonArray target, JsonValue value) {
    doAdd(get(target), value);
  }


  @Override
  public boolean containsValue(JsonArray target) {
    if (target.size() <= index) {
      return false;
    }
    return doContains(get(target));
  }


  private JsonValue get(JsonArray target) {
    try {
      return target.get(index);
    } catch (IndexOutOfBoundsException e) {
      throw new PointerIndexException("No such item", path, target.size());
    }
  }


  @Override
  public JsonValue getValue(JsonArray target) {
    return doGetValue(get(target));
  }


  @Override
  public void remove(JsonArray target) {
    doRemove(get(target));
  }


  @Override
  public void replace(JsonArray target, JsonValue value) {
    doReplace(get(target), value);
  }

}
