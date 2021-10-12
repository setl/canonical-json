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

import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.NotJsonException;
import io.setl.json.io.Utf8Appendable;
import io.setl.json.primitive.CJFalse;
import io.setl.json.primitive.CJNull;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.CJTrue;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * JSON values in canonical form.
 *
 * @author Simon Greatrix on 08/01/2020.
 */
@SuppressWarnings("OverloadMethodsDeclarationOrder")
public interface Canonical extends JsonValue {

  /**
   * Test for whether a value is a Boolean. The ValueType enumeration distinguishes between true and false, but there are times we want either.
   */
  Set<ValueType> IS_BOOLEAN = Collections.unmodifiableSet(EnumSet.of(ValueType.TRUE, ValueType.FALSE));

  /**
   * Set of structure types.
   */
  Set<ValueType> IS_STRUCTURE = Collections.unmodifiableSet(EnumSet.of(ValueType.OBJECT, ValueType.ARRAY));


  /**
   * Create a Canonical from a JsonValue. If at all possible, the original object is returned.
   *
   * @param value the value
   *
   * @return the Canonical
   */
  static Canonical cast(JsonValue value) {
    if (value == null) {
      return CJNull.NULL;
    }
    if (value instanceof Canonical) {
      return (Canonical) value;
    }
    switch (value.getValueType()) {
      case ARRAY:
        return CJArray.asArray(value.asJsonArray());
      case FALSE:
        return CJFalse.FALSE;
      case NUMBER:
        return CJNumber.castUnsafe(((JsonNumber) value).numberValue());
      case NULL:
        return CJNull.NULL;
      case OBJECT:
        return CJObject.asJObject(value.asJsonObject());
      case STRING:
        return CJString.create(((JsonString) value).getString());
      case TRUE:
        return CJTrue.TRUE;
      default:
        throw new NotJsonException("Unknown Json Value type:" + value.getValueType());
    }
  }

  /**
   * Create a Canonical from a JsonValue. If at all possible, the original object is returned.
   *
   * @param value the value
   *
   * @return the Canonical
   */
  static Canonical cast(Object value) {
    if (value == null) {
      return CJNull.NULL;
    }
    if (value instanceof Canonical) {
      return (Canonical) value;
    }
    if (value instanceof JsonValue) {
      return cast((JsonValue) value);
    }
    return create(value);
  }

  /**
   * Do the best effort conversion of any object to a Canonical, creating a new Primitive to represent the values where appropriate.
   *
   * @param value the value
   *
   * @return the Canonical
   */
  static Canonical create(Object value) {
    if (value == null) {
      return CJNull.NULL;
    }
    if (value instanceof Canonical) {
      return ((Canonical) value).copy();
    }
    if (value instanceof JsonValue) {
      // JsonValue but not a Canonical, so use "cast" to create a new Canonical
      return cast((JsonValue) value);
    }
    if (value instanceof Boolean) {
      return ((Boolean) value) ? CJTrue.TRUE : CJFalse.FALSE;
    }
    if (value instanceof AtomicBoolean) {
      return ((AtomicBoolean) value).get() ? CJTrue.TRUE : CJFalse.FALSE;
    }
    if (value instanceof String) {
      return CJString.create((String) value);
    }
    if (value instanceof Number) {
      return CJNumber.castUnsafe((Number) value);
    }
    if (value instanceof Collection<?>) {
      return CJArray.asArray((Collection<?>) value);
    }
    if (value instanceof Map<?, ?>) {
      return CJObject.asJObject((Map<?, ?>) value);
    }
    if (value.getClass().isArray()) {
      return CJArray.asArrayFromArray(value);
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
    if (example == null) {
      throw new IncorrectTypeException(IS_STRUCTURE, ValueType.NULL);
    }
    switch (example.getValueType()) {
      case ARRAY:
        return (T) JsonValue.EMPTY_JSON_ARRAY;
      case OBJECT:
        return (T) JsonValue.EMPTY_JSON_OBJECT;
      default:
        throw new IncorrectTypeException(IS_STRUCTURE, example.getValueType());
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
    if (jv instanceof Canonical) {
      return ((Canonical) jv).getValue();
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
   * Test if the value is an array.
   *
   * @param jv the value
   *
   * @return true if the value is not null and is an array
   */
  static boolean isArray(JsonValue jv) {
    return jv != null && jv.getValueType() == ValueType.ARRAY;
  }

  /**
   * Test if the value is a Boolean.
   *
   * @param jv the value
   *
   * @return true if the value is not null and is an array
   */
  static boolean isBoolean(JsonValue jv) {
    return (jv != null) && (jv.getValueType() == ValueType.TRUE || jv.getValueType() == ValueType.FALSE);
  }

  /**
   * Test if the value is a JSON null.
   *
   * @param jv the value
   *
   * @return true if the value is not null and is a JSON null
   */
  static boolean isNull(JsonValue jv) {
    return jv != null && jv.getValueType() == ValueType.NULL;
  }

  /**
   * Test if the value is a number.
   *
   * @param jv the value
   *
   * @return true if the value is not null and is a number
   */
  static boolean isNumber(JsonValue jv) {
    return jv != null && jv.getValueType() == ValueType.NUMBER;
  }

  /**
   * Test if the value is a JSON object.
   *
   * @param jv the value
   *
   * @return true if the value is not null and is an array
   */
  static boolean isObject(JsonValue jv) {
    return jv != null && jv.getValueType() == ValueType.OBJECT;
  }

  /**
   * Test if the value is a string.
   *
   * @param jv the value
   *
   * @return true if the value is not null and is an array
   */
  static boolean isString(JsonValue jv) {
    return jv != null && jv.getValueType() == ValueType.STRING;
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
   * Get the canonical JSON representation of the specified value.
   *
   * @param value the value
   *
   * @return the canonical JSON
   */
  static String toCanonicalString(Canonical value) {
    return (value != null) ? value.toCanonicalString() : CJNull.NULL.toCanonicalString();
  }

  /**
   * Get a pretty JSON representation of the specified value.
   *
   * @param value the value
   *
   * @return the pretty JSON
   */
  static String toPrettyString(Canonical value) {
    return (value != null) ? value.toPrettyString() : CJNull.NULL.toPrettyString();
  }

  @Override
  default CJArray asJsonArray() {
    try {
      return (CJArray) this;
    } catch (ClassCastException c) {
      IncorrectTypeException e = new IncorrectTypeException(ValueType.ARRAY, getValueType());
      e.initCause(c);
      throw e;
    }
  }

  @Override
  default CJObject asJsonObject() {
    try {
      return (CJObject) this;
    } catch (ClassCastException c) {
      IncorrectTypeException e = new IncorrectTypeException(ValueType.OBJECT, getValueType());
      e.initCause(c);
      throw e;
    }
  }

  /**
   * Get a copy of this. If this is immutable, then returns this. Otherwise, returns a deep copy.
   *
   * @return a copy of this
   */
  Canonical copy();

  /**
   * Get the value encapsulated by this instance.
   *
   * @return the value
   */
  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  Object getValue();

  /**
   * Get the value encapsulated by this instance.
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
   * Create the canonical textual JSON representation of this.
   *
   * @return the canonical JSON.
   */
  default String toCanonicalString() {
    return toString();
  }

  /**
   * Create the pretty JSON representation of this.
   *
   * @return the pretty JSON
   */
  default String toPrettyString() {
    return toString();
  }

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
