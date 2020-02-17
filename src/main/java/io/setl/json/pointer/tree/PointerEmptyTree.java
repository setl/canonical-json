package io.setl.json.pointer.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import io.setl.json.Primitive;
import io.setl.json.pointer.JsonExtendedPointer;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerEmptyTree implements PointerTree {

  /** The empty tree. */
  public static final PointerTree EMPTY = new PointerEmptyTree();


  private PointerEmptyTree() {
    // this is a singleton
  }


  @Override
  public boolean containsAll(JsonValue value) {
    return false;
  }


  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends JsonStructure> T copy(@Nonnull T value) {
    // Nothing can be copied, so return an empty structure
    switch (value.getValueType()) {
      case ARRAY:
        return (T) JsonValue.EMPTY_JSON_ARRAY;
      case OBJECT:
        return (T) JsonValue.EMPTY_JSON_OBJECT;
      default:
        // unreachable
        throw new IllegalArgumentException("Value of type " + value.getValueType() + " is not structure");
    }
  }


  @Override
  public boolean isParentOf(JsonExtendedPointer pointer) {
    return false;
  }


  @Nullable
  @Override
  @SuppressWarnings("unchecked")
  public <T extends JsonStructure> T remove(T value) {
    // Nothing will be removed, so just do a copy
    return (T) Primitive.cast(value).copy();
  }

}
