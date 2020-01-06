package io.setl.json;

/**
 * Exception thrown when at attempt to retrieve a required datum from a JsonObject or JsonArray fails because the datum is missing.
 */
public class MissingItemException extends RequiredItemException {

  private static final long serialVersionUID = 1L;

  private final Type expected;

  private final int index;

  private final String key;


  /**
   * New instance.
   *
   * @param index    the index that was missing
   * @param expected the expected type
   */
  public MissingItemException(int index, Type expected) {
    super("Item at " + index + " was missing and should have had type " + expected);
    this.index = index;
    this.key = null;
    this.expected = expected;
  }


  /**
   * New instance.
   *
   * @param key      the key that was missing.
   * @param expected the type that was expected
   */
  public MissingItemException(String key, Type expected) {
    super("Item at " + Canonical.format(key) + " was missing and should have had type " + expected);
    this.index = -1;
    this.key = key;
    this.expected = expected;
  }


  /**
   * Get the missing item's expected type.
   *
   * @return the expected type
   */
  public Type getExpected() {
    return expected;
  }


  /**
   * Get the index where item ought to have been encountered.
   *
   * @return the index the index (or -1 if the item was in a JSON Object)
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
