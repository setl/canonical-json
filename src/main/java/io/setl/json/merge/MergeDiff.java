package io.setl.json.merge;

import java.util.HashSet;
import java.util.Iterator;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import io.setl.json.CJObject;

/**
 * Generator of merge patches.
 *
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
  public static Merge create(JsonValue input, JsonValue output) {
    if (input.getValueType() != ValueType.OBJECT || output.getValueType() != ValueType.OBJECT) {
      return new Merge(output);
    }

    JsonObject merge = new CJObject();
    JsonObject inObject = (JsonObject) input;
    JsonObject outObject = (JsonObject) output;

    doDiff(merge, inObject, outObject);

    return new Merge(merge);
  }


  private static void doDiff(JsonObject merge, JsonObject inObject, JsonObject outObject) {
    HashSet<String> inKeys = new HashSet<>(inObject.keySet());
    HashSet<String> outKeys = new HashSet<>(outObject.keySet());

    // do removes
    for (String s : inKeys) {
      if (!outKeys.contains(s)) {
        merge.put(s, JsonValue.NULL);
      }
    }

    // do adds, and trim outKeys so it is just the common keys.
    Iterator<String> iterator = outKeys.iterator();
    while (iterator.hasNext()) {
      String s = iterator.next();
      if (!inKeys.contains(s)) {
        merge.put(s, outObject.get(s));
        iterator.remove();
      }
    }

    // do replaces
    for (String s : outKeys) {
      JsonValue inValue = inObject.get(s);
      JsonValue outValue = outObject.get(s);

      if (inValue.getValueType() == ValueType.OBJECT && outValue.getValueType() == ValueType.OBJECT) {
        CJObject childMerge = new CJObject();
        doDiff(childMerge, (JsonObject) inValue, (JsonObject) outValue);
        if (!childMerge.isEmpty()) {
          merge.put(s, childMerge);
        }
        continue;
      }

      if ((inValue.getValueType() != outValue.getValueType()) || !inValue.equals(outValue)) {
        merge.put(s, outValue);
      }
    }
  }

}
