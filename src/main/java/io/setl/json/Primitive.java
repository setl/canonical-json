package io.setl.json;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.setl.json.jackson.PrimitiveSerializer;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.json.JsonValue;

/**
 * Representation of a value in a JSON object or array.
 */
@JsonSerialize(using = PrimitiveSerializer.class)
public class Primitive implements JValue, JsonValue {

  /** Common value for false. */
  public static final Primitive FALSE = new Primitive(JType.BOOLEAN, Boolean.FALSE);

  /** Common value for NULL. */
  public static final Primitive NULL = new Primitive(JType.NULL, null);

  /** Common value for true. */
  public static final Primitive TRUE = new Primitive(JType.BOOLEAN, Boolean.TRUE);


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
      return new Primitive(JType.STRING, value);
    }
    if (value instanceof Number) {
      return new Primitive(JType.NUMBER, value);
    }
    if (value instanceof JArray) {
      return new Primitive(JType.ARRAY, value);
    }
    if (value instanceof JObject) {
      return new Primitive(JType.OBJECT, value);
    }
    if (value instanceof Collection<?>) {
      return new Primitive(JType.ARRAY, JArray.fixCollection((Collection<?>) value));
    }
    if (value instanceof Map<?, ?>) {
      return new Primitive(JType.OBJECT, JObject.fixMap((Map<?, ?>) value));
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
  final JType type;

  /** The encapsulated value. */
  final Object value;


  /**
   * Create new primitive container.
   *
   * @param type  contained type
   * @param value contained value
   */
  public Primitive(JType type, Object value) {
    if ((type == JType.NULL) != (value == null)) {
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
  @Override
  public JType getType() {
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
  public ValueType getValueType() {
    switch (type) {
      case ARRAY:
        return ValueType.ARRAY;
      case BOOLEAN:
        return ((Boolean) value) ? ValueType.TRUE : ValueType.FALSE;
      case NULL:
        return ValueType.NULL;
      case NUMBER:
        return ValueType.NUMBER;
      case OBJECT:
        return ValueType.OBJECT;
      case STRING:
        return ValueType.STRING;
      default: // Raw JSON generated during serialization
        throw new IllegalStateException("Cannot get type of generated JSON");
    }
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
    switch (type) {
      case STRING:
        return Canonical.format((String) value);
      case NUMBER:
        return Canonical.format((Number) value);
      default:
        return String.valueOf(value);
    }
  }


  @Override
  public void writeTo(Writer writer) throws IOException {
    switch (type) {
      case STRING:
        Canonical.format(writer, (String) value);
        break;
      case NUMBER:
        Canonical.format(writer, (Number) value);
        break;
      default:
        writer.write(String.valueOf(value));
        break;
    }
  }
}
