package io.setl.json.pointer;

import javax.json.JsonArray;
import javax.json.JsonValue;

import io.setl.json.exception.PointerIndexException;

/**
 * Special handling for the '-' terminal.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class ExtraTerminal extends ObjectTerminal {

  public ExtraTerminal(String path) {
    super(path, "-");
  }


  @Override
  public void add(JsonArray target, JsonValue value) {
    target.add(value);
  }


  private PointerIndexException bad(int size) {
    return new PointerIndexException("Array index not yet created", path, size);
  }


  @Override
  public boolean containsValue(JsonArray target) {
    return false;
  }


  @Override
  public String getEscapedKey() {
    return "-";
  }


  @Override
  public int getIndex() {
    return -2;
  }


  @Override
  public JsonValue getValue(JsonArray target) {
    throw bad(target.size());
  }


  @Override
  public void remove(JsonArray target) {
    throw bad(target.size());
  }


  @Override
  public void replace(JsonArray target, JsonValue value) {
    throw bad(target.size());
  }

}
