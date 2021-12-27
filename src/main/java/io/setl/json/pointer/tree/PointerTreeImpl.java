package io.setl.json.pointer.tree;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import io.setl.json.Canonical;
import io.setl.json.pointer.JsonExtendedPointer;

/**
 * An implementation of a Pointer Tree.
 *
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerTreeImpl implements PointerTree {

  /** The filter for checking containsAll. */
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
  @Nullable
  public <T extends JsonStructure> T copy(@Nonnull T value) {
    // Create an appropriate root
    T result = Canonical.createEmpty(value);
    for (JsonExtendedPointer p : pointers) {
      result = p.copy(value, result);
    }
    return result;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PointerTree)) {
      return false;
    }

    PointerTree that = (PointerTree) o;

    return pointers.equals(that.getPointers());
  }


  public List<JsonExtendedPointer> getPointers() {
    return Collections.unmodifiableList(pointers);
  }


  @Override
  public int hashCode() {
    return pointers.hashCode();
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
    T target = (T) Canonical.cast(value).copy();
    for (JsonExtendedPointer p : pointers) {
      if (p.containsValue(target)) {
        p.remove(target);
      }
    }
    return target;
  }

}
