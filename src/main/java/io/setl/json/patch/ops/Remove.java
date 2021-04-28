package io.setl.json.patch.ops;

import javax.json.JsonPatch.Operation;
import javax.json.JsonStructure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.CJObject;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.patch.PatchOperation;

/**
 * A "remove" operation.
 *
 * @author Simon Greatrix on 06/02/2020.
 */
public class Remove extends PatchOperation {

  @JsonCreator
  public Remove(@JsonProperty("path") String path) {
    super(path);
  }


  public Remove(CJObject object) {
    super(object);
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    return pointer.remove(target);
  }


  @Override
  public Operation getOperation() {
    return Operation.REMOVE;
  }


  @Override
  public CJObject toJsonObject() {
    return new ObjectBuilder()
        .add("op", getOperation().operationName())
        .add("path", getPath())
        .build();
  }

}
