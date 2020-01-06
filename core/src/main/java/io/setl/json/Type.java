package io.setl.json;

/**
 * Enumeration of JSON types.
 */
public enum Type {
  /** A JSON [...] construct. */
  ARRAY(JsonArray.class),

  /** A true or a false. */
  BOOLEAN(Boolean.class),

  /** A null. */
  NULL(Void.class),

  /** Any number. */
  NUMBER(Number.class),

  /** A JSON { ... } construct. */
  OBJECT(JsonObject.class),

  /** A string. */
  STRING(String.class);

  /** Class of associated encapsulated values. */
  private final Class<?> type;


  /**
   * Create type.
   * 
   * @param type
   *          encapsulated value class
   */
  Type(Class<?> type) {
    this.type = type;
  }


  /**
   * Get encapsulated value class.
   * 
   * @return the type of the encapsulated value
   */
  public Class<?> getType() {
    return type;
  }
}
