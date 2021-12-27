package io.setl.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.setl.json.CJArray.MySpliterator;
import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import io.setl.json.io.Generator;
import io.setl.json.jackson.JsonObjectSerializer;
import io.setl.json.primitive.CJFalse;
import io.setl.json.primitive.CJNull;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.CJTrue;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * Representation of an object in JSON.
 *
 * <p>No value in the object can be null. If you try to add one, it will be replaced by a Canonical instance holding a null.
 *
 * <p>As JSON objects can contain mixed content, this class provides type-checking accessors to the object members. There are multiple varieties of each
 * accessor which obey these contracts:
 *
 * <dl>
 * <dt><code>opt<i>Type</i>(key)</code></dt>
 * <dd>
 * <ul>
 * <li>If the key is not present, returns null.</li>
 * <li>If the entry is not the required type, returns null.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 *
 * <dt><code>get<i>Type</i>(key, default)</code></dt>
 * <dd>
 * <ul>
 * <li>If the key is not present, returns the default.
 * <li>If the entry is not the required type, returns the default.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * <dt><code>get<i>Type</i>(key, function)</code></dt>
 * <dd>
 * <ul>
 * <li>If the key is not present, invokes the function to derive a suitable value.
 * <li>If the entry is not the required type, invokes the function to derive a suitable value.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * <dt><code>get<i>Type</i>(key)</code></dt>
 * <dd>
 * <ul>
 * <li>If the key is not present, throws a {@link MissingItemException}.
 * <li>If the entry is not the required type, throws an {@link IncorrectTypeException}.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * </dl>
 *
 * <p>The numeric accessors follow the normal Java rules for primitive type conversions and consider any number to be the correct type. For example, if you
 * call {@link #getInt(String)} and the value is the Long value 1L&lt;&lt;50, then the call returns the value of Integer.MAX_VALUE, as would be expected
 * for a narrowing primitive conversion, rather than throwing a {@link IncorrectTypeException}.
 */

@JsonSerialize(using = JsonObjectSerializer.class)
public class CJObject implements NavigableMap<String, JsonValue>, JsonObject, Canonical {

  /**
   * Sort object keys into Unicode code point order.
   */
  @SuppressWarnings("java:S127") // Allow incrementing loop counter inside loop as code points can be one or two characters.
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
   * Set which converts JsonValue to Canonicals.
   */
  static class MyEntries implements Set<Entry<String, JsonValue>> {

    private final Set<Entry<String, Canonical>> mySet;


    MyEntries(Set<Entry<String, Canonical>> mySet) {
      this.mySet = mySet;
    }


    @Override
    public boolean add(Entry<String, JsonValue> entry) {
      throw new UnsupportedOperationException();
    }


    @Override
    public boolean addAll(@Nonnull Collection<? extends Entry<String, JsonValue>> c) {
      throw new UnsupportedOperationException();
    }


    @Override
    public void clear() {
      mySet.clear();
    }


    @Override
    public boolean contains(Object o) {
      if (!(o instanceof Entry<?, ?>)) {
        return false;
      }
      Entry<?, ?> e = (Entry<?, ?>) o;
      if (!(e.getKey() instanceof String)) {
        return false;
      }
      Object v = e.getValue();
      if (v instanceof Canonical) {
        return mySet.contains(e);
      }
      if (v != null && !(v instanceof JsonValue)) {
        return false;
      }
      return mySet.contains(new SimpleEntry<>(e.getKey(), Canonical.cast((JsonValue) e.getValue())));
    }


    @Override
    public boolean containsAll(Collection<?> c) {
      for (Object o : c) {
        if (!contains(o)) {
          return false;
        }
      }
      return true;
    }


    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
      return mySet.equals(o);
    }


    @Override
    public int hashCode() {
      return mySet.hashCode();
    }


    @Override
    public boolean isEmpty() {
      return mySet.isEmpty();
    }


    @Override
    @Nonnull
    public Iterator<Entry<String, JsonValue>> iterator() {
      final Iterator<Entry<String, Canonical>> myIterator = mySet.iterator();
      return new Iterator<>() {

        @Override
        public boolean hasNext() {
          return myIterator.hasNext();
        }


        @Override
        public Entry<String, JsonValue> next() {
          return new MyEntry(myIterator.next());
        }


        @Override
        public void remove() {
          myIterator.remove();
        }
      };
    }


    @Override
    public Stream<Entry<String, JsonValue>> parallelStream() {
      return mySet.parallelStream().map(MyEntry::new);
    }


    @Override
    public boolean remove(Object o) {
      return mySet.remove(o);
    }


    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
      return mySet.removeAll(c);
    }


    @Override
    public boolean removeIf(Predicate<? super Entry<String, JsonValue>> filter) {
      return mySet.removeIf(e -> filter.test(new MyEntry(e)));
    }


    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
      return mySet.retainAll(c);
    }


    @Override
    public int size() {
      return mySet.size();
    }


    @Override
    public Spliterator<Entry<String, JsonValue>> spliterator() {
      return new MyEntrySpliterator(mySet.spliterator());
    }


    @Override
    public Stream<Entry<String, JsonValue>> stream() {
      return mySet.stream().map(MyEntry::new);
    }


    @Override
    @Nonnull
    public Object[] toArray() {
      return mySet.toArray();
    }


    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    @Nonnull
    public <T> T[] toArray(@Nonnull T[] a) {
      return mySet.toArray(a);
    }


    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    @Nonnull
    public <T> T[] toArray(@Nonnull IntFunction<T[]> generator) {
      return mySet.toArray(generator);
    }


    @Override
    public String toString() {
      return mySet.toString();
    }

  }



  static class MyEntry implements Entry<String, JsonValue> {

    private final Entry<String, Canonical> me;


    public MyEntry(Entry<String, Canonical> me) {
      this.me = me;
    }


    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      return me.equals(o);
    }


    @Override
    public String getKey() {
      return me.getKey();
    }


    @Override
    public JsonValue getValue() {
      return me.getValue();
    }


    @Override
    public int hashCode() {
      return me.hashCode();
    }


    @Override
    public JsonValue setValue(JsonValue value) {
      return me.setValue(Canonical.cast(value));
    }


    @Override
    public String toString() {
      return String.valueOf(me);
    }

  }



  /**
   * Spliterator over JsonValues instead of Canonical. If someone called setValue on the output, we have to convert to a Canonical.
   */
  static class MyEntrySpliterator implements Spliterator<Entry<String, JsonValue>> {


    private final Spliterator<Entry<String, Canonical>> me;


    MyEntrySpliterator(Spliterator<Entry<String, Canonical>> me) {
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
    public boolean tryAdvance(Consumer<? super Entry<String, JsonValue>> action) {
      return me.tryAdvance(e -> action.accept(new MyEntry(e)));
    }


    @Override
    public Spliterator<Entry<String, JsonValue>> trySplit() {
      Spliterator<Entry<String, Canonical>> newSplit = me.trySplit();
      if (newSplit != null) {
        return new MyEntrySpliterator(newSplit);
      }
      return null;
    }

  }



  private static class MyValues implements Collection<JsonValue> {

    private final Collection<Canonical> me;


    MyValues(Collection<Canonical> me) {
      this.me = me;
    }


    public boolean add(JsonValue value) {
      throw new UnsupportedOperationException("Add is not supported on a map's values");
    }


    public boolean addAll(@Nonnull Collection<? extends JsonValue> c) {
      throw new UnsupportedOperationException("Add is not supported on a map's values");
    }


    @Override
    public void clear() {
      me.clear();
    }


    @Override
    public boolean contains(Object o) {
      return me.contains(o);
    }


    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
      return me.containsAll(c);
    }


    @Override
    public boolean isEmpty() {
      return me.isEmpty();
    }


    @Override
    @Nonnull
    public Iterator<JsonValue> iterator() {
      final Iterator<Canonical> myIterator = me.iterator();
      return new Iterator<>() {
        @Override
        public boolean hasNext() {
          return myIterator.hasNext();
        }


        @Override
        public JsonValue next() {
          return myIterator.next();
        }


        @Override
        public void remove() {
          myIterator.remove();
        }
      };
    }


    @Override
    public Stream<JsonValue> parallelStream() {
      return me.parallelStream().map(JsonValue.class::cast);
    }


    @Override
    public boolean remove(Object o) {
      return me.remove(o);
    }


    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
      return me.removeAll(c);
    }


    @Override
    public boolean removeIf(Predicate<? super JsonValue> filter) {
      return me.removeIf(filter);
    }


    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
      return me.retainAll(c);
    }


    @Override
    public int size() {
      return me.size();
    }


    @Override
    public Spliterator<JsonValue> spliterator() {
      return new MySpliterator(me.spliterator());
    }


    @Override
    public Stream<JsonValue> stream() {
      return me.stream().map(JsonValue.class::cast);
    }


    @Override
    @Nonnull
    public Object[] toArray() {
      return me.toArray();
    }


    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    @Nonnull
    public <T> T[] toArray(@Nonnull T[] a) {
      return me.toArray(a);
    }


    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
      return me.toArray(generator);
    }

  }


  /**
   * Convert any map into a JObject.
   *
   * @param map the map to convert
   *
   * @return the equivalent JObject
   */
  public static CJObject asJObject(Map<?, ?> map) {
    if (map instanceof CJObject) {
      return (CJObject) map;
    }

    CJObject out = new CJObject();
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      Object key = entry.getKey();
      if (key == null) {
        throw new IllegalArgumentException("Map keys must not be null");
      }
      if (!(key instanceof String)) {
        throw new IllegalArgumentException("Map keys must be Strings, not " + key.getClass());
      }

      Object value = entry.getValue();
      Canonical canonical = Canonical.create(value);
      out.put((String) key, canonical);
    }
    return out;
  }


  private final NavigableMap<String, Canonical> myMap;


  public CJObject() {
    myMap = new TreeMap<>(CODE_POINT_ORDER);
  }


  /**
   * Create a new instance as a deep copy of the provided map.
   *
   * @param map the map to copy.
   */
  public CJObject(Map<String, ?> map) {
    myMap = new TreeMap<>(CODE_POINT_ORDER);
    for (Entry<String, ?> e : map.entrySet()) {
      myMap.put(e.getKey(), Canonical.create(e.getValue()));
    }
  }


  private CJObject(NavigableMap<String, Canonical> map, boolean makeCopy) {
    if (makeCopy) {
      myMap = new TreeMap<>(CODE_POINT_ORDER);
      for (Entry<String, Canonical> e : map.entrySet()) {
        myMap.put(e.getKey(), e.getValue().copy());
      }
    } else {
      myMap = map;
    }
  }


  @Override
  public CJObject asJsonObject() {
    return this;
  }


  public void canonicalForEach(BiConsumer<? super String, ? super Canonical> action) {
    myMap.forEach(action);
  }


  @Override
  public Entry<String, JsonValue> ceilingEntry(String key) {
    return new MyEntry(myMap.ceilingEntry(key));
  }


  @Override
  public String ceilingKey(String key) {
    return myMap.ceilingKey(key);
  }


  @Override
  public void clear() {
    myMap.clear();
  }


  @Override
  public Comparator<? super String> comparator() {
    return myMap.comparator();
  }


  @Override
  public Canonical compute(String key, BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
    final BiFunction<String, Canonical, Canonical> myFunction = (k, v) -> Canonical.cast(remappingFunction.apply(k, v));
    return myMap.compute(key, myFunction);
  }


  @Override
  public Canonical computeIfAbsent(String key, Function<? super String, ? extends JsonValue> mappingFunction) {
    final Function<String, Canonical> myFunction = k -> Canonical.cast(mappingFunction.apply(k));
    return myMap.computeIfAbsent(key, myFunction);
  }


  @Override
  public Canonical computeIfPresent(String key, BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
    final BiFunction<String, Canonical, Canonical> myFunction = (k, v) -> Canonical.cast(remappingFunction.apply(k, v));
    return myMap.computeIfPresent(key, myFunction);
  }


  @Override
  public boolean containsKey(Object key) {
    return myMap.containsKey(key);
  }


  @Override
  public boolean containsValue(Object value) {
    return myMap.containsValue(value);
  }


  @Override
  public CJObject copy() {
    return new CJObject(myMap, true);
  }


  @Override
  public NavigableSet<String> descendingKeySet() {
    return myMap.descendingKeySet();
  }


  @Override
  public NavigableMap<String, JsonValue> descendingMap() {
    return new CJObject(myMap.descendingMap(), false);
  }


  @Override
  @Nonnull
  public Set<Entry<String, JsonValue>> entrySet() {
    return new MyEntries(myMap.entrySet());
  }


  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    return myMap.equals(o);
  }


  @Override
  public Entry<String, JsonValue> firstEntry() {
    return new MyEntry(myMap.firstEntry());
  }


  @Override
  public String firstKey() {
    return myMap.firstKey();
  }


  @Override
  public Entry<String, JsonValue> floorEntry(String key) {
    return new MyEntry(myMap.floorEntry(key));
  }


  @Override
  public String floorKey(String key) {
    return myMap.floorKey(key);
  }


  @Override
  public void forEach(BiConsumer<? super String, ? super JsonValue> action) {
    myMap.forEach(action);
  }


  @Override
  public Canonical get(Object key) {
    return myMap.get(key);
  }


  /**
   * Get an array from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the array, or the default
   */
  public CJArray getArray(String key, @Nonnull Function<String, CJArray> defaultValue) {
    return getQuiet(CJArray.class, key, defaultValue);
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
  public CJArray getArray(String key, @Nonnull CJArray defaultValue) {
    return getQuiet(CJArray.class, key, defaultValue);
  }


  /**
   * Get an array from the object.
   *
   * @param key the entry's key
   *
   * @return the array
   */
  @Nonnull
  public CJArray getArray(String key) {
    return getSafe(CJArray.class, ValueType.ARRAY, key);
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
    return Canonical.toBigDecimal(n);
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
    return Canonical.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the object.
   *
   * @param key the entry's key
   *
   * @return the big decimal
   */
  @Nonnull
  public BigDecimal getBigDecimal(String key) {
    Number n = getSafe(Number.class, ValueType.NUMBER, key);
    return Canonical.toBigDecimal(n);
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
    return Canonical.toBigInteger(n);
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
    return Canonical.toBigInteger(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @param key the entry's key
   *
   * @return the big integer
   */
  @Nonnull
  public BigInteger getBigInteger(String key) {
    Number n = getSafe(Number.class, ValueType.NUMBER, key);
    return Canonical.toBigInteger(n);
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
    Canonical canonical = getCanonical(key);
    if (canonical == null) {
      throw new MissingItemException(key, Canonical.IS_BOOLEAN);
    }
    Object value = canonical.getValue();
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    throw new IncorrectTypeException(key, Canonical.IS_BOOLEAN, canonical.getValueType());
  }


  public Canonical getCanonical(String name) {
    return get(name);
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
   * @param key the entry's key
   *
   * @return the double
   */
  public double getDouble(String key) {
    Canonical p = getCanonical(key);
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
    Canonical p = getCanonical(name);
    return (p != null) ? (CJNumber) p : null;
  }


  @Override
  public JsonObject getJsonObject(String name) {
    return optObject(name);
  }


  @Override
  public JsonString getJsonString(String name) {
    Canonical p = getCanonical(name);
    return (p != null) ? (CJString) p : null;
  }


  /**
   * Get a JsonValue from this object. The value must exist.
   *
   * @param key the value's key
   *
   * @return the value
   */
  public JsonValue getJsonValue(String key) {
    Canonical canonical = getCanonical(key);
    if (canonical == null) {
      throw new MissingItemException(key, EnumSet.allOf(ValueType.class));
    }
    return canonical;
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
    Canonical canonical = getCanonical(key);
    if (canonical == null) {
      return defaultValue;
    }
    return canonical;
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
    Canonical canonical = getCanonical(key);
    if (canonical == null) {
      return defaultValue.apply(key);
    }
    return canonical;
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
   * @param key the entry's key
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
  public CJObject getObject(String key, @Nonnull Function<String, CJObject> defaultValue) {
    return getQuiet(CJObject.class, key, defaultValue);
  }


  /**
   * Get an object from the object.
   *
   * @param key          the key
   * @param defaultValue the default
   *
   * @return the object, or the default
   */
  public CJObject getObject(String key, CJObject defaultValue) {
    return getQuiet(CJObject.class, key, defaultValue);
  }


  /**
   * Get an object from the object.
   *
   * @param key the entry's key
   *
   * @return the object
   */
  @Nonnull
  public CJObject getObject(String key) {
    return getSafe(CJObject.class, ValueType.OBJECT, key);
  }


  @Override
  public JsonValue getOrDefault(Object key, JsonValue defaultValue) {
    JsonValue value = myMap.get(key);
    return value != null ? value : defaultValue;
  }


  private <T> T getQuiet(Class<T> clazz, String key) {
    return getQuiet(clazz, key, (Function<String, T>) k -> null);
  }


  private <T> T getQuiet(Class<T> clazz, String key, Function<String, T> function) {
    Canonical canonical = getCanonical(key);
    if (canonical == null) {
      return function.apply(key);
    }
    Object value = canonical.getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    return function.apply(key);
  }


  private <T> T getQuiet(Class<T> clazz, String key, T defaultValue) {
    return getQuiet(clazz, key, (Function<String, T>) k -> defaultValue);
  }


  private <T> T getSafe(Class<T> clazz, ValueType type, String key) {
    Canonical canonical = getCanonical(key);
    if (canonical == null) {
      throw new MissingItemException(key, type);
    }
    Object value = canonical.getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    throw new IncorrectTypeException(key, type, canonical.getValueType());
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


  @Override
  public int hashCode() {
    return myMap.hashCode();
  }


  @Override
  public NavigableMap<String, JsonValue> headMap(String toKey, boolean inclusive) {
    return new CJObject(myMap.headMap(toKey, inclusive), false);
  }


  @Override
  @Nonnull
  public SortedMap<String, JsonValue> headMap(String toKey) {
    return new CJObject(myMap.headMap(toKey, false), false);
  }


  @Override
  public Entry<String, JsonValue> higherEntry(String key) {
    return new MyEntry(myMap.higherEntry(key));
  }


  @Override
  public String higherKey(String key) {
    return myMap.higherKey(key);
  }


  @Override
  public boolean isEmpty() {
    return myMap.isEmpty();
  }


  @Override
  public boolean isNull(String name) {
    Canonical p = getCanonical(name);
    if (p == null) {
      throw new MissingItemException(name, ValueType.NULL);
    }
    return p.getValueType() == ValueType.NULL;
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
    Canonical canonical = getCanonical(key);
    if (canonical == null) {
      throw new MissingItemException(key, type);
    }
    return canonical.getValueType() == type;
  }


  @Override
  @Nonnull
  public Set<String> keySet() {
    return myMap.keySet();
  }


  @Override
  public Entry<String, JsonValue> lastEntry() {
    return new MyEntry(myMap.lastEntry());
  }


  @Override
  public String lastKey() {
    return myMap.lastKey();
  }


  @Override
  public Entry<String, JsonValue> lowerEntry(String key) {
    return new MyEntry(myMap.lowerEntry(key));
  }


  @Override
  public String lowerKey(String key) {
    return myMap.lowerKey(key);
  }


  @Override
  public JsonValue merge(String key, JsonValue value, BiFunction<? super JsonValue, ? super JsonValue, ? extends JsonValue> remappingFunction) {
    final BinaryOperator<Canonical> myFunction = (v1, v2) -> Canonical.cast(remappingFunction.apply(v1, v2));
    return myMap.merge(key, Canonical.cast(value), myFunction);
  }


  @Override
  public NavigableSet<String> navigableKeySet() {
    return myMap.navigableKeySet();
  }


  /**
   * Get an array from the object.
   *
   * @param key the entry's key
   *
   * @return the array, or null
   */
  @Nullable
  public CJArray optArray(String key) {
    return getQuiet(CJArray.class, key);
  }


  /**
   * Get a big decimal from the object.
   *
   * @param key the entry's key
   *
   * @return the big decimal, or null
   */
  @Nullable
  public BigDecimal optBigDecimal(String key) {
    Number n = getQuiet(Number.class, key);
    return Canonical.toBigDecimal(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @param key the entry's key
   *
   * @return the big integer, or null
   */
  @Nullable
  public BigInteger optBigInteger(String key) {
    Number n = getQuiet(Number.class, key);
    return Canonical.toBigInteger(n);
  }


  /**
   * Get a Boolean from the object.
   *
   * @param key the entry's key
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
   * @param key the entry's key
   *
   * @return the double, or null
   */
  @Nullable
  public Double optDouble(String key) {
    Canonical p = getCanonical(key);
    if (p == null) {
      return null;
    }
    return CJNumber.toDouble(p);
  }


  /**
   * Get an integer from the object.
   *
   * @param key the entry's key
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
    return getCanonical(key);
  }


  /**
   * Get a long from the object.
   *
   * @param key the entry's key
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
   * @param key the entry's key
   *
   * @return the object, or null
   */
  @Nullable
  public CJObject optObject(String key) {
    return getQuiet(CJObject.class, key);
  }


  /**
   * Get a String from the object.
   *
   * @param key the entry's key
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
  public void optimiseStorage() {
    optimiseStorage(new HashMap<>());
  }


  /**
   * Ensure that all Strings and Numbers have a single representation in memory.
   *
   * @param values the unique values
   */
  void optimiseStorage(HashMap<Canonical, Canonical> values) {
    for (Entry<String, Canonical> e : myMap.entrySet()) {
      Canonical current = e.getValue();
      switch (current.getValueType()) {
        case ARRAY:
          // recurse into array
          ((CJArray) current).optimiseStorage(values);
          break;
        case OBJECT:
          // recurse into object
          ((CJObject) current).optimiseStorage(values);
          break;
        default:
          Canonical single = values.computeIfAbsent(current, c -> c);
          if (single != current) {
            e.setValue(single);
          }
          break;
      }
    }
  }


  @Override
  public Entry<String, JsonValue> pollFirstEntry() {
    return new MyEntry(myMap.pollFirstEntry());
  }


  @Override
  public Entry<String, JsonValue> pollLastEntry() {
    return new MyEntry(myMap.pollLastEntry());
  }


  @Override
  public JsonValue put(String key, JsonValue value) {
    return myMap.put(key, Canonical.cast(value));
  }


  public Canonical put(String key, Canonical value) {
    return myMap.put(key, value);
  }


  /**
   * Put a null value into this.
   *
   * @param key the key
   */
  public void put(String key) {
    put(key, CJNull.NULL);
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   *
   * @return the old value
   */
  public JsonValue put(String key, Boolean value) {
    if (value != null) {
      return put(key, value ? CJTrue.TRUE : CJFalse.FALSE);
    }
    return put(key, CJNull.NULL);
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, CJArray value) {
    if (value != null) {
      put(key, (Canonical) value);
    } else {
      put(key, CJNull.NULL);
    }
  }


  /**
   * Put a value into this.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, CJObject value) {
    if (value != null) {
      put(key, (Canonical) value);
    } else {
      put(key, CJNull.NULL);
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
      put(key, CJNumber.cast(value));
    } else {
      put(key, CJNull.NULL);
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
      put(key, CJString.create(value));
    } else {
      put(key, CJNull.NULL);
    }
  }


  /**
   * Put all the contents of the supplied map into this.
   *
   * @param m the map of values
   */
  public void putAll(@Nonnull Map<? extends String, ? extends JsonValue> m) {
    if (m instanceof CJObject) {
      myMap.putAll(((CJObject) m).myMap);
    } else {
      for (Entry<? extends String, ? extends JsonValue> e : m.entrySet()) {
        put(e.getKey(), e.getValue());
      }
    }
  }


  @Override
  public JsonValue putIfAbsent(String key, JsonValue value) {
    return myMap.putIfAbsent(key, Canonical.cast(value));
  }


  @Override
  public Canonical remove(Object key) {
    return myMap.remove(key);
  }


  @Override
  public boolean remove(Object key, Object value) {
    return myMap.remove(key, value);
  }


  /**
   * Remove a JSON array from this.
   *
   * @param key the key to remove, if it is an array
   *
   * @return the array removed
   */
  @Nullable
  public CJArray removeArray(String key) {
    Canonical canonical = getCanonical(key);
    if (canonical == null || canonical.getValueType() != ValueType.ARRAY) {
      return null;
    }
    remove(key);
    return (CJArray) canonical.getValue();
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
    Canonical canonical = getCanonical(key);
    if (canonical == null || !Canonical.IS_BOOLEAN.contains(canonical.getValueType())) {
      return null;
    }
    remove(key);
    return (Boolean) canonical.getValue();
  }


  /**
   * Remove a null from this.
   *
   * @param key the key to remove, if it is null
   */
  public void removeNull(String key) {
    Canonical canonical = getCanonical(key);
    if (canonical != null && canonical.getValueType() == ValueType.NULL) {
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
    Canonical canonical = getCanonical(key);
    if (canonical == null || canonical.getValueType() != ValueType.NUMBER) {
      return null;
    }
    remove(key);
    return (Number) canonical.getValue();
  }


  /**
   * Remove a JSON object from this.
   *
   * @param key the key to remove, if it is an object
   *
   * @return the object removed
   */
  @Nullable
  public CJObject removeObject(String key) {
    Canonical canonical = getCanonical(key);
    if (canonical == null || canonical.getValueType() != ValueType.OBJECT) {
      return null;
    }
    remove(key);
    return (CJObject) canonical.getValue();
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
    Canonical canonical = getCanonical(key);
    if (canonical == null || canonical.getValueType() != ValueType.STRING) {
      return null;
    }
    remove(key);
    return (String) canonical.getValue();
  }


  @Override
  public boolean replace(String key, JsonValue oldValue, JsonValue newValue) {
    return myMap.replace(key, Canonical.cast(oldValue), Canonical.cast(newValue));
  }


  @Override
  public JsonValue replace(String key, JsonValue value) {
    return myMap.replace(key, Canonical.cast(value));
  }


  @Override
  public void replaceAll(BiFunction<? super String, ? super JsonValue, ? extends JsonValue> function) {
    final BiFunction<String, Canonical, Canonical> myFunction = (k, v) -> Canonical.cast(function.apply(k, v));
    myMap.replaceAll(myFunction);
  }


  @Override
  public int size() {
    return myMap.size();
  }


  @Override
  public NavigableMap<String, JsonValue> subMap(String fromKey, boolean fromInclusive, String toKey, boolean toInclusive) {
    return new CJObject(myMap.subMap(fromKey, fromInclusive, toKey, toInclusive), false);
  }


  @Override
  @Nonnull
  public SortedMap<String, JsonValue> subMap(String fromKey, String toKey) {
    return new CJObject(myMap.subMap(fromKey, true, toKey, false), false);
  }


  @Override
  public NavigableMap<String, JsonValue> tailMap(String fromKey, boolean inclusive) {
    return new CJObject(myMap.tailMap(fromKey, inclusive), false);
  }


  @Override
  @Nonnull
  public SortedMap<String, JsonValue> tailMap(String fromKey) {
    return new CJObject(myMap.tailMap(fromKey, true), false);
  }


  @Override
  public String toCanonicalString() {
    StringBuilder buf = new StringBuilder();
    Generator generator = CanonicalJsonProvider.CANONICAL_GENERATOR_FACTORY.createGenerator(buf);
    generator.writeStartObject();
    for (Map.Entry<String, Canonical> e : myMap.entrySet()) {
      generator.write(e.getKey(), e.getValue());
    }
    generator.writeEnd();
    generator.close();
    return buf.toString();
  }


  @Override
  public String toPrettyString() {
    StringBuilder buf = new StringBuilder();
    Generator generator = CanonicalJsonProvider.PRETTY_GENERATOR_FACTORY.createGenerator(buf);
    generator.writeStartObject();
    for (Map.Entry<String, Canonical> e : myMap.entrySet()) {
      generator.write(e.getKey(), e.getValue());
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
  @Nonnull
  public Collection<JsonValue> values() {
    return new MyValues(myMap.values());
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    writer.append('{');
    boolean isNotFirst = false;
    for (Map.Entry<String, JsonValue> e : entrySet()) {
      if (isNotFirst) {
        writer.append(',');
      } else {
        isNotFirst = true;
      }

      CJString.format(writer, e.getKey());
      writer.append(':');
      ((Canonical) e.getValue()).writeTo(writer);
    }
    writer.append('}');
  }

}
