package io.setl.json.pointer;

import io.setl.json.exception.PointerIndexException;
import javax.json.JsonArray;
import javax.json.JsonValue;

/**
 * Special handling for the '-' terminal.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
class ExtraTerminal extends ObjectTerminal {

  ExtraTerminal(String path) {
    super(path, "-");
  }


  public void add(JsonArray target, JsonValue value) {
    target.add(value);
  }


  private PointerIndexException bad(int size) {
    return new PointerIndexException("Array index not yet created", path, size);
  }


  public boolean containsValue(JsonArray target) {
    return false;
  }


  public JsonValue getValue(JsonArray target) {
    throw bad(target.size());
  }


  public void remove(JsonArray target) {
    throw bad(target.size());
  }


  public void replace(JsonArray target, JsonValue value) {
    throw bad(target.size());
  }
}
