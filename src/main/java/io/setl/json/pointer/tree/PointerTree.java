package io.setl.json.pointer.tree;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import io.setl.json.pointer.JsonExtendedPointer;

/**
 * A collection of pointers.
 *
 * @author Simon Greatrix on 17/02/2020.
 */
public interface PointerTree {

  /**
   * Check if this tree of pointers contains pointers to all parts of the specified value.
   *
   * @param value the value to check
   *
   * @return true if the value is fully accessible
   */
  boolean containsAll(@Nonnull JsonValue value);


  /**
   * Copy as much of the provided value as this tree of pointers can access.
   *
   * @param source the value to copy
   * @param <T>    the type of the structure to copy
   *
   * @return the copy
   */
  @Nullable
  <T extends JsonStructure> T copy(@Nonnull T source);


  /**
   * Get the pointers that were incorporated into this tree.
   *
   * @return the pointers
   */
  List<JsonExtendedPointer> getPointers();


  /**
   * Check if this tree contains a parent of the specified pointer.
   *
   * @param pointer the pointer to check
   *
   * @return true if the pointer has a parent in this tree
   */
  boolean isParentOf(JsonExtendedPointer pointer);


  /**
   * Create a copy of the value with everything this tree can access removed.
   *
   * @param value the value to copy and filter
   * @param <T>   the structure type
   *
   * @return the filtered copy, or null if the root was removed.
   */
  @Nullable
  <T extends JsonStructure> T remove(T value);

}
