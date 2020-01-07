package io.setl.json;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Primitive implements Writable {

  /** Common value for false. */
  public static final Primitive FALSE = new Primitive(Type.BOOLEAN, Boolean.FALSE);

  /** Common value for NULL. */
  public static final Primitive NULL = new Primitive(Type.NULL, null);

  /** Common value for true. */
  public static final Primitive TRUE = new Primitive(Type.BOOLEAN, Boolean.TRUE);


  /**
   * Do a best effort conversion of any object to a Primitive.
   *
   * @param value the value
   *
   * @return the Primitive
   */
  public static Primitive create(Object value) {
    if (value == null) {
      return NULL;
    }
    if (value instanceof Primitive) {
      return (Primitive) value;
    }
    if (value instanceof Boolean) {
      return ((Boolean) value).booleanValue() ? TRUE : FALSE;
    }
    if (value instanceof AtomicBoolean) {
      return ((AtomicBoolean) value).get() ? TRUE : FALSE;
    }
    if (value instanceof String) {
      return new Primitive(Type.STRING, value);
    }
    if (value instanceof Number) {
      return new Primitive(Type.NUMBER, value);
    }
    if (value instanceof JsonArray) {
      return new Primitive(Type.ARRAY, value);
    }
    if (value instanceof JsonObject) {
      return new Primitive(Type.OBJECT, value);
    }
    if (value instanceof Collection<?>) {
      return new Primitive(Type.ARRAY, JsonArray.fixCollection((Collection<?>) value));
    }
    if (value instanceof Map<?, ?>) {
      return new Primitive(Type.OBJECT, JsonObject.fixMap((Map<?, ?>) value));
    }
    throw new IllegalArgumentException("Cannot include item of class " + value.getClass() + " in JSON");
  }


  static BigDecimal toBigDecimal(Number n) {
    if (n == null) {
      return null;
    }
    if (n instanceof BigDecimal) {
      return (BigDecimal) n;
    }
    if (n instanceof BigInteger) {
      return new BigDecimal((BigInteger) n);
    }
    return new BigDecimal(n.toString());
  }


  static BigInteger toBigInteger(Number n) {
    if (n == null) {
      return null;
    }
    if (n instanceof BigInteger) {
      return (BigInteger) n;
    }
    if (n instanceof BigDecimal) {
      return ((BigDecimal) n).toBigInteger();
    }
    if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte) {
      return BigInteger.valueOf(n.longValue());
    }
    return new BigDecimal(n.toString()).toBigInteger();
  }


  /** The type represented by this primitive. */
  final Type type;

  /** The encapsulated value. */
  final Object value;


  /**
   * Create new primitive container.
   *
   * @param type  contained type
   * @param value contained value
   */
  public Primitive(Type type, Object value) {
    if ((type == Type.NULL) != (value == null)) {
      throw new IllegalArgumentException("Null if and only if NULL");
    }
    type.getType().cast(value);
    this.type = type;
    this.value = value;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Primitive other = (Primitive) obj;
    if (type != other.type) {
      return false;
    }
    if (value == null) {
      // Because of the type check, this should always be true
      return other.value == null;
    }
    return value.equals(other.value);
  }


  /**
   * Get the type encapsulated by this primitive.
   *
   * @return the type
   */
  public Type getType() {
    return type;
  }


  /**
   * Get the value encapsulated by this primitive.
   *
   * @param <T>     required type
   * @param reqType the required type
   * @param dflt    default value if type is not correct
   *
   * @return the value
   */
  public <T> T getValue(Class<T> reqType, T dflt) {
    if (reqType.isInstance(value)) {
      return reqType.cast(value);
    }
    return dflt;
  }


  /**
   * Get the value encapsulated by this primitive.
   *
   * @return the value
   */
  public Object getValue() {
    return value;
  }


  /**
   * Get the value encapsulated by this primitive. Throws a ClassCastException if the type is incorrect.
   *
   * @param <T>     required type
   * @param reqType the required type
   *
   * @return the value
   */
  public <T> T getValueSafe(Class<T> reqType) {
    return reqType.cast(value);
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + type.hashCode();
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }


  @Override
  public String toString() {
    if (type == Type.STRING) {
      return Canonical.format((String) value);
    }
    if (type == Type.NUMBER) {
      return Canonical.format((Number) value);
    }
    return String.valueOf(value);
  }


  @Override
  public void writeTo(Writer writer) throws IOException {
    if (type == Type.STRING) {
      Canonical.format(writer, (String) value);
    } else if (type == Type.NUMBER) {
      Canonical.format(writer, (Number) value);
    } else {
      writer.write(String.valueOf(value));
    }
  }
}
