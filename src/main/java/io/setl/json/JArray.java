package io.setl.json;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import io.setl.json.jackson.JsonArraySerializer;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import io.setl.json.primitive.numbers.PNumber;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Representation of an array in JSON.
 *
 * <p>No entry in the list can be null. If you try to add one, it will be replaced by a Primitive instance holding a null.
 *
 * <p>As JSON arrays can contain mixed content, this class provides type-checking accessors to the array members. There are multiple varieties of each accessor
 * which obey these contracts:
 *
 * <dl>
 * <dt><code>opt<i>Type</i>(index)</code></dt>
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
 * <dt><code>get<i>Type</i>(index)</code></dt>
 * <dd>
 * <ul>
 * <li>If the index is less than zero, or greater than or equal to the size of this array, throws a <code>MissingItemException</code>.
 * <li>If the entry is not the required type, throws an <code>IncorrectTypeException</code>.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * </dl>
 *
 * <p>The numeric accessors follow the normal Java rules for primitive type conversions and consider any number to be the correct type. For example, if you
 * call <code>getIntSafe(0)</code> and element 0 contains the Long value 1L<<50, then the call returns the value of Integer.MAX_VALUE, as would be expected
 * for a narrowing primitive conversion, rather than throwing a <code>IncorrectTypeException</code>.
 */
@JsonSerialize(using = JsonArraySerializer.class)
public class JArray extends ArrayList<JsonValue> implements JsonArray, Primitive {

  /** serial version UID. */
  private static final long serialVersionUID = 2L;


  /**
   * Convert a collection into a JsonArray.
   *
   * @param c the collection
   *
   * @return the JsonArray
   */
  static JArray fixCollection(Collection<?> c) {
    if (c instanceof JArray) {
      return (JArray) c;
    }
    JArray out = new JArray();
    out.ensureCapacity(c.size());
    for (Object o : c) {
      out.add(Primitive.create(o));
    }
    return out;
  }


  /**
   * Ensure a collection contains no actual nulls.
   *
   * @param c the collection
   *
   * @return a collection with the nulls replaced with JSON nulls.
   */
  static Collection<? extends JsonValue> fixPrimitiveCollection(Collection<? extends JsonValue> c) {
    if (c instanceof JsonArray) {
      // already fixed
      return c;
    }

    ArrayList<JsonValue> list = new ArrayList<>(c.size());
    for (JsonValue jv : c) {
      list.add(jv != null ? jv : PNull.NULL);
    }
    return list;
  }


  public JArray() {
    // as super-class
  }


  public JArray(Collection<?> c) {
    super(fixCollection(c));
  }


  public boolean add(Boolean value) {
    return add(value != null ? (value ? PTrue.TRUE : PFalse.FALSE) : PNull.NULL);
  }


  public void add(int index, Boolean value) {
    add(index, value != null ? (value ? PTrue.TRUE : PFalse.FALSE) : PNull.NULL);
  }


  public void add(int index, Number number) {
    add(index, number != null ? PNumber.create(number) : PNull.NULL);
  }


  @Override
  public void add(int index, JsonValue element) {
    super.add(index, Primitive.cast(element));
  }


  public void add(int index, String string) {
    add(index, string != null ? new PString(string) : PNull.NULL);
  }


  public boolean add(Number number) {
    return add(number != null ? PNumber.cast(number) : PNull.NULL);
  }


  @Override
  public boolean add(JsonValue e) {
    return super.add(Primitive.cast(e));
  }


  public boolean add(String string) {
    return add(string != null ? new PString(string) : PNull.NULL);
  }


  @Override
  public boolean addAll(Collection<? extends JsonValue> c) {
    return super.addAll(fixPrimitiveCollection(c));
  }


  @Override
  public boolean addAll(int index, Collection<? extends JsonValue> c) {
    return super.addAll(index, fixPrimitiveCollection(c));
  }


  public boolean addNull() {
    return add(PNull.NULL);
  }


  public void addNull(int index) {
    add(index, PNull.NULL);
  }


  @Override
  public JArray copy() {
    JArray other = new JArray(this);
    other.replaceAll(jv -> Primitive.create(jv));
    return other;
  }


  /**
   * Get an array from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the array, or the default
   */
  public JsonArray getArray(int index, IntFunction<JsonArray> dflt) {
    return getQuiet(JsonArray.class, index, dflt);
  }


  /**
   * Get an array from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the array, or the default
   */
  public JsonArray getArray(int index, JsonArray dflt) {
    return getQuiet(JsonArray.class, index, dflt);
  }


  /**
   * Get an array from the array.
   *
   * @return the array
   */
  @Nonnull
  public JsonArray getArray(int index) {
    return getSafe(JsonArray.class, ValueType.ARRAY, index);
  }


  /**
   * Get a big decimal from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(int index, BigDecimal dflt) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return dflt;
    }
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(int index, IntFunction<BigDecimal> dflt) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return dflt.apply(index);
    }
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the array.
   *
   * @return the big decimal
   */
  @Nonnull
  public BigDecimal getBigDecimal(int index) {
    Number n = getSafe(Number.class, ValueType.NUMBER, index);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(int index, BigInteger dflt) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return dflt;
    }
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(int index, IntFunction<BigInteger> dflt) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return dflt.apply(index);
    }
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @return the big integer
   */
  @Nonnull
  public BigInteger getBigInteger(int index) {
    Number n = getSafe(Number.class, ValueType.NUMBER, index);
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a Boolean from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(int index, boolean dflt) {
    return getQuiet(Boolean.class, index, dflt);
  }


  /**
   * Get a Boolean from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(int index, IntPredicate dflt) {
    Boolean value = getQuiet(Boolean.class, index);
    return (value != null) ? value : dflt.test(index);
  }


  /**
   * Get a Boolean from the array.
   *
   * @return the Boolean
   */
  public boolean getBoolean(int index) {
    if (index < 0 || size() <= index) {
      throw new MissingItemException(index, Primitive.IS_BOOLEAN);
    }
    JsonValue primitive = get(index);
    Object value = Primitive.getValue(primitive);
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    throw new IncorrectTypeException(index, Primitive.IS_BOOLEAN, primitive.getValueType());
  }


  /**
   * Get a double from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the double, or the default
   */
  public double getDouble(int index, double dflt) {
    Double n = optDouble(index);
    return (n != null) ? n.doubleValue() : dflt;
  }


  /**
   * Get a double from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the double, or the default
   */
  public double getDouble(int index, IntToDoubleFunction dflt) {
    Double n = optDouble(index);
    return (n != null) ? n.doubleValue() : dflt.applyAsDouble(index);
  }


  /**
   * Get a double from the array.
   *
   * @return the double
   */
  public double getDouble(int index) {
    if (index < 0 || size() <= index) {
      throw new MissingItemException(index, ValueType.NUMBER);
    }
    JsonValue primitive = get(index);
    Double d = PNumber.toDouble(primitive);
    if (d != null) {
      return d;
    }
    throw new IncorrectTypeException(index, ValueType.NUMBER, primitive.getValueType());
  }


  /**
   * Get an integer from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the integer, or the default
   */
  public int getInt(int index, int dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : dflt;
  }


  /**
   * Get an integer from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the integer, or the default
   */
  public int getInt(int index, IntUnaryOperator dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : dflt.applyAsInt(index);
  }


  /**
   * Get an integer from the array.
   *
   * @return the integer
   */
  @Override
  public int getInt(int index) {
    return getSafe(Number.class, ValueType.NUMBER, index).intValue();
  }


  @Override
  public JsonArray getJsonArray(int index) {
    return getArray(index);
  }


  @Override
  public PNumber getJsonNumber(int index) {
    return (PNumber) get(index);
  }


  @Override
  public JsonObject getJsonObject(int index) {
    return getObject(index);
  }


  @Override
  public PString getJsonString(int index) {
    return (PString) get(index);
  }


  /**
   * Get a long from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the long, or the default
   */
  public long getLong(int index, IntToLongFunction dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : dflt.applyAsLong(index);
  }


  /**
   * Get a long from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the long, or the default
   */
  public long getLong(int index, long dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : dflt;
  }


  /**
   * Get a long from the array.
   *
   * @return the long
   */
  public long getLong(int index) {
    Number n = getSafe(Number.class, ValueType.NUMBER, index);
    return n.longValue();
  }


  /**
   * Get an object from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the object, or the default
   */
  public JsonObject getObject(int index, IntFunction<JsonObject> dflt) {
    return getQuiet(JsonObject.class, index, dflt);
  }


  /**
   * Get an object from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the object, or the default
   */
  public JsonObject getObject(int index, JsonObject dflt) {
    return getQuiet(JsonObject.class, index, dflt);
  }


  /**
   * Get an object from the array.
   *
   * @return the object
   */
  @Nonnull
  public JsonObject getObject(int index) {
    return getSafe(JsonObject.class, ValueType.OBJECT, index);
  }


  private <T> T getQuiet(Class<T> clazz, int index) {
    return getQuiet(clazz, index, (IntFunction<T>) i -> null);
  }


  private <T> T getQuiet(Class<T> clazz, int index, IntFunction<T> function) {
    if (index < 0 || size() <= index) {
      return function.apply(index);
    }
    Object value = Primitive.getValue(get(index));
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    return function.apply(index);
  }


  private <T> T getQuiet(Class<T> clazz, int index, T dflt) {
    return getQuiet(clazz, index, (IntFunction<T>) i -> dflt);
  }


  private <T> T getSafe(Class<T> clazz, ValueType type, int index) {
    if (index < 0 || size() <= index) {
      throw new MissingItemException(index, type);
    }
    JsonValue primitive = get(index);
    Object value = Primitive.getValue(primitive);
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    throw new IncorrectTypeException(index, type, primitive.getValueType());
  }


  /**
   * Get a String from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the String, or the default
   */
  public String getString(int index, IntFunction<String> dflt) {
    return getQuiet(String.class, index, dflt);
  }


  /**
   * Get a String from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the String, or the default
   */
  public String getString(int index, String dflt) {
    return getQuiet(String.class, index, dflt);
  }


  /**
   * Get a String from the array.
   *
   * @return the String
   */
  @Nonnull
  @Override
  public String getString(int index) {
    return getSafe(String.class, ValueType.STRING, index);
  }


  @Override
  public <T> T getValue(Class<T> reqType, T dflt) {
    if (reqType.isInstance(this)) {
      return reqType.cast(this);
    }
    return dflt;
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
    return ValueType.ARRAY;
  }


  @Override
  public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
    return getValuesAs(clazz::cast);
  }


  @Override
  public <T, K extends JsonValue> List<T> getValuesAs(Function<K, T> func) {
    ArrayList<T> array = new ArrayList<>(size());
    forEach(jv -> {
      @SuppressWarnings("unchecked")
      K kv = (K) jv;
      array.add(func.apply(kv));
    });
    return array;
  }


  public boolean isArray() {
    return true;
  }


  @Override
  public boolean isNull(int index) {
    return get(index).getValueType().equals(ValueType.NULL);
  }


  /**
   * Get an array from the array.
   *
   * @return the array, or null
   */
  @Nullable
  public JsonArray optArray(int index) {
    return getQuiet(JsonArray.class, index);
  }


  /**
   * Get a big decimal from the array.
   *
   * @return the big decimal, or null
   */
  @Nullable
  public BigDecimal optBigDecimal(int index) {
    Number n = getQuiet(Number.class, index);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @return the big integer, or null
   */
  @Nonnull
  public BigInteger optBigInteger(int index) {
    Number n = getQuiet(Number.class, index);
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a Boolean from the array.
   *
   * @return the Boolean, or null
   */
  @Nullable
  public Boolean optBoolean(int index) {
    return getQuiet(Boolean.class, index);
  }


  /**
   * Get a double from the array.
   *
   * @return the double, or null
   */
  @Nullable
  public Double optDouble(int index) {
    if (index < 0 || size() <= index) {
      return null;
    }
    JsonValue p = get(index);
    return PNumber.toDouble(p);
  }


  /**
   * Get an integer from the array.
   *
   * @return the integer, or null
   */
  @Nullable
  public Integer optInt(int index) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : null;
  }


  /**
   * Get a long from the array.
   *
   * @return the long, or null
   */
  @Nullable
  public Long optLong(int index) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : null;
  }


  /**
   * Get an object from the array.
   *
   * @return the object, or null
   */
  @Nullable
  public JsonObject optObject(int index) {
    return getQuiet(JsonObject.class, index);
  }


  /**
   * Get a String from the array.
   *
   * @return the String, or null
   */
  @Nullable
  public String optString(int index) {
    return getQuiet(String.class, index);
  }


  /**
   * Get the primitive at the given array index. Will be null if the index is out of range.
   *
   * @param i the array index
   *
   * @return the primitive or null.
   */
  @Nullable
  public JsonValue optValue(int i) {
    if (i < 0 || size() <= i) {
      return null;
    }
    return get(i);
  }


  /**
   * Ensure that all Strings and Numbers have a single representation in memory.
   */
  public void optimiseStorage() {
    optimiseStorage(new HashMap<>());
  }


  /**
   * Ensure that all Strings and Numbers have a single representation in memory.
   * @param values the unique values
   */
  void optimiseStorage(HashMap<JsonValue, JsonValue> values) {
    ListIterator<JsonValue> iterator = this.listIterator();
    while (iterator.hasNext()) {
      JsonValue current = iterator.next();
// TODO decide if useful
      if( current.getValueType()==ValueType.OBJECT ) {

      }
      JsonValue use = values.computeIfAbsent(current, v->v);
      iterator.set(use);
    }
  }


  @Override
  public void replaceAll(UnaryOperator<JsonValue> operator) {
    super.replaceAll(p -> Primitive.cast(operator.apply(p)));
  }


  @Override
  @Nonnull
  public JsonValue set(int index, JsonValue element) {
    return super.set(index, Primitive.cast(element));
  }


  @Nonnull
  public JsonValue set(int index, Boolean value) {
    JsonValue p = (value != null) ? (value ? PTrue.TRUE : PFalse.FALSE) : PNull.NULL;
    return super.set(index, p);
  }


  @Nonnull
  public JsonValue set(int index, Number number) {
    JsonValue p = (number != null) ? PNumber.create(number) : PNull.NULL;
    return super.set(index, p);
  }


  @Nonnull
  public JsonValue set(int index, String string) {
    JsonValue p = (string != null) ? new PString(string) : PNull.NULL;
    return super.set(index, p);
  }


  @Nonnull
  public JsonValue setNull(int index) {
    return super.set(index, PNull.NULL);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    for (JsonValue e : this) {
      buf.append(e);
      buf.append(',');
    }
    // remove final comma
    if (buf.length() > 1) {
      buf.setLength(buf.length() - 1);
    }
    buf.append(']');
    return buf.toString();
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    writer.append('[');
    int length = size();
    for (int i = 0; i < length; i++) {
      if (i > 0) {
        writer.append(',');
      }
      Primitive.cast(get(i)).writeTo(writer);
    }
    writer.append(']');
  }
}
