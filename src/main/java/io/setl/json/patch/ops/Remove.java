package io.setl.json.patch.ops;

import javax.json.JsonObject;
import javax.json.JsonStructure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.JObject;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.patch.PatchOperation;

/**
 * @author Simon Greatrix on 06/02/2020.
 */
public class Remove extends PatchOperation {

  @JsonCreator
  public Remove(@JsonProperty("path") String path) {
    super(path);
  }


  public Remove(JObject object) {
    super(object);
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    return pointer.remove(target);
  }


  @Override
  public String getOp() {
    return "remove";
  }


  @Override
  public JsonObject toJsonObject() {
    return new JObjectBuilder()
        .add("op", getOp())
        .add("path", getPath())
        .build();
  }

}
