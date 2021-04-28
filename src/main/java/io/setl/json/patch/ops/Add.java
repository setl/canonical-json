package io.setl.json.patch.ops;

import javax.json.JsonPatch.Operation;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.CJObject;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.patch.PatchOperation;

/**
 * @author Simon Greatrix on 06/02/2020.
 */
public class Add extends PatchOperation {

  private final JsonValue value;


  @JsonCreator
  public Add(
      @JsonProperty("path") String path,
      @JsonProperty("value") JsonValue value
  ) {
    super(path);
    this.value = value;
  }


  public Add(CJObject object) {
    super(object);
    value = object.getJsonValue("value");
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    return pointer.add(target, value);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Add)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    Add add = (Add) o;

    return value.equals(add.value);
  }


  @Override
  public Operation getOperation() {
    return Operation.ADD;
  }


  public JsonValue getValue() {
    return value;
  }


  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + value.hashCode();
    return result;
  }


  @Override
  public CJObject toJsonObject() {
    return new ObjectBuilder()
        .add("op", getOperation().operationName())
        .add("path", getPath())
        .add("value", getValue())
        .build();
  }

}
