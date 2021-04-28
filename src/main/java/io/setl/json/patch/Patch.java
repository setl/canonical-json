package io.setl.json.patch;

import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonPatch;
import javax.json.JsonStructure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.setl.json.CJArray;
import io.setl.json.Canonical;

/**
 * Implementation of JSON Patch as defined in RFC-6902.
 *
 * @author Simon Greatrix on 28/01/2020.
 */
public class Patch implements JsonPatch {

  private final List<PatchOperation> operations;


  @JsonCreator
  public Patch(List<PatchOperation> operationList) {
    operations = new ArrayList<>(operationList);
  }


  /**
   * Create a new instance from its JSON representation.
   *
   * @param array the representation
   */
  public Patch(JsonArray array) {
    operations = PatchOperation.convert(array);
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    @SuppressWarnings("unchecked")
    T output = (T) Canonical.cast(target).copy();
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
    if (!(o instanceof Patch)) {
      return false;
    }

    Patch patch = (Patch) o;
    return operations.equals(patch.operations);
  }


  /**
   * Get the operations that constitute this patch. Note that the patch may be manipulated by changing the values in this list.
   *
   * @return a mutable list of operations
   */
  @JsonValue
  public List<PatchOperation> getOperations() {
    return operations;
  }


  public int hashCode() {
    return operations.hashCode();
  }


  public String toCanonicalString() {
    return toJsonArray().toCanonicalString();
  }


  @Override
  public CJArray toJsonArray() {
    CJArray jsonArray = new CJArray();
    for (PatchOperation op : operations) {
      jsonArray.add(op.toJsonObject());
    }
    return jsonArray;
  }


  public String toPrettyString() {
    return toJsonArray().toPrettyString();
  }


  @Override
  public String toString() {
    return toJsonArray().toString();
  }

}
