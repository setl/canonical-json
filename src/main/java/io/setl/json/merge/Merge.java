package io.setl.json.merge;

import java.util.Map.Entry;
import javax.json.JsonMergePatch;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import io.setl.json.CJObject;
import io.setl.json.Canonical;

/**
 * Implementation of a JSON merge patch. Merge patches only operate on JSON objects.
 *
 * @author Simon Greatrix on 28/01/2020.
 */
public class Merge implements JsonMergePatch {

  static JsonValue mergePatch(JsonValue target, JsonValue patch) {
    if (patch.getValueType() != ValueType.OBJECT) {
      return Canonical.create(patch);
    }
    JsonObject patchObject = (JsonObject) patch;

    JsonObject output;
    if (target == null || target.getValueType() != ValueType.OBJECT) {
      output = new CJObject();
    } else {
      output = (JsonObject) target;
    }

    for (Entry<String, JsonValue> entry : patchObject.entrySet()) {
      if (entry.getValue().getValueType() == ValueType.NULL) {
        output.remove(entry.getKey());
      } else {
        String key = entry.getKey();
        JsonValue current = output.get(key);
        output.put(key, mergePatch(current, entry.getValue()));
      }
    }
    return output;
  }


  private final Canonical patch;


  public Merge(JsonValue patch) {
    this.patch = Canonical.cast(patch).copy();
  }


  /**
   * Implements the MergePatch function from RFC-7396.
   *
   * @param target the target to apply this patch to.
   *
   * @return the new value
   */
  @Override
  public JsonValue apply(JsonValue target) {
    // Create a copy
    JsonValue copy = Canonical.create(target);

    // Apply the patch
    return mergePatch(copy, patch);
  }


  @Override
  public JsonValue toJsonValue() {
    return patch;
  }

}
