package com.pippsford.json;

public class MissingItemException extends RequiredItemException {
  private static final long serialVersionUID = 1L;

  private final int index;

  private final String key;

  private final Type expected;


  public MissingItemException(int index, Type expected) {
    super("Item at " + index + " was missing and should have had type " + expected);
    this.index = index;
    this.key = null;
    this.expected = expected;
  }


  public MissingItemException(String key, Type expected) {
    super("Item at " + Generator.escapeString(key) + " was missing and should have had type " + expected);
    this.index = -1;
    this.key = null;
    this.expected = expected;
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


  /**
   * Get the missing item's expected type.
   * 
   * @return the expected type
   */
  public Type getExpected() {
    return expected;
  }

}
