package io.setl.json.merge;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

/**
 * @author Simon Greatrix on 28/01/2020.
 */
public class MergeDiff {

  public static JMerge create(JsonValue input, JsonValue output) {
    if( input.getValueType() != ValueType.OBJECT || output.getValueType() != ValueType.OBJECT) {
      return new JMerge(output);
    }

    JsonObject inObject = (JsonObject) input;
    JsonObject outObject = (JsonObject) output;


    // TODO implement me!
    return null;
  }
}
