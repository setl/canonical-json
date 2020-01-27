package io.setl.json.pointer;

import io.setl.json.exception.PointerIndexException;
import javax.json.JsonArray;
import javax.json.JsonValue;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
class ArrayPath extends ObjectPath {

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
  public boolean containsValue(JsonArray target) {
    if(target.size()<=index ) {
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
