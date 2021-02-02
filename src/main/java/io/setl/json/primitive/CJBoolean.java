package io.setl.json.primitive;

/**
 * Marker interface for the JTrue and JFalse Canonicals.
 *
 * @author Valerio Trigari, 23/03/2020
 */
public interface CJBoolean {

  /** Convenience reference to the FALSE singleton. */
  CJFalse FALSE = CJFalse.FALSE;

  /** Convenience reference to the FALSE singleton. */
  CJTrue TRUE = CJTrue.TRUE;

  /**
   * Get the raw value of this, which is always a Boolean.
   *
   * @return the boolean value of this
   */
  Boolean getValue();

}
