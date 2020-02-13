package io.setl.json.pointer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.exception.PointerMismatchException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class EmptyPointer implements JsonExtendedPointer {

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
  public boolean contains(JsonExtendedPointer other) {
    return true;
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
    if (target == null) {
      target = new JObject();
    }
    target.putAll(source);
    return target;
  }


  @Nonnull
  private JsonArray copy(@Nonnull JsonArray source, @Nullable JsonArray target) {
    if (target == null) {
      target = new JArray();
    }
    int s = Math.min(source.size(), target.size());
    for (int i = 0; i < s; i++) {
      target.set(i, source.get(i));
    }
    for (int i = s; i < source.size(); i++) {
      target.add(source.get(i));
    }
    return target;
  }


  @Override
  public String getPath() {
    return "";
  }


  @Override
  public JsonValue getValue(JsonStructure target) {
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

}
