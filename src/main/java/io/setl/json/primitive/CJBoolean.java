package io.setl.json.primitive;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Marker interface for the JTrue and JFalse Canonicals.
 *
 * @author Valerio Trigari, 23/03/2020
 */
// Referencing the TRUE and FALSE constants from this parent interface is definitely bad practice as it creates a circular dependency, but it has been deemed
// both useful and harmless in this case.
@SuppressFBWarnings("IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION")
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
