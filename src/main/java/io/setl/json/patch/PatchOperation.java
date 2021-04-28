package io.setl.json.patch;

import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;
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
import io.setl.json.exception.InvalidPatchException;
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

  /**
   * Convert a JsonArray specifying patch operations to the actual operations.
   *
   * @param array array
   *
   * @return the operations
   */
  static List<PatchOperation> convert(JsonArray array) {
    if (array == null) {
      return List.of();
    }
    int s = array.size();
    List<PatchOperation> operations = new ArrayList<>(s);
    for (int i = 0; i < s; i++) {
      CJObject jsonObject = CJObject.asJObject(array.getJsonObject(i));
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
          throw new InvalidPatchException("Unknown operation: \"" + op + "\"");
      }
    }
    return operations;
  }


  protected final JsonExtendedPointer pointer;

  private final String path;


  protected PatchOperation(String path) {
    this.path = path;
    pointer = PointerFactory.create(path);
  }


  protected PatchOperation(JsonExtendedPointer pointer) {
    path = pointer.getPath();
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
