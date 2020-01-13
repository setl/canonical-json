package io.setl.json;

/**
 * Enumeration of JSON types.
 */
public enum JType {
  /** A JSON [...] construct. */
  ARRAY(JArray.class),

  /** A true or a false. */
  BOOLEAN(Boolean.class),

  /** A block of canonical JSON. */
  JSON(String.class),

  /** A null. */
  NULL(Void.class),

  /** Any number. */
  NUMBER(Number.class),

  /** A JSON { ... } construct. */
  OBJECT(JObject.class),

  /** A string. */
  STRING(String.class);

  /** Class of associated encapsulated values. */
  private final Class<?> valueType;


  /**
   * Create type.
   *
   * @param valueType encapsulated value class
   */
  JType(Class<?> valueType) {
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
