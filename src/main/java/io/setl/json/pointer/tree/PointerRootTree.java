package io.setl.json.pointer.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import io.setl.json.Primitive;
import io.setl.json.pointer.JsonExtendedPointer;

/**
 * A pointer tree that contains the empty pointer and hence has access to everything.
 *
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerRootTree implements PointerTree {

  /** The root tree. */
  public static final PointerTree ROOT = new PointerRootTree();


  private PointerRootTree() {
    // this is a singleton
  }


  @Override
  public boolean containsAll(JsonValue value) {
    return true;
  }


  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends JsonStructure> T copy(@Nonnull T value) {
    return (T) Primitive.cast(value).copy();
  }


  @Override
  public boolean isParentOf(JsonExtendedPointer pointer) {
    return true;
  }


  @Nullable
  @Override
  public <T extends JsonStructure> T remove(T value) {
    // Everything is removed.
    return null;
  }

}
