package io.setl.json.pointer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.exception.PointerMismatchException;

/**
 * A pointer to the document root.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class EmptyPointer implements JsonExtendedPointer {

  /** The singleton instance. */
  public static final EmptyPointer INSTANCE = new EmptyPointer();


  private EmptyPointer() {
    // do nothing
  }


  @Override
  public <T extends JsonStructure> T add(T target, JsonValue value) {
    if (value.getValueType() != target.getValueType()) {
      throw new PointerMismatchException("Root structure type mismatch", "", target.getValueType(), value.getValueType());
    }
    // This could fail if two javax.json implementations were in use in the same JVM.
    @SuppressWarnings("unchecked")
    T output = (T) value;
    return output;
  }


  @Override
  public boolean containsValue(JsonStructure target) {
    return true;
  }


  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends JsonStructure> T copy(@Nonnull T source, @Nullable T target) {
    if (source instanceof JsonObject) {
      return (T) copy((JsonObject) source, (JsonObject) target);
    }
    return (T) copy((JsonArray) source, (JsonArray) target);
  }


  @Nonnull
  private JsonObject copy(@Nonnull JsonObject source, @Nullable JsonObject target) {
    if (target == null || target == JsonValue.EMPTY_JSON_OBJECT) {
      target = new CJObject();
    }
    target.putAll(source);
    return target;
  }


  @Nonnull
  private JsonArray copy(@Nonnull JsonArray source, @Nullable JsonArray target) {
    if (target == null || target == JsonValue.EMPTY_JSON_ARRAY) {
      target = new CJArray();
    }
    int s = source.size();
    while (target.size() < s) {
      target.add(null);
    }
    for (int i = 0; i < s; i++) {
      target.set(i, source.get(i));
    }
    return target;
  }


  @Override
  public String getPath() {
    return "";
  }


  @Override
  public PathElement getPathElement() {
    return null;
  }


  @Override
  public JsonValue getValue(JsonStructure target) {
    return target;
  }


  @Override
  public boolean isParentOf(JsonExtendedPointer other) {
    return true;
  }


  @Override
  public JsonValue optValue(JsonStructure target) {
    return target;
  }


  @Override
  public <T extends JsonStructure> T remove(T target) {
    throw new JsonException("Cannot remove root structure");
  }


  @Override
  public <T extends JsonStructure> T replace(T target, JsonValue value) {
    throw new JsonException("Cannot replace root structure");
  }


  @Override
  public ResultOfAdd testAdd(JsonStructure target) {
    return ResultOfAdd.UPDATE;
  }

}
