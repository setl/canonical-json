package io.setl.json.pointer.tree;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

import io.setl.json.Canonical;
import io.setl.json.pointer.JsonExtendedPointer;

/**
 * An empty tree of pointers.
 *
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerEmptyTree implements PointerTree {

  /** The empty tree. */
  public static final PointerTree INSTANCE = new PointerEmptyTree();


  private PointerEmptyTree() {
    // this is a singleton
  }


  @Override
  public boolean containsAll(JsonValue value) {
    return false;
  }


  @Override
  public <T extends JsonStructure> T copy(@Nonnull T value) {
    // Nothing can be copied, so return null
    return null;
  }


  @Override
  public List<JsonExtendedPointer> getPointers() {
    return Collections.emptyList();
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
    return (T) Canonical.cast(value).copy();
  }

}
