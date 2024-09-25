package io.setl.json.patch.ops;

import java.util.Objects;
import javax.json.JsonPatch.Operation;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.CJObject;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.patch.PatchOperation;

/**
 * A "replace" operation.
 *
 * @author Simon Greatrix on 06/02/2020.
 */
public class Replace extends PatchOperation {

  private final JsonValue value;


  /**
   * New instance.
   *
   * @param path  the path to replace
   * @param value the value to replace with
   */
  @JsonCreator
  public Replace(
      @JsonProperty("path") String path,
      @JsonProperty("value") JsonValue value
  ) {
    super(path);
    this.value = value;
  }


  /**
   * New instance from the JSON representation.
   *
   * @param object the representation of the replace operation
   */
  public Replace(CJObject object) {
    super(object);
    value = object.getJsonValue("value");
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    return pointer.replace(target, value);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Replace)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    Replace replace = (Replace) o;

    return Objects.equals(value, replace.value);
  }


  @Override
  public Operation getOperation() {
    return Operation.REPLACE;
  }


  /**
   * Get the value to replace with.
   *
   * @return the value
   */
  public JsonValue getValue() {
    return value;
  }


  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (value != null ? value.hashCode() : 0);
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
