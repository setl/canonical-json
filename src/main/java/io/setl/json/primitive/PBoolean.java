package io.setl.json.primitive;

/**
 * Marker interface for the PTrue and PFalse primitives.
 *
 * @author Valerio Trigari, 23/03/2020
 */
public interface PBoolean {

  /**
   * Get the raw value of this primitive, which is always a Boolean.
   *
   * @return the boolean value of this
   */
  Boolean getValue();

}
