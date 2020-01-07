package io.setl.json;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import io.setl.json.jackson.JsonObjectSerializer;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;

/**
 * Representation of an object in JSON.
 *
 * <p>No value in the object can be null. If you try to add one, it will be replaced by a Primitive instance holding a null.
 *
 * <p>As JSON objects can contain mixed content, this class provides type-checking accessors to the array members. There are multiple varieties of each accessor
 * which obey these contracts:
 *
 * <dl>
 * <dt><code>get<i>Type</i>(key)</code></dt>
 * <dd>
 * <ul>
 * <li>If the key is not present, returns null.</li>
 * <li>If the entry is not the required type, returns null.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 *
 * <dt><code>get<i>Type</i>(index, default)</code></dt>
 * <dd>
 * <ul>
 * <li>If the key is not present, returns the default.
 * <li>If the entry is not the required type, returns the default.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * <dt><code>get<i>Type</i>(index, function)</code></dt>
 * <dd>
 * <ul>
 * <li>If the key is not present, invokes the function to derive a suitable value.
 * <li>If the entry is not the required type, invokes the function to derive a suitable value.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * <dt><code>get<i>Type</i>Safe(index)</code></dt>
 * <dd>
 * <ul>
 * <li>If the key is not present, throws a <code>MissingItemException</code>.
 * <li>If the entry is not the required type, throws an <code>IncorrectTypeException</code>.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * </dl>
 *
 * <p>The numeric accessors follow the normal Java rules for primitive type conversions and consider any number to be the correct type. For example, if you
 * call <code>getIntSafe(key)</code> and element 0 contains the Long value 1L<<50, then the call returns the value of Integer.MAX_VALUE, as would be expected
 * for a narrowing primitive conversion, rather than throwing a <code>IncorrectTypeException</code>.
 */

@JsonSerialize(using = JsonObjectSerializer.class)
public class JObject extends TreeMap<String, Primitive> implements JContainer, JsonObject {

  /**
   * Sort object keys into Unicode code point order.
   */
  public static final Comparator<String> CODE_POINT_ORDER = (s1, s2) -> {
    int len1 = s1.length();
    int len2 = s2.length();
    int lim = Math.min(len1, len2);
    for (int i = 0; i < lim; i++) {
      int cp1 = s1.codePointAt(i);
      int cp2 = s2.codePointAt(i);
      if (cp1 != cp2) {
        return cp1 - cp2;
      }
      if (cp1 > 0xffff) {
        i++;
      }
    }
    return len1 - len2;
  };

  /** serial version UID. */
  private static final long serialVersionUID = 1L;


  /**
   * Convert any map into a JObject.
   *
   * @param map the map to convert
   *
   * @return the equivalent JObject
   */
  static JObject fixMap(Map<?, ?> map) {
    if (map instanceof JObject) {
      return (JObject) map;
    }

    JObject out = new JObject();
    for (Map.Entry<?, ?> entry : map.entrySet()) {
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


  public JObject() {
    super(CODE_POINT_ORDER);
  }


  public JObject(Map<String, ?> map) {
    super(CODE_POINT_ORDER);
    putAll(fixMap(map));
  }


  /**
   * Get an array from the object.
   *
   * @return the array, or null
   */
  public JArray getArray(String key) {
    return getQuiet(JArray.class, key);
  }


  /**
   * Get an array from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the array, or the default
   */
  public JArray getArray(String key, Function<String, JArray> dflt) {
    return getQuiet(JArray.class, key, dflt);
  }


  /**
   * Get an array from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the array, or the default
   */
  public JArray getArray(String key, JArray dflt) {
    return getQuiet(JArray.class, key, dflt);
  }


  /**
   * Get an array from the object.
   *
   * @return the array
   */
  public JArray getArraySafe(String key) {
    return getSafe(JArray.class, JType.ARRAY, key);
  }


  /**
   * Get a big decimal from the object.
   *
   * @return the big decimal, or null
   */
  public BigDecimal getBigDecimal(String key) {
    Number n = getQuiet(Number.class, key);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(String key, BigDecimal dflt) {
    Number n = getQuiet(Number.class, key);
    if (n == null) {
      return dflt;
    }
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(String key, Function<String, BigDecimal> dflt) {
    Number n = getQuiet(Number.class, key);
    if (n == null) {
      return dflt.apply(key);
    }
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the object.
   *
   * @return the big decimal
   */
  public BigDecimal getBigDecimalSafe(String key) {
    Number n = getSafe(Number.class, JType.NUMBER, key);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @return the big integer, or null
   */
  @Nullable
  public BigInteger getBigInteger(String key) {
    Number n = getQuiet(Number.class, key);
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(String key, BigInteger dflt) {
    Number n = getQuiet(Number.class, key);
    if (n == null) {
      return dflt;
    }
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(String key, Function<String, BigInteger> dflt) {
    Number n = getQuiet(Number.class, key);
    if (n == null) {
      return dflt.apply(key);
    }
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @return the big integer
   */
  public BigInteger getBigIntegerSafe(String key) {
    Number n = getSafe(Number.class, JType.NUMBER, key);
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a Boolean from the object.
   *
   * @return the Boolean, or null
   */
  @Nullable
  public Boolean getBoolean(String key) {
    return getQuiet(Boolean.class, key);
  }


  /**
   * Get a Boolean from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(String key, boolean dflt) {
    return getQuiet(Boolean.class, key, Boolean.valueOf(dflt)).booleanValue();
  }


  @Override
  public boolean isNull(String name) {
    return get(name).getType() == JType.NULL;
  }


  /**
   * Get a Boolean from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(String key, Predicate<String> dflt) {
    Boolean value = getQuiet(Boolean.class, key);
    return (value != null) ? value.booleanValue() : dflt.test(key);
  }


  /**
   * Get a Boolean from the object.
   *
   * @return the Boolean
   */
  @Nonnull
  public Boolean getBooleanSafe(String key) {
    return getSafe(Boolean.class, JType.BOOLEAN, key);
  }


  /**
   * Get a double from the object.
   *
   * @return the double, or null
   */
  @Nullable
  public Double getDouble(String key) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? Double.valueOf(n.doubleValue()) : null;
  }


  /**
   * Get a double from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the double, or the default
   */
  public double getDouble(String key, double dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.doubleValue() : dflt;
  }


  /**
   * Get a double from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the double, or the default
   */
  public double getDouble(String key, ToDoubleFunction<String> dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.doubleValue() : dflt.applyAsDouble(key);
  }


  /**
   * Get a double from the object.
   *
   * @return the double
   */
  public double getDoubleSafe(String key) {
    Number n = getSafe(Number.class, JType.NUMBER, key);
    return n.doubleValue();
  }


  /**
   * Get an integer from the object.
   *
   * @return the integer, or null
   */
  @Nullable
  public Integer getInteger(String key) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? Integer.valueOf(n.intValue()) : null;
  }


  /**
   * Get an integer from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the integer, or the default
   */
  public int getInteger(String key, int dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.intValue() : dflt;
  }


  /**
   * Get an integer from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the integer, or the default
   */
  public int getInteger(String key, ToIntFunction<String> dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.intValue() : dflt.applyAsInt(key);
  }


  /**
   * Get an integer from the object.
   *
   * @return the integer
   */
  public int getIntegerSafe(String key) {
    return getSafe(Number.class, JType.NUMBER, key).intValue();
  }


  /**
   * Get a long from the object.
   *
   * @return the long, or null
   */
  @Nullable
  public Long getLong(String key) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? Long.valueOf(n.longValue()) : null;
  }


  /**
   * Get a long from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the long, or the default
   */
  public long getLong(String key, long dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.longValue() : dflt;
  }


  /**
   * Get a long from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the long, or the default
   */
  public long getLong(String key, ToLongFunction<String> dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.longValue() : dflt.applyAsLong(key);
  }


  /**
   * Get a long from the object.
   *
   * @return the long
   */
  public long getLongSafe(String key) {
    Number n = getSafe(Number.class, JType.NUMBER, key);
    return n.longValue();
  }


  /**
   * Get an object from the object.
   *
   * @return the object, or null
   */
  @Nullable
  public JObject getObject(String key) {
    return getQuiet(JObject.class, key);
  }


  /**
   * Get an object from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the object, or the default
   */
  public JObject getObject(String key, Function<String, JObject> dflt) {
    return getQuiet(JObject.class, key, dflt);
  }


  /**
   * Get an object from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the object, or the default
   */
  public JObject getObject(String key, JObject dflt) {
    return getQuiet(JObject.class, key, dflt);
  }


  /**
   * Get an object from the object.
   *
   * @return the object
   */
  @Nonnull
  public JObject getObjectSafe(String key) {
    return getSafe(JObject.class, JType.OBJECT, key);
  }


  private <T> T getQuiet(Class<T> clazz, String key) {
    return getQuiet(clazz, key, (Function<String, T>) k -> null);
  }


  private <T> T getQuiet(Class<T> clazz, String key, Function<String, T> function) {
    Primitive primitive = get(key);
    if (primitive == null) {
      return function.apply(key);
    }
    Object value = primitive.getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    return function.apply(key);
  }


  private <T> T getQuiet(Class<T> clazz, String key, T dflt) {
    return getQuiet(clazz, key, (Function<String, T>) k -> dflt);
  }


  private <T> T getSafe(Class<T> clazz, JType type, String key) {
    Primitive primitive = get(key);
    if (primitive == null) {
      throw new MissingItemException(key, type);
    }
    Object value = primitive.getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    throw new IncorrectTypeException(key, type, primitive.getType());
  }


  @Override
  public JsonArray getJsonArray(String name) {
    return null;
  }


  @Override
  public JsonObject getJsonObject(String name) {
    return null;
  }


  @Override
  public JsonNumber getJsonNumber(String name) {
    return null;
  }


  @Override
  public JsonString getJsonString(String name) {
    return null;
  }


  /**
   * Get a String from the object.
   *
   * @return the String, or null
   */
  @Nullable
  public String getString(String key) {
    return getQuiet(String.class, key);
  }


  /**
   * Get a String from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the String, or the default
   */
  public String getString(String key, Function<String, String> dflt) {
    return getQuiet(String.class, key, dflt);
  }


  /**
   * Get a String from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the String, or the default
   */
  public String getString(String key, String dflt) {
    return getQuiet(String.class, key, dflt);
  }


  /**
   * Get a String from the object.
   *
   * @return the String
   */
  @Nonnull
  public String getStringSafe(String key) {
    return getSafe(String.class, JType.STRING, key);
  }


  @Override
  public JType getType() {
    return JType.OBJECT;
  }


  public boolean isArray() {
    return false;
  }


  public boolean isType(String key, JType type) {
    Primitive primitive = get(key);
    return (primitive != null) && primitive.getType() == type;
  }


  /**
   * Put a null value into this.
   *
   * @param key the key
   */
  public void put(String key) {
    put(key, new Primitive(JType.NULL, null));
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, Boolean value) {
    if (value != null) {
      put(key, new Primitive(JType.BOOLEAN, value));
    } else {
      put(key, new Primitive(JType.NULL, null));
    }
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, JArray value) {
    if (value != null) {
      put(key, new Primitive(JType.ARRAY, value));
    } else {
      put(key, new Primitive(JType.NULL, null));
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
      put(key, new Primitive(JType.OBJECT, value));
    } else {
      put(key, new Primitive(JType.NULL, null));
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
      put(key, new Primitive(JType.NUMBER, value));
    } else {
      put(key, new Primitive(JType.NULL, null));
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
      put(key, new Primitive(JType.STRING, value));
    } else {
      put(key, new Primitive(JType.NULL, null));
    }
  }


  /**
   * Remove a JSON array from this.
   *
   * @param key the key to remove, if it is an array
   *
   * @return the array removed
   */
  @Nullable
  public JArray removeArray(String key) {
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != JType.ARRAY) {
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
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != JType.BOOLEAN) {
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
    Primitive primitive = get(key);
    if (primitive != null && primitive.getType() == JType.NULL) {
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
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != JType.NUMBER) {
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
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != JType.OBJECT) {
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
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != JType.STRING) {
      return null;
    }
    remove(key);
    return (String) primitive.getValue();
  }


  @Override
  public ValueType getValueType() {
    return ValueType.OBJECT;
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('{');
    for (Map.Entry<String, Primitive> e : entrySet()) {
      buf.append(Canonical.format(e.getKey()));
      buf.append(':');
      buf.append(String.valueOf(e.getValue()));
      buf.append(',');
    }
    if (buf.length() > 1) {
      // remove final comma
      buf.setLength(buf.length() - 1);
    }
    buf.append('}');
    return buf.toString();
  }


  @Override
  public void writeTo(Writer writer) throws IOException {
    writer.write('{');
    boolean isNotFirst = false;
    for (Map.Entry<String, Primitive> e : entrySet()) {
      if (isNotFirst) {
        writer.write(',');
      } else {
        isNotFirst = true;
      }

      Canonical.format(writer, e.getKey());
      writer.write(':');
      e.getValue().writeTo(writer);
    }
    writer.write('}');
  }
}