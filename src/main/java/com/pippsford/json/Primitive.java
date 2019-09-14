package com.pippsford.json;

public class Primitive {
  
  /** Common value for NULL. */
  public static final Primitive NULL = new Primitive(Type.NULL,null);
  /** Common value for true. */
  public static final Primitive TRUE = new Primitive(Type.BOOLEAN,Boolean.TRUE);
  /** Common value for false. */
  public static final Primitive FALSE = new Primitive(Type.BOOLEAN,Boolean.FALSE);
  

  /** The type represented by this primitive */
  final Type type;

  /** The encapsulated value */
  final Object value;


  /**
   * Create new primitive container.
   * 
   * @param type
   *          contained type
   * @param value
   *          contained value
   */
  public Primitive(Type type, Object value) {
    type.getType().cast(value);
    if( (type == Type.NULL) != (value == null) ) {
      throw new IllegalArgumentException("Null if and only if NULL");
    }
    this.type = type;
    this.value = value;
  }


  /**
   * Get the type encapsulated by this primitive
   * 
   * @return the type
   */
  public Type getType() {
    return type;
  }


  /**
   * Get the value encapsulated by this primitive
   * 
   * @return the value
   */
  public Object getValue() {
    return value;
  }


  /**
   * Get the value encapsulated by this primitive
   * 
   * @param <T>
   *          required type
   * @param reqType
   *          the required type
   * @param dflt
   *          default value if type is not correct
   * @return the value
   */
  public <T> T getValue(Class<T> reqType, T dflt) {
    if( reqType.isInstance(value) ) {
      return reqType.cast(value);
    }
    return dflt;
  }


  /**
   * Get the value encapsulated by this primitive. Throws a ClassCastException if the type is incorrect.
   * 
   * @param <T>
   *          required type
   * @param reqType
   *          the required type
   * @return the value
   */
  public <T> T getValueSafe(Class<T> reqType) {
    return reqType.cast(value);
  }


  @Override
  public String toString() {
    if( type == Type.STRING ) return Generator.escapeString((String) value);
    if( type == Type.NUMBER ) return Generator.escapeNumber((Number) value);
    return String.valueOf(value);
  }
}
