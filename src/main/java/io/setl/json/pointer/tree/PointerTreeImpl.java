package io.setl.json.pointer.tree;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.Primitive;
import io.setl.json.pointer.JsonExtendedPointer;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerTreeImpl implements PointerTree {

  private final Filter filter;
  /** The pointers that make up this tree. */
  private final List<JsonExtendedPointer> pointers;


  PointerTreeImpl(List<JsonExtendedPointer> pointers) {
    this.pointers = pointers;
    filter = new FilterTree();
    for (JsonExtendedPointer p : pointers) {
      filter.add(p.getRoot());
    }
  }


  @Override
  public boolean containsAll(@Nonnull JsonValue value) {
    switch (value.getValueType()) {
      case OBJECT:
        return filter.containsAll((JsonObject) value);
      case ARRAY:
        return filter.containsAll((JsonArray) value);
      default:
        // Can only be true if this is a root tree and that has a special implementation
        return false;
    }
  }


  @Override
  @Nonnull
  @SuppressWarnings("unchecked")
  public <T extends JsonStructure> T copy(@Nonnull T value) {
    // Create an appropriate root
    T result;
    switch (value.getValueType()) {
      case ARRAY:
        result = (T) new JArray();
        break;
      case OBJECT:
        result = (T) new JObject();
        break;
      default:
        throw new IllegalArgumentException("Value of type " + value.getValueType() + " is not structure");
    }

    for (JsonExtendedPointer p : pointers) {
      result = p.copy(value, result);
    }
    return result;
  }


  @Override
  public boolean isParentOf(JsonExtendedPointer pointer) {
    for (JsonExtendedPointer p : pointers) {
      if (p.isParentOf(pointer)) {
        return true;
      }
    }
    return false;
  }


  @Nullable
  @Override
  public <T extends JsonStructure> T remove(T value) {
    @SuppressWarnings("unchecked")
    T target = (T) Primitive.cast(value).copy();
    for (JsonExtendedPointer p : pointers) {
      p.remove(target);
    }
    return target;
  }

}
