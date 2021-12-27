package io.setl.json.exception;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.json.JsonValue.ValueType;

import io.setl.json.primitive.CJString;

/**
 * Exception thrown when at attempt to retrieve a required datum from a JObject or JArray fails because the datum is missing. The javax.json API requires that
 * this extends from NullPointerException.
 */
public class MissingItemException extends NullPointerException {

  private static final String MESSAGE = "Item at %s was missing and should have had type %s";

  private static final long serialVersionUID = 1L;

  private final Set<ValueType> expected;

  private final int index;

  private final String key;


  /**
   * New instance.
   *
   * @param index    the index that was missing
   * @param expected the expected type
   */
  public MissingItemException(int index, ValueType expected) {
    super(String.format(MESSAGE, index, expected));
    this.index = index;
    key = null;
    this.expected = Collections.unmodifiableSet(EnumSet.of(expected));
  }


  /**
   * New instance.
   *
   * @param key      the key that was missing.
   * @param expected the type that was expected
   */
  public MissingItemException(String key, ValueType expected) {
    super(String.format(MESSAGE, key, expected));
    index = -1;
    this.key = key;
    this.expected = Collections.unmodifiableSet(EnumSet.of(expected));
  }


  /**
   * New instance.
   *
   * @param index    the index that was missing
   * @param expected the expected type
   */
  public MissingItemException(int index, Set<ValueType> expected) {
    super(String.format(MESSAGE, index, expected));
    this.index = index;
    key = null;
    this.expected = Collections.unmodifiableSet(EnumSet.copyOf(expected));
  }


  /**
   * New instance.
   *
   * @param key      the key that was missing.
   * @param expected the type that was expected
   */
  public MissingItemException(String key, Set<ValueType> expected) {
    super(String.format(MESSAGE, CJString.format(key), expected));
    index = -1;
    this.key = key;
    this.expected = Collections.unmodifiableSet(EnumSet.copyOf(expected));
  }


  /**
   * Get the missing item's expected type.
   *
   * @return the expected type
   */
  public Set<ValueType> getExpected() {
    return expected;
  }


  /**
   * Get the index where item ought to have been encountered.
   *
   * @return the index (or -1 if the item was in a JSON Object)
   */
  public int getIndex() {
    return index;
  }


  /**
   * Get the key where the item ought to have been encountered.
   *
   * @return the key (or null if the item was in a JSON Array)
   */
  public String getKey() {
    return key;
  }

}
