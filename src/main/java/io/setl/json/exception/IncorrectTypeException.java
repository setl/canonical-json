package io.setl.json.exception;

import io.setl.json.primitive.PString;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.json.JsonValue.ValueType;

/**
 * An exception thrown when a type-checking accessor finds data of the wrong type. The javax.json API requires that this extends from ClassCastException.
 *
 * @author Simon Greatrix
 */
public class IncorrectTypeException extends ClassCastException {

  private static final long serialVersionUID = 1L;

  private final ValueType actual;

  private final int index;

  private final String key;

  private final Set<ValueType> required;


  /**
   * New instance.
   *
   * @param index    the array index that was accessed
   * @param required the required data type
   * @param actual   the actual data type
   */
  public IncorrectTypeException(int index, Set<ValueType> required, ValueType actual) {
    super("Item at " + index + " has type " + actual + ". Required " + required);
    this.index = index;
    this.key = null;
    this.required = Collections.unmodifiableSet(EnumSet.copyOf(required));
    this.actual = actual;
  }


  /**
   * New instance.
   *
   * @param key      the object key that was accessed
   * @param required the required data type
   * @param actual   the actual data type
   */
  public IncorrectTypeException(String key, Set<ValueType> required, ValueType actual) {
    super("Item at " + PString.format(key) + " has type " + actual + ". Required " + required);
    this.index = -1;
    this.key = key;
    this.required = Collections.unmodifiableSet(EnumSet.copyOf(required));
    this.actual = actual;
  }


  /**
   * New instance.
   *
   * @param index    the array index that was accessed
   * @param required the required data type
   * @param actual   the actual data type
   */
  public IncorrectTypeException(int index, ValueType required, ValueType actual) {
    super("Item at " + index + " has type " + actual + ". Required " + required);
    this.index = index;
    this.key = null;
    this.required = Collections.unmodifiableSet(EnumSet.of(required));
    this.actual = actual;
  }


  /**
   * New instance.
   *
   * @param key      the object key that was accessed
   * @param required the required data type
   * @param actual   the actual data type
   */
  public IncorrectTypeException(String key, ValueType required, ValueType actual) {
    super("Item at " + PString.format(key) + " has type " + actual + ". Required " + required);
    this.index = -1;
    this.key = key;
    this.required = Collections.unmodifiableSet(EnumSet.of(required));
    this.actual = actual;
  }


  /**
   * The actual type.
   *
   * @return the actual type
   */
  public ValueType getActual() {
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
  public Set<ValueType> getRequired() {
    return required;
  }

}
