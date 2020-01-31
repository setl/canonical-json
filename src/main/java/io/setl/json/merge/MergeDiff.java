package io.setl.json.merge;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

/**
 * @author Simon Greatrix on 28/01/2020.
 */
public class MergeDiff {

  /**
   * Create the "diff" of two values using a JSON Merge Patch.
   *
   * @param input  the input value
   * @param output the desired result of the patch
   *
   * @return the patch
   */
  public static JMerge create(JsonValue input, JsonValue output) {
    if (input.getValueType() != ValueType.OBJECT || output.getValueType() != ValueType.OBJECT) {
      return new JMerge(output);
    }

    JsonObject inObject = (JsonObject) input;
    JsonObject outObject = (JsonObject) output;

    // TODO implement me!
    return null;
  }
}
