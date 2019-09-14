package com.pippsford.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.*;

/**
 * Representation of an object in JSON
 */
public class JsonObject extends TreeMap<String, Primitive> {
  /** serial version UID */
  private static final long serialVersionUID = 1l;

  private static Comparator<String> UNICODE_ORDER = new Comparator<String>() {
    @Override
    public int compare(String s1, String s2) {
      int len1 = s1.length();
      int len2 = s2.length();
      int lim = Math.min(len1, len2);
      for(int i = 0;i < lim;i++) {
        int cp1 = s1.codePointAt(i);
        int cp2 = s2.codePointAt(i);
        if( cp1 != cp2 ) {
          return cp1 - cp2;
        }
        if( cp1 > 0xffff ) {
          i++;
        }
      }
      return len1 - len2;
    }
  };


  public JsonObject() {
    super(UNICODE_ORDER);
  }


  /**
   * Get an array from the array.
   * 
   * @param key
   * @return the array, or null
   */
  public JsonArray getArray(String key) {
    return getQuiet(JsonArray.class, key);
  }


  /**
   * Get an array from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the array, or the default
   */
  public JsonArray getArray(String key, Function<String, JsonArray> dflt) {
    return getQuiet(JsonArray.class, key, dflt);
  }


  /**
   * Get an array from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the array, or the default
   */
  public JsonArray getArray(String key, JsonArray dflt) {
    return getQuiet(JsonArray.class, key, dflt);
  }


  /**
   * Get an array from the array.
   * 
   * @param key
   * @return the array
   */
  public JsonArray getArraySafe(String key) {
    return getSafe(JsonArray.class, Type.ARRAY, key);
  }


  /**
   * Get a big decimal from the array.
   * 
   * @param key
   * @return the big decimal, or null
   */
  public BigDecimal getBigDecimal(String key) {
    Number n = getQuiet(Number.class, key);
    if( n == null ) {
      return null;
    }
    if( n instanceof BigDecimal ) {
      return (BigDecimal) n;
    }
    if( n instanceof BigInteger ) {
      return new BigDecimal((BigInteger) n);
    }
    return new BigDecimal(n.toString());
  }


  /**
   * Get a big decimal from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(String key, BigDecimal dflt) {
    Number n = getQuiet(Number.class, key);
    if( n == null ) {
      return dflt;
    }
    if( n instanceof BigDecimal ) {
      return (BigDecimal) n;
    }
    if( n instanceof BigInteger ) {
      return new BigDecimal((BigInteger) n);
    }
    return new BigDecimal(n.toString());
  }


  /**
   * Get a big decimal from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(String key, Function<String, BigDecimal> dflt) {
    Number n = getQuiet(Number.class, key);
    if( n == null ) {
      return dflt.apply(key);
    }
    if( n instanceof BigDecimal ) {
      return (BigDecimal) n;
    }
    if( n instanceof BigInteger ) {
      return new BigDecimal((BigInteger) n);
    }
    return new BigDecimal(n.toString());
  }


  /**
   * Get a big decimal from the array.
   * 
   * @param key
   * @return the big decimal
   */
  public BigDecimal getBigDecimalSafe(String key) {
    Number n = getSafe(Number.class, Type.NUMBER, key);
    if( n instanceof BigDecimal ) {
      return (BigDecimal) n;
    }
    if( n instanceof BigInteger ) {
      return new BigDecimal((BigInteger) n);
    }
    return new BigDecimal(n.toString());
  }


  /**
   * Get a big integer from the array.
   * 
   * @param key
   * @return the big integer, or null
   */
  public BigInteger getBigInteger(String key) {
    Number n = getQuiet(Number.class, key);
    if( n == null ) {
      return null;
    }
    if( n instanceof BigInteger ) {
      return (BigInteger) n;
    }
    if( n instanceof BigDecimal ) {
      return ((BigDecimal) n).toBigInteger();
    }
    if( n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte ) {
      return BigInteger.valueOf(n.longValue());
    }
    return new BigDecimal(n.toString()).toBigInteger();
  }


  /**
   * Get a big integer from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(String key, BigInteger dflt) {
    Number n = getQuiet(Number.class, key);
    if( n == null ) {
      return dflt;
    }
    if( n instanceof BigInteger ) {
      return (BigInteger) n;
    }
    if( n instanceof BigDecimal ) {
      return ((BigDecimal) n).toBigInteger();
    }
    if( n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte ) {
      return BigInteger.valueOf(n.longValue());
    }
    return new BigDecimal(n.toString()).toBigInteger();
  }


  /**
   * Get a big integer from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(String key, Function<String, BigInteger> dflt) {
    Number n = getQuiet(Number.class, key);
    if( n == null ) {
      return dflt.apply(key);
    }
    if( n instanceof BigInteger ) {
      return (BigInteger) n;
    }
    if( n instanceof BigDecimal ) {
      return ((BigDecimal) n).toBigInteger();
    }
    if( n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte ) {
      return BigInteger.valueOf(n.longValue());
    }
    return new BigDecimal(n.toString()).toBigInteger();
  }


  /**
   * Get a big integer from the array.
   * 
   * @param key
   * @return the big integer
   */
  public BigInteger getBigIntegerSafe(String key) {
    Number n = getSafe(Number.class, Type.NUMBER, key);
    if( n instanceof BigInteger ) {
      return (BigInteger) n;
    }
    if( n instanceof BigDecimal ) {
      return ((BigDecimal) n).toBigInteger();
    }
    if( n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte ) {
      return BigInteger.valueOf(n.longValue());
    }
    return new BigDecimal(n.toString()).toBigInteger();
  }


  /**
   * Get a Boolean from the array.
   * 
   * @param key
   * @return the Boolean, or null
   */
  public Boolean getBoolean(String key) {
    return getQuiet(Boolean.class, key);
  }


  /**
   * Get a Boolean from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the Boolean, or the default
   */
  public boolean getBoolean(String key, boolean dflt) {
    return getQuiet(Boolean.class, key, Boolean.valueOf(dflt)).booleanValue();
  }


  /**
   * Get a Boolean from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the Boolean, or the default
   */
  public boolean getBoolean(String key, Predicate<String> dflt) {
    Boolean value = getQuiet(Boolean.class, key);
    return (value != null) ? value.booleanValue() : dflt.test(key);
  }


  /**
   * Get a Boolean from the array.
   * 
   * @param key
   * @return the Boolean, or null
   */
  public Boolean getBooleanSafe(String key) {
    return getSafe(Boolean.class, Type.BOOLEAN, key);
  }


  /**
   * Get a double from the array.
   * 
   * @param key
   * @return the double, or null
   */
  public Double getDouble(String key) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? Double.valueOf(n.doubleValue()) : null;
  }


  /**
   * Get a double from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the double, or the default
   */
  public double getDouble(String key, double dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.doubleValue() : dflt;
  }


  /**
   * Get a double from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the double, or the default
   */
  public double getDouble(String key, ToDoubleFunction<String> dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.doubleValue() : dflt.applyAsDouble(key);
  }


  /**
   * Get a double from the array.
   * 
   * @param key
   * @return the double
   */
  public double getDoubleSafe(String key) {
    Number n = getSafe(Number.class, Type.NUMBER, key);
    return n.doubleValue();
  }


  /**
   * Get an integer from the array.
   * 
   * @param key
   * @return the integer, or null
   */
  public Integer getInt(String key) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? Integer.valueOf(n.intValue()) : null;
  }


  /**
   * Get an integer from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the integer, or the default
   */
  public int getInt(String key, int dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.intValue() : dflt;
  }


  /**
   * Get an integer from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the integer, or the default
   */
  public int getInt(String key, ToIntFunction<String> dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.intValue() : dflt.applyAsInt(key);
  }


  /**
   * Get an integer from the array.
   * 
   * @param key
   * @return the integer
   */
  public int getIntSafe(String key) {
    return getSafe(Number.class, Type.NUMBER, key).intValue();
  }


  /**
   * Get a long from the array.
   * 
   * @param key
   * @return the long, or null
   */
  public Long getLong(String key) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? Long.valueOf(n.longValue()) : null;
  }


  /**
   * Get a long from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the long, or the default
   */
  public long getLong(String key, ToLongFunction<String> dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.longValue() : dflt.applyAsLong(key);
  }


  /**
   * Get a long from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the long, or the default
   */
  public long getLong(String key, long dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.longValue() : dflt;
  }


  /**
   * Get a long from the array.
   * 
   * @param key
   * @return the long
   */
  public long getLongSafe(String key) {
    Number n = getSafe(Number.class, Type.NUMBER, key);
    return n.longValue();
  }


  /**
   * Get an object from the array.
   * 
   * @param key
   * @return the object, or null
   */
  public JsonObject getObject(String key) {
    return getQuiet(JsonObject.class, key);
  }


  /**
   * Get an object from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the object, or the default
   */
  public JsonObject getObject(String key, Function<String, JsonObject> dflt) {
    return getQuiet(JsonObject.class, key, dflt);
  }


  /**
   * Get an object from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the object, or the default
   */
  public JsonObject getObject(String key, JsonObject dflt) {
    return getQuiet(JsonObject.class, key, dflt);
  }


  /**
   * Get an object from the array.
   * 
   * @param key
   * @return the object
   */
  public JsonObject getObjectSafe(String key) {
    return getSafe(JsonObject.class, Type.OBJECT, key);
  }


  private <T> T getQuiet(Class<T> clazz, String key) {
    return getQuiet(clazz, key, (Function<String, T>) k -> null);
  }


  private <T> T getQuiet(Class<T> clazz, String key, Function<String, T> function) {
    Object value = get(key).getValue();
    if( clazz.isInstance(value) ) {
      return clazz.cast(value);
    }
    return function.apply(key);
  }


  private <T> T getQuiet(Class<T> clazz, String key, T dflt) {
    return getQuiet(clazz, key, (Function<String, T>) k -> dflt);
  }


  private <T> T getSafe(Class<T> clazz, Type type, String key) {
    Primitive primitive = get(key);
    if( primitive == null ) {
      throw new MissingItemException(key, type);
    }
    Object value = primitive.getValue();
    if( clazz.isInstance(value) ) {
      return clazz.cast(value);
    }
    throw new IncorrectTypeException(key, type, primitive.getType());
  }


  /**
   * Get a String from the array.
   * 
   * @param key
   * @return the String, or null
   */
  public String getString(String key) {
    return getQuiet(String.class, key);
  }


  /**
   * Get a String from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the String, or the default
   */
  public String getString(String key, Function<String, String> dflt) {
    return getQuiet(String.class, key, dflt);
  }


  /**
   * Get a String from the array.
   * 
   * @param key
   *          the key
   * @param dflt
   *          the default
   * @return the String, or the default
   */
  public String getString(String key, String dflt) {
    return getQuiet(String.class, key, dflt);
  }


  /**
   * Get a String from the array.
   * 
   * @param key
   * @return the String
   */
  public String getStringSafe(String key) {
    return getSafe(String.class, Type.STRING, key);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('{');
    for(Map.Entry<String, Primitive> e:entrySet()) {
      buf.append(Generator.escapeString(e.getKey()));
      buf.append(':');
      buf.append(String.valueOf(e.getValue()));
      buf.append(',');
    }
    if( buf.length() > 1 ) {
      // remove final comma
      buf.setLength(buf.length() - 1);
    }
    buf.append('}');
    return buf.toString();
  }
}
