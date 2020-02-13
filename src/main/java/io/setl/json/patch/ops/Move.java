package io.setl.json.patch.ops;

import javax.json.JsonObject;
import javax.json.JsonPatch.Operation;
import javax.json.JsonPointer;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.JObject;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.patch.PatchOperation;
import io.setl.json.pointer.JPointerFactory;

/**
 * @author Simon Greatrix on 06/02/2020.
 */
public class Move extends PatchOperation {

  private final String from;

  private final JsonPointer fromPointer;


  /**
   * New instance.
   *
   * @param path the path to move to
   * @param from the path to move from
   */
  @JsonCreator
  public Move(
      @JsonProperty("path") String path,
      @JsonProperty("from") String from
  ) {
    super(path);
    this.from = from;
    fromPointer = JPointerFactory.create(from);
  }


  /**
   * New instance from its JSON representation.
   *
   * @param object the representation
   */
  public Move(JObject object) {
    super(object);
    this.from = object.getString("from");
    fromPointer = JPointerFactory.create(from);
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    JsonValue value = fromPointer.getValue(target);
    target = fromPointer.remove(target);
    return pointer.add(target, value);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Move)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    Move move = (Move) o;
    return from.equals(move.from);
  }


  public String getFrom() {
    return from;
  }


  @Override
  public Operation getOperation() {
    return Operation.MOVE;
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
