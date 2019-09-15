package com.pippsford.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.*;

/**
 * Representation of an array in JSON.
 * <p>
 * No entry in the list can be null. If you try to add one, it will be replaced by a Primitive instance holding a null.
 * <p>
 * As JSON arrays can contain mixed content, this class provides type-checking accessors to the array members. There are multiple varieties of each accessor
 * which obey these contracts:
 * 
 * <dl>
 * <dt><code>get<i>Type</i>(index)</code></dt>
 * <dd>
 * <ul>
 * <li>If the index is less than zero, or greater than or equal to the size of this array, returns null.
 * <li>If the entry is not the required type, returns null.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * 
 * <dt><code>get<i>Type</i>(index, default)</code></dt>
 * <dd>
 * <ul>
 * <li>If the index is less than zero, or greater than or equal to the size of this array, returns the default.
 * <li>If the entry is not the required type, returns the default.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * <dt><code>get<i>Type</i>(index, function)</code></dt>
 * <dd>
 * <ul>
 * <li>If the index is less than zero, or greater than or equal to the size of this array, invokes the function to derive a suitable value.
 * <li>If the entry is not the required type, invokes the function to derive a suitable value.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * <dt><code>get<i>Type</i>Safe(index)</code></dt>
 * <dd>
 * <ul>
 * <li>If the index is less than zero, or greater than or equal to the size of this array, throws a <code>MissingItemException</code>.
 * <li>If the entry is not the required type, throws an <code>IncorrectTypeException</code>.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * </dl>
 * 
 * <p>
 * The numeric accessors follow the normal Java rules for primitive type conversions and consider any number to be the correct type. For example, if you call
 * <code>getIntSafe(0)</code> and element 0 contains the Long value 1L<<50, then the call returns the value of Integer.MAX_VALUE, as would be expected for a
 * narrowing primitive conversion, rather than throwing a <code>IncorrectTypeException</code>.
 */
public class JsonArray extends ArrayList<Primitive> {

  /** serial version UID */
  private static final long serialVersionUID = 2l;


  /**
   * Ensure a collection contains no actual nulls.
   * 
   * @param c
   *          the collection
   * @return a collection with the nulls replaced with JSON nulls.
   */
  private static Collection<Primitive> fixCollection(Collection<? extends Primitive> c) {
    Primitive[] array = c.toArray(new Primitive[0]);
    for(int i = array.length - 1;i >= 0;i--) {
      if( array[i] == null ) {
        array[i] = Primitive.NULL;
      }
    }
    return Arrays.asList(array);
  }


  private static Primitive fixNull(Primitive element) {
    return element != null ? element : Primitive.NULL;
  }


  @Override
  public void add(int index, Primitive element) {
    super.add(index, fixNull(element));
  }


  @Override
  public boolean add(Primitive e) {
    return super.add(fixNull(e));
  }


  @Override
  public boolean addAll(Collection<? extends Primitive> c) {
    return super.addAll(fixCollection(c));
  }


  @Override
  public boolean addAll(int index, Collection<? extends Primitive> c) {
    return super.addAll(index, fixCollection(c));
  }


  /**
   * Get an array from the array.
   * 
   * @param index
   * @return the array, or null
   */
  public JsonArray getArray(int index) {
    return getQuiet(JsonArray.class, index);
  }


  /**
   * Get an array from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the array, or the default
   */
  public JsonArray getArray(int index, IntFunction<JsonArray> dflt) {
    return getQuiet(JsonArray.class, index, dflt);
  }


  /**
   * Get an array from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the array, or the default
   */
  public JsonArray getArray(int index, JsonArray dflt) {
    return getQuiet(JsonArray.class, index, dflt);
  }


  /**
   * Get an array from the array.
   * 
   * @param index
   * @return the array
   */
  public JsonArray getArraySafe(int index) {
    return getSafe(JsonArray.class, Type.ARRAY, index);
  }


  /**
   * Get a big decimal from the array.
   * 
   * @param index
   * @return the big decimal, or null
   */
  public BigDecimal getBigDecimal(int index) {
    Number n = getQuiet(Number.class, index);
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
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(int index, BigDecimal dflt) {
    Number n = getQuiet(Number.class, index);
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
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(int index, IntFunction<BigDecimal> dflt) {
    Number n = getQuiet(Number.class, index);
    if( n == null ) {
      return dflt.apply(index);
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
   * @param index
   * @return the big decimal
   */
  public BigDecimal getBigDecimalSafe(int index) {
    Number n = getSafe(Number.class, Type.NUMBER, index);
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
   * @param index
   * @return the big integer, or null
   */
  public BigInteger getBigInteger(int index) {
    Number n = getQuiet(Number.class, index);
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
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(int index, BigInteger dflt) {
    Number n = getQuiet(Number.class, index);
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
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(int index, IntFunction<BigInteger> dflt) {
    Number n = getQuiet(Number.class, index);
    if( n == null ) {
      return dflt.apply(index);
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
   * @param index
   * @return the big integer
   */
  public BigInteger getBigIntegerSafe(int index) {
    Number n = getSafe(Number.class, Type.NUMBER, index);
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
   * @param index
   * @return the Boolean, or null
   */
  public Boolean getBoolean(int index) {
    return getQuiet(Boolean.class, index);
  }


  /**
   * Get a Boolean from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the Boolean, or the default
   */
  public boolean getBoolean(int index, boolean dflt) {
    return getQuiet(Boolean.class, index, Boolean.valueOf(dflt)).booleanValue();
  }


  /**
   * Get a Boolean from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the Boolean, or the default
   */
  public boolean getBoolean(int index, IntPredicate dflt) {
    Boolean value = getQuiet(Boolean.class, index);
    return (value != null) ? value.booleanValue() : dflt.test(index);
  }


  /**
   * Get a Boolean from the array.
   * 
   * @param index
   * @return the Boolean, or null
   */
  public Boolean getBooleanSafe(int index) {
    return getSafe(Boolean.class, Type.BOOLEAN, index);
  }


  /**
   * Get a double from the array.
   * 
   * @param index
   * @return the double, or null
   */
  public Double getDouble(int index) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? Double.valueOf(n.doubleValue()) : null;
  }


  /**
   * Get a double from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the double, or the default
   */
  public double getDouble(int index, double dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.doubleValue() : dflt;
  }


  /**
   * Get a double from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the double, or the default
   */
  public double getDouble(int index, IntToDoubleFunction dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.doubleValue() : dflt.applyAsDouble(index);
  }


  /**
   * Get a double from the array.
   * 
   * @param index
   * @return the double
   */
  public double getDoubleSafe(int index) {
    Number n = getSafe(Number.class, Type.NUMBER, index);
    return n.doubleValue();
  }


  /**
   * Get an integer from the array.
   * 
   * @param index
   * @return the integer, or null
   */
  public Integer getInt(int index) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? Integer.valueOf(n.intValue()) : null;
  }


  /**
   * Get an integer from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the integer, or the default
   */
  public int getInt(int index, int dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : dflt;
  }


  /**
   * Get an integer from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the integer, or the default
   */
  public int getInt(int index, IntUnaryOperator dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : dflt.applyAsInt(index);
  }


  /**
   * Get an integer from the array.
   * 
   * @param index
   * @return the integer
   */
  public int getIntSafe(int index) {
    return getSafe(Number.class, Type.NUMBER, index).intValue();
  }


  /**
   * Get a long from the array.
   * 
   * @param index
   * @return the long, or null
   */
  public Long getLong(int index) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? Long.valueOf(n.longValue()) : null;
  }


  /**
   * Get a long from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the long, or the default
   */
  public long getLong(int index, IntToLongFunction dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : dflt.applyAsLong(index);
  }


  /**
   * Get a long from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the long, or the default
   */
  public long getLong(int index, long dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : dflt;
  }


  /**
   * Get a long from the array.
   * 
   * @param index
   * @return the long
   */
  public long getLongSafe(int index) {
    Number n = getSafe(Number.class, Type.NUMBER, index);
    return n.longValue();
  }


  /**
   * Get an object from the array.
   * 
   * @param index
   * @return the object, or null
   */
  public JsonObject getObject(int index) {
    return getQuiet(JsonObject.class, index);
  }


  /**
   * Get an object from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the object, or the default
   */
  public JsonObject getObject(int index, IntFunction<JsonObject> dflt) {
    return getQuiet(JsonObject.class, index, dflt);
  }


  /**
   * Get an object from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the object, or the default
   */
  public JsonObject getObject(int index, JsonObject dflt) {
    return getQuiet(JsonObject.class, index, dflt);
  }


  /**
   * Get an object from the array.
   * 
   * @param index
   * @return the object
   */
  public JsonObject getObjectSafe(int index) {
    return getSafe(JsonObject.class, Type.OBJECT, index);
  }


  private <T> T getQuiet(Class<T> clazz, int index) {
    return getQuiet(clazz, index, (IntFunction<T>) i -> null);
  }


  private <T> T getQuiet(Class<T> clazz, int index, IntFunction<T> function) {
    if( index < 0 || size() <= index ) {
      return function.apply(index);
    }
    Object value = get(index).getValue();
    if( clazz.isInstance(value) ) {
      return clazz.cast(value);
    }
    return function.apply(index);
  }


  private <T> T getQuiet(Class<T> clazz, int index, T dflt) {
    return getQuiet(clazz, index, (IntFunction<T>) i -> dflt);
  }


  private <T> T getSafe(Class<T> clazz, Type type, int index) {
    if( index < 0 || size() <= index ) {
      throw new MissingItemException(index, type);
    }
    Primitive primitive = get(index);
    Object value = primitive.getValue();
    if( clazz.isInstance(value) ) {
      return clazz.cast(value);
    }
    throw new IncorrectTypeException(index, type, primitive.getType());
  }


  /**
   * Get a String from the array.
   * 
   * @param index
   * @return the String, or null
   */
  public String getString(int index) {
    return getQuiet(String.class, index);
  }


  /**
   * Get a String from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the String, or the default
   */
  public String getString(int index, IntFunction<String> dflt) {
    return getQuiet(String.class, index, dflt);
  }


  /**
   * Get a String from the array.
   * 
   * @param index
   *          the index
   * @param dflt
   *          the default
   * @return the String, or the default
   */
  public String getString(int index, String dflt) {
    return getQuiet(String.class, index, dflt);
  }


  /**
   * Get a String from the array.
   * 
   * @param index
   * @return the String
   */
  public String getStringSafe(int index) {
    return getSafe(String.class, Type.STRING, index);
  }


  @Override
  public void replaceAll(UnaryOperator<Primitive> operator) {
    super.replaceAll(p -> fixNull(operator.apply(p)));
  }


  @Override
  public Primitive set(int index, Primitive element) {
    return super.set(index, fixNull(element));
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    for(Primitive e:this) {
      buf.append(String.valueOf(e));
      buf.append(',');
    }
    // remove final comma
    if( buf.length() > 1 ) {
      buf.setLength(buf.length() - 1);
    }
    buf.append(']');
    return buf.toString();
  }

}
