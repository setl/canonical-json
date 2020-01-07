package io.setl.json;

/**
 * Enumeration of JSON types.
 */
public enum Type {
  /** A JSON [...] construct. */
  ARRAY(JsonArray.class),

  /** A true or a false. */
  BOOLEAN(Boolean.class),

  /** A block of canonical JSON. */
  JSON(String.class),

  /** A null. */
  NULL(Void.class),

  /** Any number. */
  NUMBER(Number.class),

  /** A JSON { ... } construct. */
  OBJECT(JsonObject.class),

  /** A string. */
  STRING(String.class);

  /** Class of associated encapsulated values. */
  private final Class<?> valueType;


  /**
   * Create type.
   * 
   * @param valueType
   *          encapsulated value class
   */
  Type(Class<?> valueType) {
    this.valueType = valueType;
  }


  /**
   * Get encapsulated value class.
   * 
   * @return the type of the encapsulated value
   */
  public Class<?> getType() {
    return valueType;
  }
}
