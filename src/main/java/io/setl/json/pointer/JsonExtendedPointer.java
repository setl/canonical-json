package io.setl.json.pointer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonPointer;
import javax.json.JsonStructure;

/**
 * Extensions to the standard pointer functionality.
 *
 * @author Simon Greatrix on 13/02/2020.
 */
public interface JsonExtendedPointer extends JsonPointer {

  /**
   * Checks if this pointer points at a parent structure of whatever the other pointer points at. The '-' end-of-array marker is treated as a wildcard in this
   * which matches any array index. Hence "/a/-/b" contains "/a/5/b".
   */
  boolean contains(JsonExtendedPointer other);

  /**
   * If the source contains a value for this pointer, copy it into the target. Existing sub-structures are over-written, not combined. Any required parent
   * structures are created in the target. The '-' end-of-array marker is treated as a wildcard in this which matches any array index.
   *
   * @param source the source structure
   * @param target the target, or null if it is to be created
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

}
