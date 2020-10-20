package io.setl.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import io.setl.json.primitive.numbers.PNumber;

public abstract class JObject implements JsonObject, Primitive {

  /**
   * Convert any map into a JObject.
   *
   * @param map the map to convert
   *
   * @return the equivalent JObject
   */
  public static JObject asJObject(Map<?, ?> map) {
    if (map instanceof JObject) {
      return (JObject) map;
    }

    JObject out = new JCanonicalObject();
    for (Entry<?, ?> entry : map.entrySet()) {
      Object key = entry.getKey();
      if (key == null) {
        throw new IllegalArgumentException("Map keys must not be null");
      }
      if (!(key instanceof String)) {
        throw new IllegalArgumentException("Map keys must be Strings, not " + key.getClass());
      }

      Object value = entry.getValue();
      Primitive primitive = Primitive.create(value);
      out.put((String) key, primitive);
    }
    return out;
  }


  @Override
  public JObject asJsonObject() {
    return this;
  }


  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract void forEach(BiConsumer<? super String, ? super JsonValue> action);


  /**
   * Get an array from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the array, or the default
   */
  public JArray getArray(String key, @Nonnull Function<String, JArray> defaultValue) {
    return getQuiet(JArray.class, key, defaultValue);
  }


  /**
   * Get an array from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the array, or the default
   */
  @Nonnull
  public JArray getArray(String key, @Nonnull JArray defaultValue) {
    return getQuiet(JArray.class, key, defaultValue);
  }


  /**
   * Get an array from the object.
   *
   * @return the array
   */
  @Nonnull
  public JArray getArray(String key) {
    return getSafe(JArray.class, ValueType.ARRAY, key);
  }


  /**
   * Get a big decimal from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the big decimal, or the default
   */
  @Nonnull
  public BigDecimal getBigDecimal(String key, @Nonnull BigDecimal defaultValue) {
    Number n = getQuiet(Number.class, key);
    if (n == null) {
      return defaultValue;
    }
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(String key, @Nonnull Function<String, BigDecimal> defaultValue) {
    Number n = getQuiet(Number.class, key);
    if (n == null) {
      return defaultValue.apply(key);
    }
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the object.
   *
   * @return the big decimal
   */
  @Nonnull
  public BigDecimal getBigDecimal(String key) {
    Number n = getSafe(Number.class, ValueType.NUMBER, key);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the big integer, or the default
   */
  @Nonnull
  public BigInteger getBigInteger(String key, @Nonnull BigInteger defaultValue) {
    Number n = getQuiet(Number.class, key);
    if (n == null) {
      return defaultValue;
    }
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(String key, @Nonnull Function<String, BigInteger> defaultValue) {
    Number n = getQuiet(Number.class, key);
    if (n == null) {
      return defaultValue.apply(key);
    }
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @return the big integer
   */
  @Nonnull
  public BigInteger getBigInteger(String key) {
    Number n = getSafe(Number.class, ValueType.NUMBER, key);
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a Boolean from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    return getQuiet(Boolean.class, key, defaultValue);
  }


  /**
   * Get a Boolean from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(String key, @Nonnull Predicate<String> defaultValue) {
    Boolean value = getQuiet(Boolean.class, key);
    return (value != null) ? value : defaultValue.test(key);
  }


  /**
   * Get a Boolean from the object.
   *
   * @return the Boolean
   */
  public boolean getBoolean(String key) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      throw new MissingItemException(key, Primitive.IS_BOOLEAN);
    }
    Object value = primitive.getValue();
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    throw new IncorrectTypeException(key, Primitive.IS_BOOLEAN, primitive.getValueType());
  }


  /**
   * Get a double from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the double, or the default
   */
  public double getDouble(String key, double defaultValue) {
    Double n = optDouble(key);
    return (n != null) ? n : defaultValue;
  }


  /**
   * Get a double from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the double, or the default
   */
  public double getDouble(String key, @Nonnull ToDoubleFunction<String> defaultValue) {
    Double n = optDouble(key);
    return (n != null) ? n : defaultValue.applyAsDouble(key);
  }


  /**
   * Get a double from the object.
   *
   * @return the double
   */
  public double getDouble(String key) {
    Primitive p = getPrimitive(key);
    if (p == null) {
      throw new MissingItemException(key, ValueType.NUMBER);
    }
    Double n = optDouble(key);
    if (n == null) {
      throw new IncorrectTypeException(key, ValueType.NUMBER, p.getValueType());
    }
    return n;
  }


  /**
   * Get an integer from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the integer, or the default
   */
  public int getInt(String key, int defaultValue) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.intValue() : defaultValue;
  }


  /**
   * Get an integer from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the integer, or the default
   */
  public int getInt(String key, @Nonnull ToIntFunction<String> defaultValue) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.intValue() : defaultValue.applyAsInt(key);
  }


  /**
   * Get an integer from the object.
   *
   * @return the integer
   */
  @Override
  public int getInt(String key) {
    return getSafe(Number.class, ValueType.NUMBER, key).intValue();
  }


  @Override
  public JsonArray getJsonArray(String name) {
    return optArray(name);
  }


  @Override
  public JsonNumber getJsonNumber(String name) {
    Primitive p = getPrimitive(name);
    return (p != null) ? (PNumber) p : null;
  }


  @Override
  public JsonObject getJsonObject(String name) {
    return optObject(name);
  }


  @Override
  public JsonString getJsonString(String name) {
    Primitive p = getPrimitive(name);
    return (p != null) ? (PString) p : null;
  }


  /**
   * Get a JsonValue from this object. The value must exist.
   *
   * @param key the value's key
   *
   * @return the value
   */
  public JsonValue getJsonValue(String key) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      throw new MissingItemException(key, EnumSet.allOf(ValueType.class));
    }
    return primitive;
  }


  /**
   * Get a JsonValue from this object. If the value does not exist, the default value is returned.
   *
   * @param key          the value's key
   * @param defaultValue the default value
   *
   * @return the value
   */
  public JsonValue getJsonValue(String key, JsonValue defaultValue) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      return defaultValue;
    }
    return primitive;
  }


  /**
   * Get a JsonValue from this object. If the value does not exist, the function is invoked to create a value.
   *
   * @param key          the value's key
   * @param defaultValue supplier of a default value
   *
   * @return the value
   */
  public JsonValue getJsonValue(String key, @Nonnull Function<String, JsonValue> defaultValue) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      return defaultValue.apply(key);
    }
    return primitive;
  }


  /**
   * Get a long from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the long, or the default
   */
  public long getLong(String key, long defaultValue) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.longValue() : defaultValue;
  }


  /**
   * Get a long from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the long, or the default
   */
  public long getLong(String key, @Nonnull ToLongFunction<String> defaultValue) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.longValue() : defaultValue.applyAsLong(key);
  }


  /**
   * Get a long from the object.
   *
   * @return the long
   */
  public long getLong(String key) {
    Number n = getSafe(Number.class, ValueType.NUMBER, key);
    return n.longValue();
  }


  /**
   * Get an object from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the object, or the default
   */
  public JObject getObject(String key, @Nonnull Function<String, JObject> defaultValue) {
    return getQuiet(JObject.class, key, defaultValue);
  }


  /**
   * Get an object from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the object, or the default
   */
  public JObject getObject(String key, JObject defaultValue) {
    return getQuiet(JObject.class, key, defaultValue);
  }


  /**
   * Get an object from the object.
   *
   * @return the object
   */
  @Nonnull
  public JObject getObject(String key) {
    return getSafe(JObject.class, ValueType.OBJECT, key);
  }


  @Override
  public abstract JsonValue getOrDefault(Object key, JsonValue defaultValue);


  public abstract Primitive get(String name);


  public Primitive getPrimitive(String name) {
    return get(name);
  }


  private <T> T getQuiet(Class<T> clazz, String key) {
    return getQuiet(clazz, key, (Function<String, T>) k -> null);
  }


  private <T> T getQuiet(Class<T> clazz, String key, Function<String, T> function) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      return function.apply(key);
    }
    Object value = primitive.getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    return function.apply(key);
  }


  private <T> T getQuiet(Class<T> clazz, String key, T defaultValue) {
    return getQuiet(clazz, key, (Function<String, T>) k -> defaultValue);
  }


  private <T> T getSafe(Class<T> clazz, ValueType type, String key) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      throw new MissingItemException(key, type);
    }
    Object value = primitive.getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    throw new IncorrectTypeException(key, type, primitive.getValueType());
  }


  /**
   * Get a String from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the String, or the default
   */
  public String getString(String key, @Nonnull UnaryOperator<String> defaultValue) {
    return getQuiet(String.class, key, defaultValue);
  }


  /**
   * Get a String from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the String, or the default
   */
  public String getString(String key, String defaultValue) {
    return getQuiet(String.class, key, defaultValue);
  }


  /**
   * Get a String from the object.
   *
   * @return the String
   */
  @Nonnull
  public String getString(String key) {
    return getSafe(String.class, ValueType.STRING, key);
  }


  @Override
  public <T> T getValue(Class<T> reqType, T defaultValue) {
    if (reqType.isInstance(this)) {
      return reqType.cast(this);
    }
    return defaultValue;
  }


  @Override
  public Object getValue() {
    return this;
  }


  @Override
  public <T> T getValueSafe(Class<T> reqType) {
    return reqType.cast(this);
  }


  @Override
  public ValueType getValueType() {
    return ValueType.OBJECT;
  }


  /**
   * Verify if the type of the specified property is as required.
   *
   * @param key  the key
   * @param type the desired type
   *
   * @return True if the property exists and has the required type. False if the property exists and does not have the required type.
   *
   * @throws MissingItemException if the property does not exist
   */
  public boolean isType(String key, ValueType type) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      throw new MissingItemException(key, type);
    }
    return primitive.getValueType() == type;
  }


  @Override
  public abstract JsonValue merge(
      String key,
      @Nonnull JsonValue value,
      @Nonnull BiFunction<? super JsonValue, ? super JsonValue, ? extends JsonValue> remappingFunction
  );


  /**
   * Get an array from the object.
   *
   * @return the array, or null
   */
  @Nullable
  public JArray optArray(String key) {
    return getQuiet(JArray.class, key);
  }


  /**
   * Get a big decimal from the object.
   *
   * @return the big decimal, or null
   */
  @Nullable
  public BigDecimal optBigDecimal(String key) {
    Number n = getQuiet(Number.class, key);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @return the big integer, or null
   */
  @Nullable
  public BigInteger optBigInteger(String key) {
    Number n = getQuiet(Number.class, key);
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a Boolean from the object.
   *
   * @return the Boolean, or null
   */
  @Nullable
  public Boolean optBoolean(String key) {
    return getQuiet(Boolean.class, key);
  }


  /**
   * Get a double from the object.
   *
   * @return the double, or null
   */
  @Nullable
  public Double optDouble(String key) {
    Primitive p = getPrimitive(key);
    if (p == null) {
      return null;
    }
    return PNumber.toDouble(p);
  }


  /**
   * Get an integer from the object.
   *
   * @return the integer, or null
   */
  @Nullable
  public Integer optInt(String key) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.intValue() : null;
  }


  /**
   * Get a JsonValue from this object. If the value does not exist, null is returned.
   *
   * @param key the value's key
   *
   * @return the value
   */
  public JsonValue optJsonValue(String key) {
    return getPrimitive(key);
  }


  /**
   * Get a long from the object.
   *
   * @return the long, or null
   */
  @Nullable
  public Long optLong(String key) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.longValue() : null;
  }


  /**
   * Get an object from the object.
   *
   * @return the object, or null
   */
  @Nullable
  public JObject optObject(String key) {
    return getQuiet(JObject.class, key);
  }


  /**
   * Get a String from the object.
   *
   * @return the String, or null
   */
  @Nullable
  public String optString(String key) {
    return getQuiet(String.class, key);
  }


  /**
   * Ensure that all Strings and Numbers have a single representation in memory.
   */
  public abstract void optimiseStorage();

  /**
   * Ensure that all Strings and Numbers have a single representation in memory.
   *
   * @param values the unique values
   */
  abstract void optimiseStorage(HashMap<Primitive, Primitive> values);

  public abstract Primitive put(String key, Primitive value);


  /**
   * Put a null value into this.
   *
   * @param key the key
   */
  public void put(String key) {
    put(key, PNull.NULL);
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public JsonValue put(String key, Boolean value) {
    if (value != null) {
      return put(key, value ? PTrue.TRUE : PFalse.FALSE);
    }
    return put(key, PNull.NULL);
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, JArray value) {
    if (value != null) {
      put(key, (Primitive) value);
    } else {
      put(key, PNull.NULL);
    }
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, JObject value) {
    if (value != null) {
      put(key, (Primitive) value);
    } else {
      put(key, PNull.NULL);
    }
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, Number value) {
    if (value != null) {
      put(key, PNumber.cast(value));
    } else {
      put(key, PNull.NULL);
    }
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, String value) {
    if (value != null) {
      put(key, PString.create(value));
    } else {
      put(key, PNull.NULL);
    }
  }


  @Override
  public abstract JsonValue putIfAbsent(String key, JsonValue value);

  @Override
  public abstract Primitive remove(Object key);

  @Override
  public abstract boolean remove(Object key, Object value);


  /**
   * Remove a JSON array from this.
   *
   * @param key the key to remove, if it is an array
   *
   * @return the array removed
   */
  @Nullable
  public JArray removeArray(String key) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null || primitive.getValueType() != ValueType.ARRAY) {
      return null;
    }
    remove(key);
    return (JArray) primitive.getValue();
  }


  /**
   * Remove a Boolean from this.
   *
   * @param key the key to remove, if it is a Boolean
   *
   * @return the Boolean removed
   */
  @Nullable
  public Boolean removeBoolean(String key) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null || !Primitive.IS_BOOLEAN.contains(primitive.getValueType())) {
      return null;
    }
    remove(key);
    return (Boolean) primitive.getValue();
  }


  /**
   * Remove a null from this.
   *
   * @param key the key to remove, if it is null
   */
  public void removeNull(String key) {
    Primitive primitive = getPrimitive(key);
    if (primitive != null && primitive.getValueType() == ValueType.NULL) {
      remove(key);
    }
  }


  /**
   * Remove a number from this.
   *
   * @param key the key to remove, if it is a number
   *
   * @return the number removed
   */
  @Nullable
  public Number removeNumber(String key) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null || primitive.getValueType() != ValueType.NUMBER) {
      return null;
    }
    remove(key);
    return (Number) primitive.getValue();
  }


  /**
   * Remove a JSON object from this.
   *
   * @param key the key to remove, if it is an object
   *
   * @return the object removed
   */
  @Nullable
  public JObject removeObject(String key) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null || primitive.getValueType() != ValueType.OBJECT) {
      return null;
    }
    remove(key);
    return (JObject) primitive.getValue();
  }


  /**
   * Remove a String from this.
   *
   * @param key the key to remove, if it is a String
   *
   * @return the String that was removed
   */
  @Nullable
  public String removeString(String key) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null || primitive.getValueType() != ValueType.STRING) {
      return null;
    }
    remove(key);
    return (String) primitive.getValue();
  }


  @Override
  public abstract boolean replace(String key, JsonValue oldValue, JsonValue newValue);

  @Override
  public abstract JsonValue replace(String key, JsonValue value);

  @Override
  public abstract void replaceAll(BiFunction<? super String, ? super JsonValue, ? extends JsonValue> function);

}
