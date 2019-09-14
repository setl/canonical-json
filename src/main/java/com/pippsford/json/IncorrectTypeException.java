package com.pippsford.json;

/**
 * An exception thrown when a type-checking accessor finds data of the wrong type.
 * 
 * @author Simon Greatrix
 *
 */
public class IncorrectTypeException extends RequiredItemException {
  private static final long serialVersionUID = 1L;

  private final int index;

  private final String key;

  private final Type required;

  private final Type actual;


  public IncorrectTypeException(int index, Type required, Type actual) {
    super("Item at " + index + " has type " + actual + ". Required " + required);
    this.index = index;
    this.key = null;
    this.required = required;
    this.actual = actual;
  }


  public IncorrectTypeException(String key, Type required, Type actual) {
    super("Item at " + Generator.escapeString(key) + " has type " + actual + ". Required " + required);
    this.index = -1;
    this.key = key;
    this.required = required;
    this.actual = actual;
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
  public Type getRequired() {
    return required;
  }


  /**
   * The actual type.
   * 
   * @return the actual type
   */
  public Type getActual() {
    return actual;
  }

}
