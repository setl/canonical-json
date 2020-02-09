package io.setl.json.patch.ops;

import javax.json.JsonObject;
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
  public String getOp() {
    return "replace";
  }


  public JsonValue getValue() {
    return value;
  }


  @Override
  public JsonObject toJsonObject() {
    return new JObjectBuilder()
        .add("op", getOp())
        .add("path", getPath())
        .add("value", getValue())
        .build();
  }

}
