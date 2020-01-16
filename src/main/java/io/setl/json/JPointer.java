package io.setl.json;

import javax.json.JsonPointer;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
public class JPointer implements JsonPointer {

  @Override
  public <T extends JsonStructure> T add(T target, JsonValue value) {
    // TODO : Implement me! simongreatrix 15/01/2020
    return null;
  }


  @Override
  public boolean containsValue(JsonStructure target) {
    // TODO : Implement me! simongreatrix 15/01/2020
    return false;
  }


  @Override
  public JsonValue getValue(JsonStructure target) {
    // TODO : Implement me! simongreatrix 15/01/2020
    return null;
  }


  @Override
  public <T extends JsonStructure> T remove(T target) {
    // TODO : Implement me! simongreatrix 15/01/2020
    return null;
  }


  @Override
  public <T extends JsonStructure> T replace(T target, JsonValue value) {
    // TODO : Implement me! simongreatrix 15/01/2020
    return null;
  }
}
