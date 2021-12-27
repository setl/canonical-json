package io.setl.json.patch;

/**
 * Feature specification for the Diff engine.
 */
public enum DiffFeatures {

  /**
   * Try to create "move" operations. (Not yet implemented)
   */
  CREATE_MOVES,

  /**
   * Try to create "copy" operations. (Not yet implemented)
   */
  CREATE_COPIES,

  /**
   * This flag instructs the diff generator to emit "test" operations that check a value before removing or replacing it. In principle, this allows the patch to
   * be reversed.
   *
   * <p>The resulting patches are standard per RFC 6902 and should be processed correctly by any compliant library; due to the associated space and performance
   * costs, however, this isn't default behavior.
   */
  EMIT_TESTS,

  /**
   * This flag instructs the diff generator to emit "test" operations that validate the state of the entire source document against a cryptographic digest
   * before applying any mutations. This can be useful if you want to ensure data integrity prior to applying the patch.
   *
   * <p>The resulting patches are NOT standard per RFC 6902 and can only be processed by a library that understand canonical form and the "digest" extension.
   * This isn't default behavior.
   */
  EMIT_DIGEST


}
