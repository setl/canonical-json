package io.setl.json.pointer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonPointer;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * Extensions to the standard pointer functionality.
 *
 * @author Simon Greatrix on 13/02/2020.
 */
public interface JsonExtendedPointer extends JsonPointer {

  /** The result of an add operation. The add operation will always create items in an array, but may create or update them in an object. */
  enum ResultOfAdd {
    /** The operation will create the item. */
    CREATE,
    /** The operation will update the item. */
    UPDATE,
    /** The operation will fail. */
    FAIL
  }


  /**
   * If the source contains a value for this pointer, copy it into the target. Existing sub-structures are over-written, not combined. Any required parent
   * structures are created in the target. The '-' end-of-array marker is treated as a wildcard in this which matches any array index.
   *
   * @param source the source structure
   * @param target the target, or null if it is to be created
   * @param <T>    the structure type
   *
   * @return the updated (or created) target
   */
  @Nonnull
  <T extends JsonStructure> T copy(@Nonnull T source, @Nullable T target);


  /**
   * Get the path representing this JsonPointer.
   *
   * @return the path
   */
  String getPath();


  /**
   * Get the root path element. The empty path has no root element.
   *
   * @return the root element
   */
  @Nullable
  PathElement getPathElement();


  /**
   * Checks if this pointer points at a parent structure of whatever the other pointer points at. The '-' end-of-array marker is treated as a wildcard in this
   * which matches any array index. Hence, "/a/-/b" contains "/a/5/b".
   *
   * @param other the other pointer to compare to this
   *
   * @return true if this is the parent of the other pointer
   */
  boolean isParentOf(JsonExtendedPointer other);


  /**
   * Returns the value at the referenced location in the specified {@code target}, or null if there is no such value.
   *
   * @param target the target referenced by this {@code JsonPointer}
   *
   * @return the referenced value in the target, or null
   *
   * @throws NullPointerException if {@code target} is null
   */
  JsonValue optValue(JsonStructure target);


  /**
   * Test what an add operation to this pointer would do to the target.
   *
   * @param target the target
   *
   * @return the result
   */
  ResultOfAdd testAdd(JsonStructure target);

}
