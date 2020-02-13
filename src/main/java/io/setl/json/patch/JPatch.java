package io.setl.json.patch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonPatch;
import javax.json.JsonStructure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.Primitive;
import io.setl.json.patch.ops.Add;
import io.setl.json.patch.ops.Copy;
import io.setl.json.patch.ops.Move;
import io.setl.json.patch.ops.Remove;
import io.setl.json.patch.ops.Replace;
import io.setl.json.patch.ops.Test;

/**
 * Implementation of JSON Patch as defined in RFC-6902.
 *
 * @author Simon Greatrix on 28/01/2020.
 */
public class JPatch implements JsonPatch {

  private final List<PatchOperation> operations;


  @JsonCreator
  public JPatch(@JsonProperty("operations") List<PatchOperation> operationList) {
    operations = new ArrayList<>(operationList);
  }


  /**
   * Create a new instance from its JSON representation.
   *
   * @param array the representation
   */
  public JPatch(JsonArray array) {
    int s = array.size();
    operations = new ArrayList<>(s);
    for (int i = 0; i < s; i++) {
      JObject jsonObject = JObject.asJObject(array.getJsonObject(i));
      String op = jsonObject.getString("op");
      switch (op) {
        case "add":
          operations.add(new Add(jsonObject));
          break;
        case "copy":
          operations.add(new Copy(jsonObject));
          break;
        case "move":
          operations.add(new Move(jsonObject));
          break;
        case "remove":
          operations.add(new Remove(jsonObject));
          break;
        case "replace":
          operations.add(new Replace(jsonObject));
          break;
        case "test":
          operations.add(new Test(jsonObject));
          break;
        default:
          throw new IllegalArgumentException("Unknown operation: \"" + op + "\"");
      }
    }
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    @SuppressWarnings("unchecked")
    T output = (T) Primitive.cast(target).copy();
    for (PatchOperation op : operations) {
      output = op.apply(output);
    }
    return output;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof JPatch)) {
      return false;
    }

    JPatch jPatch = (JPatch) o;
    return operations.equals(jPatch.operations);
  }


  public List<PatchOperation> getOperations() {
    return Collections.unmodifiableList(operations);
  }


  public int hashCode() {
    return operations.hashCode();
  }


  @Override
  public JsonArray toJsonArray() {
    JsonArray jsonArray = new JArray();
    for (PatchOperation op : operations) {
      jsonArray.add(op.toJsonObject());
    }
    return jsonArray;
  }


  public String toString() {
    return toJsonArray().toString();
  }

}
