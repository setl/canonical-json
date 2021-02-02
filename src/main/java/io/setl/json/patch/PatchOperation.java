package io.setl.json.patch;

import javax.json.JsonPatch.Operation;
import javax.json.JsonStructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.setl.json.CJObject;
import io.setl.json.patch.ops.Add;
import io.setl.json.patch.ops.Copy;
import io.setl.json.patch.ops.Move;
import io.setl.json.patch.ops.Remove;
import io.setl.json.patch.ops.Replace;
import io.setl.json.patch.ops.Test;
import io.setl.json.pointer.JsonExtendedPointer;
import io.setl.json.pointer.PointerFactory;

/**
 * @author Simon Greatrix on 06/02/2020.
 */
@JsonTypeInfo(
    use = Id.NAME,
    include = As.EXISTING_PROPERTY,
    property = "op"
)
@JsonSubTypes({
    @Type(name = "add", value = Add.class),
    @Type(name = "copy", value = Copy.class),
    @Type(name = "move", value = Move.class),
    @Type(name = "remove", value = Remove.class),
    @Type(name = "replace", value = Replace.class),
    @Type(name = "test", value = Test.class)
})
public abstract class PatchOperation {

  protected final JsonExtendedPointer pointer;

  private final String path;


  protected PatchOperation(String path) {
    this.path = path;
    pointer = PointerFactory.create(path);
  }


  protected PatchOperation(JsonExtendedPointer pointer) {
    this.path = pointer.getPath();
    this.pointer = pointer;
  }


  protected PatchOperation(CJObject object) {
    this(object.getString("path"));
  }


  public abstract <T extends JsonStructure> T apply(T target);


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PatchOperation)) {
      return false;
    }

    PatchOperation operation = (PatchOperation) o;
    return path.equals(operation.path);
  }


  @JsonProperty("op")
  public String getOp() {
    return getOperation().operationName();
  }


  @JsonIgnore
  public abstract Operation getOperation();


  public String getPath() {
    return path;
  }


  @JsonIgnore
  public JsonExtendedPointer getPathPointer() {
    return pointer;
  }


  @Override
  public int hashCode() {
    return path.hashCode();
  }


  public String toCanonicalString() {
    return toJsonObject().toCanonicalString();
  }


  public abstract CJObject toJsonObject();


  public String toPrettyString() {
    return toJsonObject().toPrettyString();
  }


  @Override
  public String toString() {
    return toJsonObject().toString();
  }

}
