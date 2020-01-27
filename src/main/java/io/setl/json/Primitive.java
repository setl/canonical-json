package io.setl.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.numbers.PNumber;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
@JsonTypeInfo(use = Id.NAME)
@JsonSubTypes(
    {
        @Type(name = "ARRAY", value = JArray.class),
        @Type(name = "TRUE", value = PTrue.class),
        @Type(name = "FALSE", value = PFalse.class),
        @Type(name = "NULL", value = PNull.class),
        @Type(name = "OBJECT", value = JObject.class),
        @Type(name = "NUMBER", value = PNumber.class),
        @Type(name = "STRING", value = PString.class)
    }
)
public interface Primitive extends JsonValue {


  /**
   * Create a Primitive from a JsonValue.
   *
   * @param value the value
   *
   * @return the Primitive
   */
  static Primitive create(JsonValue value) {
    if (value == null) {
      return PNull.NULL;
    }
    if (value instanceof Primitive) {
      return (Primitive) value;
    }
    switch (value.getValueType()) {
      case ARRAY:
        return JArray.fixCollection(value.asJsonArray());
      case FALSE:
        return PFalse.FALSE;
      case NUMBER:
        return PNumber.create(((JsonNumber) value).numberValue());
      case NULL:
        return PNull.NULL;
      case OBJECT:
        return JObject.fixMap(value.asJsonObject());
      case STRING:
        return new PString(((JsonString) value).getString());
      case TRUE:
        return PTrue.TRUE;
      default:
        throw new IllegalArgumentException("Unknown Json Value type:" + value.getValueType());
    }
  }

  /**
   * Do a best effort conversion of any object to a Primitive.
   *
   * @param value the value
   *
   * @return the Primitive
   */
  static Primitive create(Object value) {
    if (value == null) {
      return PNull.NULL;
    }
    if (value instanceof Primitive) {
      return (Primitive) value;
    }
    if (value instanceof JsonValue) {
      return create((JsonValue) value);
    }
    if (value instanceof Boolean) {
      return ((Boolean) value) ? PTrue.TRUE : PFalse.FALSE;
    }
    if (value instanceof AtomicBoolean) {
      return ((AtomicBoolean) value).get() ? PTrue.TRUE : PFalse.FALSE;
    }
    if (value instanceof String) {
      return new PString((String) value);
    }
    if (value instanceof Number) {
      return PNumber.cast((Number) value);
    }
    if (value instanceof Collection<?>) {
      return JArray.fixCollection((Collection<?>) value);
    }
    if (value instanceof Map<?, ?>) {
      return JObject.fixMap((Map<?, ?>) value);
    }
    throw new IllegalArgumentException("Cannot include item of class " + value.getClass() + " in JSON");
  }

  /**
   * Convert any number to a BigDecimal.
   *
   * @param n the number
   *
   * @return the BigDecimal
   */
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
    if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte || n instanceof AtomicInteger || n instanceof AtomicLong) {
      return BigDecimal.valueOf(n.longValue());
    }
    if (n instanceof Double || n instanceof Float) {
      return BigDecimal.valueOf(n.doubleValue());
    }

    // unknown numeric type
    return new BigDecimal(n.toString());
  }

  /**
   * Convert any number to a BigInteger, possibly losing precision in the conversion.
   *
   * @param n the number
   *
   * @return the BigInteger
   */
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

  /**
   * Get a copy of this. If this is immutable, then returns this. Otherwise returns a deep copy.
   *
   * @return a copy of this
   */
  Primitive copy();

  /**
   * Get the type of this primitive.
   *
   * @return the type
   */
  JType getType();

  /**
   * Get the value encapsulated by this primitive.
   *
   * @return the value
   */
  Object getValue();

  /**
   * Get the value encapsulated by this primitive.
   *
   * @param <T>     required type
   * @param reqType the required type
   * @param dflt    default value if type is not correct
   *
   * @return the value
   */
  <T> T getValue(Class<T> reqType, T dflt);

  /**
   * Get the value encapsulated by this primitive. Throws a ClassCastException if the type is incorrect.
   *
   * @param <T>     required type
   * @param reqType the required type
   *
   * @return the value
   */
  <T> T getValueSafe(Class<T> reqType);

  /**
   * Write this to the specified writer.
   *
   * @param writer the writer
   *
   * @throws IOException if writing fails
   */
  void writeTo(Writer writer) throws IOException;
}
