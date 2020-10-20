package io.setl.json.patch.ops;

import java.util.Objects;
import javax.json.JsonObject;
import javax.json.JsonPatch.Operation;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.JObject;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.patch.PatchOperation;

/**
 * @author Simon Greatrix on 06/02/2020.
 */
public class Replace extends PatchOperation {

  private final JsonValue value;


  @JsonCreator
  public Replace(
      @JsonProperty("path") String path,
      @JsonProperty("value") JsonValue value
  ) {
    super(path);
    this.value = value;
  }


  public Replace(JObject object) {
    super(object);
    this.value = object.getJsonValue("value");
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    return pointer.replace(target, value);
  }


  @Override
  public Operation getOperation() {
    return Operation.REPLACE;
  }


  public JsonValue getValue() {
    return value;
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
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }


  @Override
  public JsonObject toJsonObject() {
    return new JObjectBuilder()
        .add("op", getOperation().operationName())
        .add("path", getPath())
        .add("value", getValue())
        .build();
  }

}
