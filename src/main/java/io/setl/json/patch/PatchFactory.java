package io.setl.json.patch;

import java.util.Map;
import java.util.TreeSet;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import io.setl.json.JArray;
import io.setl.json.patch.key.Key;
import io.setl.json.patch.key.ObjectKey;

/**
 * @author Simon Greatrix on 28/01/2020.
 */
public class PatchFactory {

  public PatchFactory(Map<String, ?> config) {
    // there is no config
  }


  public PatchFactory() {
    // there is no config
  }


  public JsonPatch create(JsonStructure input, JsonStructure output) {
    // handle unlikely but simple case where the structure type changes.
    if (input.getValueType() != output.getValueType()) {
      return new JPatchBuilder()
          .replace("/", output)
          .build();
    }

    // We are going to do a lot of equality tests. We make them faster by optimising the storage.
    JArray jArray = new JArray();
    jArray.add(input);
    jArray.add(output);
    jArray.optimiseStorage();

    JsonStructure myInput = jArray.getPrimitive(0).getValueSafe(JsonStructure.class);
    JsonStructure myOutput = jArray.getPrimitive(1).getValueSafe(JsonStructure.class);

    JPatchBuilder builder = new JPatchBuilder();
    if (myInput.getValueType() == ValueType.ARRAY) {
      patchArray(builder, "/", null, (JsonArray) myInput, (JsonArray) myOutput);
    } else {
      patchObject(builder, "/", null, (JsonObject) myInput, (JsonObject) myOutput);
    }
    return builder.build();
  }


  private void patchArray(JPatchBuilder builder, String prefix, Key root, JsonArray myInput, JsonArray myOutput) {
    ArrayDiff arrayDiff = new ArrayDiff(builder, root, myInput, myOutput);
    arrayDiff.process();
  }


  private void patchObject(JPatchBuilder builder, String prefix, Key root, JsonObject myInput, JsonObject myOutput) {
    TreeSet<String> allKeys = new TreeSet<>(myInput.keySet());
    allKeys.addAll(myOutput.keySet());
    for (String key : allKeys) {
      JsonValue in = myInput.get(key);
      JsonValue out = myOutput.get(key);
      if (in == null) {
        // it is an add
        builder.add(prefix + key, out);
      } else if (out == null) {
        // it is a remove
        builder.remove(prefix + key);
      } else if (!in.equals(out)) {
        // it is changed
        if (in.getValueType() != out.getValueType()) {
          builder.replace(prefix + key, out);
        } else if (in.getValueType() == ValueType.ARRAY) {
          patchArray(builder, prefix + key + "/", new ObjectKey(root, key), (JsonArray) in, (JsonArray) out);
        } else if (in.getValueType() == ValueType.OBJECT) {
          patchObject(builder, prefix + key + "/", new ObjectKey(root, key), (JsonObject) in, (JsonObject) out);
        } else {
          builder.replace(prefix + key, out);
        }
      }
    }
  }

}
