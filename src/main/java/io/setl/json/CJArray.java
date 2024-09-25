package io.setl.json;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import io.setl.json.io.Generator;
import io.setl.json.jackson.JsonArraySerializer;
import io.setl.json.primitive.CJNull;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.CJTrue;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * Representation of an array in JSON.
 *
 * <p>No entry in the list can be null. If you try to add one, it will be replaced by a Canonical instance holding a null.
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
 * <li>If the index is less than zero, or greater than or equal to the size of this array, throws a {@link MissingItemException}.
 * <li>If the entry is not the required type, throws an {@link IncorrectTypeException}.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * </dl>
 *
 * <p>The numeric accessors follow the normal Java rules for primitive type conversions and consider any number to be the correct type. For example, if you
 * call {@link #getInt(int)} and the element contains the Long value 1L&lt;&lt;50, then the call returns the value of Integer.MAX_VALUE, as would be expected
 * for a narrowing primitive conversion, rather than throwing a <code>IncorrectTypeException</code>.
 */
@JsonSerialize(using = JsonArraySerializer.class)
public class CJArray implements JsonArray, Canonical {

  static class MyIterator implements ListIterator<JsonValue> {

    private final ListIterator<Canonical> me;


    MyIterator(ListIterator<Canonical> me) {
      this.me = me;
    }


    @Override
    public void add(JsonValue jsonValue) {
      me.add(Canonical.cast(jsonValue));
    }


    @Override
    public boolean hasNext() {
      return me.hasNext();
    }


    @Override
    public boolean hasPrevious() {
      return me.hasPrevious();
    }


    @Override
    public JsonValue next() {
      return me.next();
    }


    @Override
    public int nextIndex() {
      return me.nextIndex();
    }


    @Override
    public JsonValue previous() {
      return me.previous();
    }


    @Override
    public int previousIndex() {
      return me.previousIndex();
    }


    @Override
    public void remove() {
      me.remove();
    }


    @Override
    public void set(JsonValue jsonValue) {
      me.set(Canonical.cast(jsonValue));
    }

  }



  /**
   * Spliterator over JsonValues instead of Canonicals. I'm not sure why Java requires a wrapper as every Canonical is a JsonValue, but it is happier with one.
   */
  static class MySpliterator implements Spliterator<JsonValue> {

    private final Spliterator<Canonical> me;


    MySpliterator(Spliterator<Canonical> me) {
      this.me = me;
    }


    @Override
    public int characteristics() {
      return me.characteristics();
    }


    @Override
    public long estimateSize() {
      return me.estimateSize();
    }


    @Override
    public long getExactSizeIfKnown() {
      return me.getExactSizeIfKnown();
    }


    @Override
    public boolean tryAdvance(Consumer<? super JsonValue> action) {
      return me.tryAdvance(action);
    }


    @Override
    public Spliterator<JsonValue> trySplit() {
      Spliterator<Canonical> newSplit = me.trySplit();
      if (newSplit != null) {
        return new MySpliterator(newSplit);
      }
      return null;
    }

  }


  /**
   * Convert a collection into a JsonArray.
   *
   * @param c the collection
   *
   * @return the JsonArray
   */
  public static CJArray asArray(Collection<?> c) {
    if (c instanceof CJArray) {
      return (CJArray) c;
    }
    CJArray out = new CJArray(c.size());
    for (Object o : c) {
      out.add(Canonical.cast(o));
    }
    return out;
  }


  /**
   * Create an object, that must be some kind of array, into a canonical JSON array.
   *
   * @param value the object, that must be an array
   *
   * @return the
   */
  public static CJArray asArrayFromArray(Object value) {
    Objects.requireNonNull(value);
    if (!value.getClass().isArray()) {
      throw new IllegalArgumentException("Value " + value.getClass() + " is not an array");
    }
    int length = Array.getLength(value);
    CJArray out = new CJArray(length);
    for (int i = 0; i < length; i++) {
      out.add(Canonical.cast(Array.get(value, i)));
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
  static Collection<Canonical> fixCollection(Collection<? extends JsonValue> c) {
    if (c instanceof CJArray) {
      // already fixed
      return ((CJArray) c).myList;
    }

    ArrayList<Canonical> list = new ArrayList<>(c.size());
    for (JsonValue jv : c) {
      list.add(Canonical.cast(jv));
    }
    return list;
  }


  private final List<Canonical> myList;


  /** New instance. */
  public CJArray() {
    myList = new ArrayList<>();
  }


  /**
   * New instance.
   *
   * @param size the initial capacity
   */
  public CJArray(int size) {
    myList = new ArrayList<>(size);
  }


  /**
   * New instance containing the JSON representation of the collection's elements.
   *
   * @param c the collection of values
   */
  public CJArray(Collection<?> c) {
    myList = new ArrayList<>(asArray(c).myList);
  }


  private CJArray(CJArray jsonValues, int fromIndex, int toIndex) {
    myList = jsonValues.myList.subList(fromIndex, toIndex);
  }


  /**
   * Add a boolean to the end of this array.
   *
   * @param value the value
   *
   * @return true
   */
  public boolean add(Boolean value) {
    return add(CJTrue.valueOf(value));
  }


  /**
   * Add a boolean at the specified index in this array.
   *
   * @param index the index
   * @param value the value
   */
  public void add(int index, Boolean value) {
    add(index, CJTrue.valueOf(value));
  }


  /**
   * Add a number at the specified index in this array.
   *
   * @param index  the index
   * @param number the number
   */
  public void add(int index, Number number) {
    add(index, number != null ? CJNumber.cast(number) : CJNull.NULL);
  }


  @Override
  public void add(int index, JsonValue element) {
    myList.add(index, Canonical.cast(element));
  }


  /**
   * Add a string at the specified index in this array.
   *
   * @param index  the index
   * @param string the string
   */
  public void add(int index, String string) {
    add(index, string != null ? CJString.create(string) : CJNull.NULL);
  }


  /**
   * Addd a number to the end of this array.
   *
   * @param number the number
   *
   * @return true
   */
  public boolean add(Number number) {
    return add(number != null ? CJNumber.cast(number) : CJNull.NULL);
  }


  @Override
  public boolean add(JsonValue e) {
    return myList.add(Canonical.cast(e));
  }


  /**
   * Add a string to the end of this array.
   *
   * @param string the string
   *
   * @return true
   */
  public boolean add(String string) {
    return add(string != null ? CJString.create(string) : CJNull.NULL);
  }


  @Override
  public boolean addAll(@Nonnull Collection<? extends JsonValue> c) {
    return myList.addAll(fixCollection(c));
  }


  @Override
  public boolean addAll(int index, @Nonnull Collection<? extends JsonValue> c) {
    return myList.addAll(index, fixCollection(c));
  }


  /**
   * Add a null to the end of this array.
   *
   * @return true
   */
  public boolean addNull() {
    return add(CJNull.NULL);
  }


  /**
   * Add a null to the array at the given index.
   *
   * @param index the index
   */
  public void addNull(int index) {
    add(index, CJNull.NULL);
  }


  @Override
  public CJArray asJsonArray() {
    return this;
  }


  /**
   * Offer every element of the collection to the provided consumer as a canonical JSON value.
   *
   * @param action the consumer
   */
  public void canonicalForEach(Consumer<? super Canonical> action) {
    myList.forEach(action);
  }


  /**
   * A list iterator across the canonical representation of the values in this.
   *
   * @return the list iterator
   */
  public ListIterator<Canonical> canonicalListIterator() {
    return myList.listIterator();
  }


  /**
   * A list iterator across the canonical representation of the values in this.
   *
   * @param index the index to start at
   *
   * @return the list iterator
   */
  public ListIterator<Canonical> canonicalListIterator(int index) {
    return myList.listIterator(index);
  }


  @Override
  public void clear() {
    myList.clear();
  }


  @Override
  public boolean contains(Object o) {
    return myList.contains(Canonical.cast(o));
  }


  @Override
  public boolean containsAll(@Nonnull Collection<?> c) {
    for (Object o : c) {
      if (!myList.contains(Canonical.cast(o))) {
        return false;
      }
    }
    return true;
  }


  @Override
  public CJArray copy() {
    CJArray other = new CJArray(this);
    other.replaceAll(Canonical::create);
    return other;
  }


  /**
   * Ensure the underlying list has enough capacity to store the requested number of entries, if possible.
   *
   * @param size the requested number of entries to accommodate.
   */
  public void ensureCapacity(int size) {
    // Cannot ensure capacity of sub-lists.
    if (myList instanceof ArrayList) {
      ((ArrayList<?>) myList).ensureCapacity(size);
    }
  }


  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return myList.equals(o);
  }


  @Override
  public void forEach(Consumer<? super JsonValue> action) {
    myList.forEach(action);
  }


  @Override
  public JsonValue get(int index) {
    return myList.get(index);
  }


  /**
   * Get an array from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the array, or the default
   */
  public JsonArray getArray(int index, IntFunction<JsonArray> defaultValue) {
    return getQuiet(JsonArray.class, index, defaultValue);
  }


  /**
   * Get an array from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the array, or the default
   */
  public JsonArray getArray(int index, JsonArray defaultValue) {
    return getQuiet(JsonArray.class, index, defaultValue);
  }


  /**
   * Get an array from the array.
   *
   * @param index the values index in the array
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
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(int index, BigDecimal defaultValue) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return defaultValue;
    }
    return Canonical.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(int index, IntFunction<BigDecimal> defaultValue) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return defaultValue.apply(index);
    }
    return Canonical.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the array.
   *
   * @param index the values index in the array
   *
   * @return the big decimal
   */
  @Nonnull
  public BigDecimal getBigDecimal(int index) {
    Number n = getSafe(Number.class, ValueType.NUMBER, index);
    return Canonical.toBigDecimal(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(int index, BigInteger defaultValue) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return defaultValue;
    }
    return Canonical.toBigInteger(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(int index, IntFunction<BigInteger> defaultValue) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return defaultValue.apply(index);
    }
    return Canonical.toBigInteger(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @param index the values index in the array
   *
   * @return the big integer
   */
  @Nonnull
  public BigInteger getBigInteger(int index) {
    Number n = getSafe(Number.class, ValueType.NUMBER, index);
    return Canonical.toBigInteger(n);
  }


  /**
   * Get a Boolean from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(int index, boolean defaultValue) {
    return getQuiet(Boolean.class, index, defaultValue);
  }


  /**
   * Get a Boolean from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(int index, IntPredicate defaultValue) {
    Boolean value = getQuiet(Boolean.class, index);
    return (value != null) ? value : defaultValue.test(index);
  }


  /**
   * Get a Boolean from the array.
   *
   * @param index the values index in the array
   *
   * @return the Boolean
   */
  public boolean getBoolean(int index) {
    if (index < 0 || size() <= index) {
      throw new MissingItemException(index, IS_BOOLEAN);
    }
    JsonValue canonical = get(index);
    Object value = Canonical.getValue(canonical);
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    throw new IncorrectTypeException(index, IS_BOOLEAN, canonical.getValueType());
  }


  /**
   * Get the canonical JSON value at index <code>i</code>.
   *
   * @param i the index
   *
   * @return the canonical JSON value
   */
  public Canonical getCanonical(int i) {
    return myList.get(i);
  }


  /**
   * Get a double from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the double, or the default
   */
  public double getDouble(int index, double defaultValue) {
    Double n = optDouble(index);
    return (n != null) ? n : defaultValue;
  }


  /**
   * Get a double from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the double, or the default
   */
  public double getDouble(int index, IntToDoubleFunction defaultValue) {
    Double n = optDouble(index);
    return (n != null) ? n : defaultValue.applyAsDouble(index);
  }


  /**
   * Get a double from the array.
   *
   * @param index the values index in the array
   *
   * @return the double
   */
  public double getDouble(int index) {
    if (index < 0 || size() <= index) {
      throw new MissingItemException(index, ValueType.NUMBER);
    }
    JsonValue primitive = get(index);
    Double d = CJNumber.toDouble(primitive);
    if (d != null) {
      return d;
    }
    throw new IncorrectTypeException(index, ValueType.NUMBER, primitive.getValueType());
  }


  /**
   * Get an integer from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the integer, or the default
   */
  public int getInt(int index, int defaultValue) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : defaultValue;
  }


  /**
   * Get an integer from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the integer, or the default
   */
  public int getInt(int index, IntUnaryOperator defaultValue) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : defaultValue.applyAsInt(index);
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
  public JsonNumber getJsonNumber(int index) {
    return (JsonNumber) myList.get(index);
  }


  @Override
  public JsonObject getJsonObject(int index) {
    return getObject(index);
  }


  @Override
  public JsonString getJsonString(int index) {
    return (JsonString) myList.get(index);
  }


  /**
   * Get a long from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the long, or the default
   */
  public long getLong(int index, IntToLongFunction defaultValue) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : defaultValue.applyAsLong(index);
  }


  /**
   * Get a long from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the long, or the default
   */
  public long getLong(int index, long defaultValue) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : defaultValue;
  }


  /**
   * Get a long from the array.
   *
   * @param index the values index in the array
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
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the object, or the default
   */
  public JsonObject getObject(int index, IntFunction<JsonObject> defaultValue) {
    return getQuiet(JsonObject.class, index, defaultValue);
  }


  /**
   * Get an object from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the object, or the default
   */
  public JsonObject getObject(int index, JsonObject defaultValue) {
    return getQuiet(JsonObject.class, index, defaultValue);
  }


  /**
   * Get an object from the array.
   *
   * @param index the values index in the array
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
    Object value = myList.get(index).getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    return function.apply(index);
  }


  private <T> T getQuiet(Class<T> clazz, int index, T defaultValue) {
    return getQuiet(clazz, index, (IntFunction<T>) i -> defaultValue);
  }


  private <T> T getSafe(Class<T> clazz, ValueType type, int index) {
    if (index < 0 || size() <= index) {
      throw new MissingItemException(index, type);
    }
    Canonical canonical = myList.get(index);
    Object value = canonical.getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    throw new IncorrectTypeException(index, type, canonical.getValueType());
  }


  /**
   * Get a String from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the String, or the default
   */
  public String getString(int index, IntFunction<String> defaultValue) {
    return getQuiet(String.class, index, defaultValue);
  }


  /**
   * Get a String from the array.
   *
   * @param index        the index
   * @param defaultValue the default
   *
   * @return the String, or the default
   */
  public String getString(int index, String defaultValue) {
    return getQuiet(String.class, index, defaultValue);
  }


  /**
   * Get a String from the array.
   *
   * @param index the values index in the array
   *
   * @return the String
   */
  @Nonnull
  @Override
  public String getString(int index) {
    return getSafe(String.class, ValueType.STRING, index);
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


  @Override
  public int hashCode() {
    return myList.hashCode();
  }


  @Override
  public int indexOf(Object o) {
    return myList.indexOf(Canonical.cast(o));
  }


  @Override
  public boolean isEmpty() {
    return myList.isEmpty();
  }


  @Override
  public boolean isNull(int index) {
    return get(index).getValueType().equals(ValueType.NULL);
  }


  @Override
  @Nonnull
  public Iterator<JsonValue> iterator() {
    return listIterator(0);
  }


  @Override
  public int lastIndexOf(Object o) {
    return myList.lastIndexOf(Canonical.cast(o));
  }


  @Override
  @Nonnull
  public ListIterator<JsonValue> listIterator() {
    return listIterator(0);
  }


  @Override
  @Nonnull
  public ListIterator<JsonValue> listIterator(int index) {
    return new MyIterator(myList.listIterator(index));
  }


  /**
   * Get an array from the array.
   *
   * @param index the values index in the array
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
   * @param index the values index in the array
   *
   * @return the big decimal, or null
   */
  @Nullable
  public BigDecimal optBigDecimal(int index) {
    Number n = getQuiet(Number.class, index);
    return Canonical.toBigDecimal(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @param index the values index in the array
   *
   * @return the big integer, or null
   */
  @Nonnull
  public BigInteger optBigInteger(int index) {
    Number n = getQuiet(Number.class, index);
    return Canonical.toBigInteger(n);
  }


  /**
   * Get a Boolean from the array.
   *
   * @param index the values index in the array
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
   * @param index the values index in the array
   *
   * @return the double, or null
   */
  @Nullable
  public Double optDouble(int index) {
    if (index < 0 || size() <= index) {
      return null;
    }
    JsonValue p = get(index);
    return CJNumber.toDouble(p);
  }


  /**
   * Get an integer from the array.
   *
   * @param index the values index in the array
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
   * @param index the values index in the array
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
   * @param index the values index in the array
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
   * @param index the values index in the array
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
   *
   * @param values the unique values
   */
  void optimiseStorage(HashMap<Canonical, Canonical> values) {
    ListIterator<Canonical> iterator = myList.listIterator();
    while (iterator.hasNext()) {
      Canonical current = iterator.next();
      switch (current.getValueType()) {
        case ARRAY:
          // recurse into array
          ((CJArray) current).optimiseStorage(values);
          break;
        case OBJECT:
          ((CJObject) current).optimiseStorage(values);
          break;
        default:
          Canonical single = values.computeIfAbsent(current, c -> c);
          if (single != current) {
            iterator.set(single);
          }
          break;
      }
    }
  }


  @Override
  public Stream<JsonValue> parallelStream() {
    return myList.parallelStream().map(JsonValue.class::cast);
  }


  @Override
  public JsonValue remove(int index) {
    return myList.remove(index);
  }


  @Override
  public boolean remove(Object o) {
    return myList.remove(Canonical.cast(o));
  }


  @Override
  public boolean removeAll(@Nonnull Collection<?> c) {
    boolean b = false;
    for (Object o : c) {
      if (myList.remove(Canonical.cast(o))) {
        b = true;
      }
    }
    return b;
  }


  @Override
  public boolean removeIf(Predicate<? super JsonValue> filter) {
    return myList.removeIf(filter);
  }


  @Override
  public void replaceAll(UnaryOperator<JsonValue> operator) {
    myList.replaceAll(p -> Canonical.cast(operator.apply(p)));
  }


  @Override
  public boolean retainAll(@Nonnull Collection<?> c) {
    HashSet<Canonical> set = new HashSet<>();
    for (Object o : c) {
      set.add(Canonical.cast(o));
    }
    return myList.retainAll(set);
  }


  @Override
  @Nonnull
  public JsonValue set(int index, JsonValue element) {
    return myList.set(index, Canonical.cast(element));
  }


  /**
   * Set the value at the given index to the specified boolean, returning the old value.
   *
   * @param index the index to set
   * @param value the boolean to set
   *
   * @return the old value
   */
  @Nonnull
  public JsonValue set(int index, Boolean value) {
    return myList.set(index, CJTrue.valueOf(value));
  }


  /**
   * Set the value at the given index to the specified number, returning the old value.
   *
   * @param index  the index to set
   * @param number the number to set
   *
   * @return the old value
   */
  @Nonnull
  public JsonValue set(int index, Number number) {
    Canonical p = (number != null) ? CJNumber.cast(number) : CJNull.NULL;
    return myList.set(index, p);
  }


  /**
   * Set the value at the given index to the specified string, returning the old value.
   *
   * @param index  the index to set
   * @param string the string to set
   *
   * @return the old value
   */
  @Nonnull
  public JsonValue set(int index, String string) {
    Canonical p = (string != null) ? CJString.create(string) : CJNull.NULL;
    return myList.set(index, p);
  }


  /**
   * Set the value at the given index to null, returning the old value.
   *
   * @param index the index
   *
   * @return the old value
   */
  @Nonnull
  public JsonValue setNull(int index) {
    return myList.set(index, CJNull.NULL);
  }


  @Override
  public int size() {
    return myList.size();
  }


  @Override
  public void sort(Comparator<? super JsonValue> c) {
    myList.sort(c);
  }


  @Override
  public Spliterator<JsonValue> spliterator() {
    return new MySpliterator(myList.spliterator());
  }


  @Override
  public Stream<JsonValue> stream() {
    return myList.stream().map(JsonValue.class::cast);
  }


  @Override
  @Nonnull
  public List<JsonValue> subList(int fromIndex, int toIndex) {
    return new CJArray(this, fromIndex, toIndex);
  }


  @Override
  @Nonnull
  public Object[] toArray() {
    return myList.toArray();
  }


  @Override
  @Nonnull
  public <T> T[] toArray(@Nonnull T[] a) {
    return myList.toArray(a);
  }


  @Override
  public <T> T[] toArray(IntFunction<T[]> generator) {
    return myList.toArray(generator);
  }


  @Override
  public String toCanonicalString() {
    StringBuilder buf = new StringBuilder();
    Generator<?> generator = CanonicalJsonProvider.CANONICAL_GENERATOR_FACTORY.createGenerator(buf);
    generator.writeStartArray();
    for (Canonical c : myList) {
      generator.write(c);
    }
    generator.writeEnd();
    generator.close();
    return buf.toString();
  }


  @Override
  public String toPrettyString() {
    StringBuilder buf = new StringBuilder();
    Generator<?> generator = CanonicalJsonProvider.PRETTY_GENERATOR_FACTORY.createGenerator(buf);
    generator.writeStartArray();
    for (Canonical c : myList) {
      generator.write(c);
    }
    generator.writeEnd();
    generator.close();
    return buf.toString();
  }


  @Override
  public String toString() {
    return CanonicalJsonProvider.isToStringPretty ? toPrettyString() : toCanonicalString();
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    writer.append('[');
    int length = size();
    for (int i = 0; i < length; i++) {
      if (i > 0) {
        writer.append(',');
      }
      Canonical.cast(get(i)).writeTo(writer);
    }
    writer.append(']');
  }

}
