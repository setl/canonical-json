package io.setl.json.patch.ops;

import javax.json.JsonObject;
import javax.json.JsonPatch.Operation;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.JObject;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.patch.PatchOperation;
import io.setl.json.pointer.JPointerFactory;
import io.setl.json.pointer.JsonExtendedPointer;

/**
 * @author Simon Greatrix on 06/02/2020.
 */
public class Copy extends PatchOperation {

  private final String from;

  private final JsonExtendedPointer fromPointer;


  /**
   * Create a new copy operation.
   *
   * @param path the path to copy to
   * @param from the path to copy from
   */
  @JsonCreator
  public Copy(
      @JsonProperty("path") String path,
      @JsonProperty("from") String from
  ) {
    super(path);
    this.from = from;
    fromPointer = JPointerFactory.create(from);
  }


  /**
   * New instance from the JSON representation.
   *
   * @param object the representation of the copy operation
   */
  public Copy(JObject object) {
    super(object);
    this.from = object.getString("from");
    fromPointer = JPointerFactory.create(from);
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    JsonValue value = fromPointer.getValue(target);
    return pointer.add(target, value);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Copy)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    Copy copy = (Copy) o;
    return from.equals(copy.from);
  }


  public String getFrom() {
    return from;
  }


  @JsonIgnore
  public JsonExtendedPointer getFromPointer() {
    return fromPointer;
  }


  @Override
  public Operation getOperation() {
    return Operation.COPY;
  }


  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + from.hashCode();
    return result;
  }


  @Override
  public JsonObject toJsonObject() {
    return new JObjectBuilder()
        .add("op", getOperation().operationName())
        .add("path", getPath())
        .add("from", from)
        .build();
  }

}
