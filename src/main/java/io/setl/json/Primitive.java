package io.setl.json;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import io.setl.json.exception.NotJsonException;
import io.setl.json.io.Utf8Appendable;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import io.setl.json.primitive.numbers.PNumber;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public interface Primitive extends JsonValue {

  /**
   * Test for whether a value is a boolean. The ValueType enumeration distinguishes between true and false, but there are times we want either.
   */
  Set<ValueType> IS_BOOLEAN = Collections.unmodifiableSet(EnumSet.of(ValueType.TRUE, ValueType.FALSE));

  /**
   * Create a Primitive from a JsonValue. If at all possible, the original object is returned.
   *
   * @param value the value
   *
   * @return the Primitive
   */
  static Primitive cast(JsonValue value) {
    if (value == null) {
      return PNull.NULL;
    }
    if (value instanceof Primitive) {
      return (Primitive) value;
    }
    switch (value.getValueType()) {
      case ARRAY:
        return JArray.asJArray(value.asJsonArray());
      case FALSE:
        return PFalse.FALSE;
      case NUMBER:
        return PNumber.castUnsafe(((JsonNumber) value).numberValue());
      case NULL:
        return PNull.NULL;
      case OBJECT:
        return JObject.asJObject(value.asJsonObject());
      case STRING:
        return PString.create(((JsonString) value).getString());
      case TRUE:
        return PTrue.TRUE;
      default:
        throw new IllegalArgumentException("Unknown Json Value type:" + value.getValueType());
    }
  }

  /**
   * Create a Primitive from a JsonValue. If at all possible, the original object is returned.
   *
   * @param value the value
   *
   * @return the Primitive
   */
  static Primitive cast(Object value) {
    if (value == null) {
      return PNull.NULL;
    }
    if (value instanceof Primitive) {
      return (Primitive) value;
    }
    if (value instanceof JsonValue) {
      return cast((JsonValue) value);
    }
    return create(value);
  }

  /**
   * Do a best effort conversion of any object to a Primitive, creating a new Primitive to represent the values where appropriate.
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
      return ((Primitive) value).copy();
    }
    if (value instanceof JsonValue) {
      // JsonValue but not a Primitive, so use "cast" to create a new Primitive
      return cast((JsonValue) value);
    }
    if (value instanceof Boolean) {
      return ((Boolean) value) ? PTrue.TRUE : PFalse.FALSE;
    }
    if (value instanceof AtomicBoolean) {
      return ((AtomicBoolean) value).get() ? PTrue.TRUE : PFalse.FALSE;
    }
    if (value instanceof String) {
      return PString.create((String) value);
    }
    if (value instanceof Number) {
      return PNumber.castUnsafe((Number) value);
    }
    if (value instanceof Collection<?>) {
      return JArray.asJArray((Collection<?>) value);
    }
    if (value instanceof Map<?, ?>) {
      return JObject.asJObject((Map<?, ?>) value);
    }
    throw new NotJsonException(value);
  }

  /**
   * Return an empty structure of the same type is the example.
   *
   * @param example the example
   * @param <T>     the desired type
   *
   * @return the empty structure
   */
  @SuppressWarnings("unchecked")
  static <T extends JsonStructure> T createEmpty(T example) {
    switch (example.getValueType()) {
      case ARRAY:
        return (T) JsonValue.EMPTY_JSON_ARRAY;
      case OBJECT:
        return (T) JsonValue.EMPTY_JSON_OBJECT;
      default:
        throw new IllegalArgumentException("Type " + example.getValueType() + " is not a structure");
    }
  }

  /**
   * Extract the contained value from a JsonValue.
   *
   * @param jv the JSON value
   *
   * @return the contained value
   */
  static Object getValue(JsonValue jv) {
    if (jv instanceof Primitive) {
      return ((Primitive) jv).getValue();
    }
    switch (jv.getValueType()) {
      case ARRAY:
      case OBJECT:
        return jv;
      case FALSE:
        return Boolean.FALSE;
      case NUMBER:
        return ((JsonNumber) jv).numberValue();
      case NULL:
        return null;
      case STRING:
        return ((JsonString) jv).getString();
      case TRUE:
        return Boolean.TRUE;
      default:
        throw new IllegalArgumentException("Unknown value type: " + jv.getValueType());
    }
  }

  /**
   * Get the value enclosed in a JSON value.
   *
   * @param reqType      the required type
   * @param jv           the JSON value
   * @param defaultValue the default value to return if the value is missing or not the correct type
   * @param <T>          the required type
   *
   * @return the value if possible, otherwise the default
   */
  static <T> T getValue(Class<T> reqType, JsonValue jv, T defaultValue) {
    Object value = getValue(jv);
    if (reqType.isInstance(value)) {
      return reqType.cast(value);
    }
    return defaultValue;
  }

  static <T> T getValue(Class<T> reqType, JsonValue jv) {
    return reqType.cast(getValue(jv));
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

  @Override
  default JArray asJsonArray() {
    return (JArray) this;
  }

  @Override
  default JObject asJsonObject() {
    return (JObject) this;
  }

  /**
   * Get a copy of this. If this is immutable, then returns this. Otherwise returns a deep copy.
   *
   * @return a copy of this
   */
  Primitive copy();

  /**
   * Get the value encapsulated by this primitive.
   *
   * @return the value
   */
  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  Object getValue();

  /**
   * Get the value encapsulated by this primitive.
   *
   * @param <T>          required type
   * @param reqType      the required type
   * @param defaultValue default value if type is not correct
   *
   * @return the value
   */
  <T> T getValue(Class<T> reqType, T defaultValue);

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
   * Write this to the specified stream in UTF-8.
   *
   * @param out the stream
   */
  default void writeTo(OutputStream out) throws IOException {
    Utf8Appendable utf8Appendable = new Utf8Appendable(out);
    writeTo(utf8Appendable);
    utf8Appendable.finish();
  }

  /**
   * Write this to the specified writer.
   *
   * @param writer the writer
   *
   * @throws IOException if writing fails
   */
  void writeTo(Appendable writer) throws IOException;

}
