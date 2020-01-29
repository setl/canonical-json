package io.setl.json.patch;

import io.setl.json.Primitive;
import javax.json.JsonArray;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * Implementation of JSON Patch as defined in RFC-6902.
 *
 * @author Simon Greatrix on 28/01/2020.
 */
public class JPatch implements JsonPatch {

  JsonArray array;

  public JPatch(JsonArray patch) {
    // TODO : Implement me! simongreatrix 28/01/2020

  }

  public JPatch() {
    // TODO : Implement me! simongreatrix 28/01/2020

  }

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
