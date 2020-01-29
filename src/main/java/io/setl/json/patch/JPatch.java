package io.setl.json.patch;

import javax.json.JsonArray;
import javax.json.JsonPatch;
import javax.json.JsonStructure;

/**
 * @author Simon Greatrix on 28/01/2020.
 */
public class JPatch implements JsonPatch {

  @Override
  public <T extends JsonStructure> T apply(T target) {
    // TODO : Implement me! simongreatrix 28/01/2020
    return null;
  }


  @Override
  public JsonArray toJsonArray() {
    // TODO : Implement me! simongreatrix 28/01/2020
    return null;
  }
}
