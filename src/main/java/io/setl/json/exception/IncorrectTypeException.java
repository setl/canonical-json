package io.setl.json.exception;

import io.setl.json.Canonical;
import io.setl.json.JType;

/**
 * An exception thrown when a type-checking accessor finds data of the wrong type.
 *
 * @author Simon Greatrix
 */
public class IncorrectTypeException extends RequiredItemException {

  private static final long serialVersionUID = 1L;

  private final JType actual;

  private final int index;

  private final String key;

  private final JType required;


  /**
   * New instance.
   *
   * @param index    the array index that was accessed
   * @param required the required data type
   * @param actual   the actual data type
   */
  public IncorrectTypeException(int index, JType required, JType actual) {
    super("Item at " + index + " has type " + actual + ". Required " + required);
    this.index = index;
    this.key = null;
    this.required = required;
    this.actual = actual;
  }


  /**
   * New instance.
   *
   * @param key      the object key that was accessed
   * @param required the required data type
   * @param actual   the actual data type
   */
  public IncorrectTypeException(String key, JType required, JType actual) {
    super("Item at " + Canonical.format(key) + " has type " + actual + ". Required " + required);
    this.index = -1;
    this.key = key;
    this.required = required;
    this.actual = actual;
  }


  /**
   * The actual type.
   *
   * @return the actual type
   */
  public JType getActual() {
    return actual;
  }


  /**
   * The index in the array where the problem item was encountered.
   *
   * @return the index (or -1 if the item came from a JSON Object)
   */
  public int getIndex() {
    return index;
  }


  /**
   * The object key in the object where the problem item was encountered.
   *
   * @return the key (or null if the item came from a JSON Array)
   */
  public String getKey() {
    return key;
  }


  /**
   * The required type.
   *
   * @return the required type
   */
  public JType getRequired() {
    return required;
  }

}
