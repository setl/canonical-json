package io.setl.json;

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

/**
 * Representation of an object in JSON.
 */
public class JsonObject extends TreeMap<String, Primitive> implements Writable {

  /** serial version UID. */
  private static final long serialVersionUID = 1L;

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


  /**
   * Convert any map into a JsonObject.
   *
   * @param map the map to convert
   *
   * @return the equivalent JsonObject
   */
  static JsonObject fixMap(Map<?, ?> map) {
    if (map instanceof JsonObject) {
      return (JsonObject) map;
    }

    JsonObject out = new JsonObject();
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


  public JsonObject() {
    super(CODE_POINT_ORDER);
  }


  public JsonObject(Map<String, ?> map) {
    super(CODE_POINT_ORDER);
    putAll(fixMap(map));
  }


  /**
   * Get an array from the object.
   *
   * @return the array, or null
   */
  public JsonArray getArray(String key) {
    return getQuiet(JsonArray.class, key);
  }


  /**
   * Get an array from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the array, or the default
   */
  public JsonArray getArray(String key, Function<String, JsonArray> dflt) {
    return getQuiet(JsonArray.class, key, dflt);
  }


  /**
   * Get an array from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the array, or the default
   */
  public JsonArray getArray(String key, JsonArray dflt) {
    return getQuiet(JsonArray.class, key, dflt);
  }


  /**
   * Get an array from the object.
   *
   * @return the array
   */
  public JsonArray getArraySafe(String key) {
    return getSafe(JsonArray.class, Type.ARRAY, key);
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
    Number n = getSafe(Number.class, Type.NUMBER, key);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @return the big integer, or null
   */
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
    Number n = getSafe(Number.class, Type.NUMBER, key);
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a Boolean from the object.
   *
   * @return the Boolean, or null
   */
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
   * @return the Boolean, or null
   */
  public Boolean getBooleanSafe(String key) {
    return getSafe(Boolean.class, Type.BOOLEAN, key);
  }


  /**
   * Get a double from the object.
   *
   * @return the double, or null
   */
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
    Number n = getSafe(Number.class, Type.NUMBER, key);
    return n.doubleValue();
  }


  /**
   * Get an integer from the object.
   *
   * @return the integer, or null
   */
  public Integer getInt(String key) {
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
  public int getInt(String key, int dflt) {
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
  public int getInt(String key, ToIntFunction<String> dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.intValue() : dflt.applyAsInt(key);
  }


  /**
   * Get an integer from the object.
   *
   * @return the integer
   */
  public int getIntSafe(String key) {
    return getSafe(Number.class, Type.NUMBER, key).intValue();
  }


  /**
   * Get a long from the object.
   *
   * @return the long, or null
   */
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
    Number n = getSafe(Number.class, Type.NUMBER, key);
    return n.longValue();
  }


  /**
   * Get an object from the object.
   *
   * @return the object, or null
   */
  public JsonObject getObject(String key) {
    return getQuiet(JsonObject.class, key);
  }


  /**
   * Get an object from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the object, or the default
   */
  public JsonObject getObject(String key, Function<String, JsonObject> dflt) {
    return getQuiet(JsonObject.class, key, dflt);
  }


  /**
   * Get an object from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the object, or the default
   */
  public JsonObject getObject(String key, JsonObject dflt) {
    return getQuiet(JsonObject.class, key, dflt);
  }


  /**
   * Get an object from the object.
   *
   * @return the object
   */
  public JsonObject getObjectSafe(String key) {
    return getSafe(JsonObject.class, Type.OBJECT, key);
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


  private <T> T getSafe(Class<T> clazz, Type type, String key) {
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


  /**
   * Get a String from the object.
   *
   * @return the String, or null
   */
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
  public String getStringSafe(String key) {
    return getSafe(String.class, Type.STRING, key);
  }


  public boolean isType(String key, Type type) {
    Primitive primitive = get(key);
    return (primitive != null) && primitive.getType() == type;
  }


  /**
   * Put a null value into this.
   *
   * @param key the key
   */
  public void put(String key) {
    put(key, new Primitive(Type.NULL, null));
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, Boolean value) {
    if (value != null) {
      put(key, new Primitive(Type.BOOLEAN, value));
    } else {
      put(key, new Primitive(Type.NULL, null));
    }
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, JsonArray value) {
    if (value != null) {
      put(key, new Primitive(Type.ARRAY, value));
    } else {
      put(key, new Primitive(Type.NULL, null));
    }
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, JsonObject value) {
    if (value != null) {
      put(key, new Primitive(Type.OBJECT, value));
    } else {
      put(key, new Primitive(Type.NULL, null));
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
      put(key, new Primitive(Type.NUMBER, value));
    } else {
      put(key, new Primitive(Type.NULL, null));
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
      put(key, new Primitive(Type.STRING, value));
    } else {
      put(key, new Primitive(Type.NULL, null));
    }
  }


  /**
   * Remove a JSON array from this.
   *
   * @param key the key to remove, if it is an array
   *
   * @return the array removed
   */
  public JsonArray removeArray(String key) {
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != Type.ARRAY) {
      return null;
    }
    remove(key);
    return (JsonArray) primitive.getValue();
  }


  /**
   * Remove a Boolean from this.
   *
   * @param key the key to remove, if it is a Boolean
   *
   * @return the Boolean removed
   */
  public Boolean removeBoolean(String key) {
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != Type.BOOLEAN) {
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
    if (primitive != null && primitive.getType() == Type.NULL) {
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

  public Number removeNumber(String key) {
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != Type.NUMBER) {
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
  public JsonObject removeObject(String key) {
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != Type.OBJECT) {
      return null;
    }
    remove(key);
    return (JsonObject) primitive.getValue();
  }


  /**
   * Remove a String from this.
   *
   * @param key the key to remove, if it is a String
   *
   * @return the String that was removed
   */
  public String removeString(String key) {
    Primitive primitive = get(key);
    if (primitive == null || primitive.getType() != Type.STRING) {
      return null;
    }
    remove(key);
    return (String) primitive.getValue();
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
