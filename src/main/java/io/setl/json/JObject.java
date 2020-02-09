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

import io.setl.json.JArray.MySpliterator;
import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import io.setl.json.jackson.JsonObjectSerializer;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import io.setl.json.primitive.numbers.PNumber;

/**
 * Representation of an object in JSON.
 *
 * <p>No value in the object can be null. If you try to add one, it will be replaced by a Primitive instance holding a null.
 *
 * <p>As JSON objects can contain mixed content, this class provides type-checking accessors to the array members. There are multiple varieties of each accessor
 * which obey these contracts:
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
 * <dt><code>get<i>Type</i>(index)</code></dt>
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
public class JObject implements NavigableMap<String, JsonValue>, JsonObject, Primitive {

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
   * Set which converts JsonValue to Primitives.
   */
  static class MyEntries implements Set<Entry<String, JsonValue>> {

    private final Set<Entry<String, Primitive>> mySet;


    MyEntries(Set<Entry<String, Primitive>> mySet) {
      this.mySet = mySet;
    }


    @Override
    public boolean add(Entry<String, JsonValue> entry) {
      throw new UnsupportedOperationException();
    }


    @Override
    public boolean addAll(Collection<? extends Entry<String, JsonValue>> c) {
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
      if (v instanceof Primitive) {
        return mySet.contains(e);
      }
      if (v != null && !(v instanceof JsonValue)) {
        return false;
      }
      return mySet.contains(new SimpleEntry<>(e.getKey(), Primitive.cast((JsonValue) e.getValue())));
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
    public Iterator<Entry<String, JsonValue>> iterator() {
      final Iterator<Entry<String, Primitive>> myIterator = mySet.iterator();
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
    public boolean removeAll(Collection<?> c) {
      return mySet.removeAll(c);
    }


    @Override
    public boolean removeIf(Predicate<? super Entry<String, JsonValue>> filter) {
      return mySet.removeIf(e -> filter.test(new MyEntry(e)));
    }


    @Override
    public boolean retainAll(Collection<?> c) {
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
    public Object[] toArray() {
      return mySet.toArray();
    }


    @Override
    public <T> T[] toArray(T[] a) {
      return mySet.toArray(a);
    }


    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
      return mySet.toArray(generator);
    }


    @Override
    public String toString() {
      return mySet.toString();
    }

  }



  static class MyEntry implements Entry<String, JsonValue> {

    private final Entry<String, Primitive> me;


    public MyEntry(Entry<String, Primitive> me) {
      this.me = me;
    }


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
      return me.setValue(Primitive.cast(value));
    }

  }



  /**
   * Spliterator over JsonValues instead of Primitives. If someone called setValue on the output, we have to convert to a Primitive.
   */
  static class MyEntrySpliterator implements Spliterator<Entry<String, JsonValue>> {


    private final Spliterator<Entry<String, Primitive>> me;


    MyEntrySpliterator(Spliterator<Entry<String, Primitive>> me) {
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
      Spliterator<Entry<String, Primitive>> newSplit = me.trySplit();
      if (newSplit != null) {
        return new MyEntrySpliterator(newSplit);
      }
      return null;
    }

  }



  private static class MyValues implements Collection<JsonValue> {

    private final Collection<Primitive> me;


    MyValues(Collection<Primitive> me) {
      this.me = me;
    }


    public boolean add(JsonValue primitive) {
      throw new UnsupportedOperationException("Add is not supported on a map's values");
    }


    public boolean addAll(Collection<? extends JsonValue> c) {
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
    public boolean containsAll(Collection<?> c) {
      return me.containsAll(c);
    }


    @Override
    public boolean isEmpty() {
      return me.isEmpty();
    }


    @Override
    public Iterator<JsonValue> iterator() {
      final Iterator<Primitive> myIterator = me.iterator();
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
    public boolean removeAll(Collection<?> c) {
      return me.removeAll(c);
    }


    @Override
    public boolean removeIf(Predicate<? super JsonValue> filter) {
      return me.removeIf(filter);
    }


    @Override
    public boolean retainAll(Collection<?> c) {
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
    public Object[] toArray() {
      return me.toArray();
    }


    @Override
    public <T> T[] toArray(T[] a) {
      return me.toArray(a);
    }


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
  public static JObject asJObject(Map<?, ?> map) {
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


  private final NavigableMap<String, Primitive> myMap;


  public JObject() {
    myMap = new TreeMap<>(CODE_POINT_ORDER);
  }


  public JObject(Map<String, ?> map) {
    myMap = new TreeMap<>(CODE_POINT_ORDER);
    for (Entry<String, ?> e : map.entrySet()) {
      myMap.put(e.getKey(), Primitive.create(e.getValue()));
    }
  }


  private JObject(NavigableMap<String, Primitive> map, boolean makeCopy) {
    if (makeCopy) {
      myMap = new TreeMap<>(CODE_POINT_ORDER);
      for (Entry<String, Primitive> e : map.entrySet()) {
        myMap.put(e.getKey(), e.getValue().copy());
      }
    } else {
      myMap = map;
    }
  }


  @Override
  public JObject asJsonObject() {
    return this;
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
  public Primitive compute(String key, BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
    final BiFunction<String, Primitive, Primitive> myFunction = (k, v) -> Primitive.cast(remappingFunction.apply(k, v));
    return myMap.compute(key, myFunction);
  }


  @Override
  public Primitive computeIfAbsent(String key, Function<? super String, ? extends JsonValue> mappingFunction) {
    final Function<String, Primitive> myFunction = k -> Primitive.cast(mappingFunction.apply(k));
    return myMap.computeIfAbsent(key, myFunction);
  }


  @Override
  public Primitive computeIfPresent(String key, BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
    final BiFunction<String, Primitive, Primitive> myFunction = (k, v) -> Primitive.cast(remappingFunction.apply(k, v));
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
  public JObject copy() {
    return new JObject(myMap, true);
  }


  @Override
  public NavigableSet<String> descendingKeySet() {
    return myMap.descendingKeySet();
  }


  @Override
  public NavigableMap<String, JsonValue> descendingMap() {
    return new JObject(myMap.descendingMap(), false);
  }


  @Override
  public Set<Entry<String, JsonValue>> entrySet() {
    return new MyEntries(myMap.entrySet());
  }


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
  public Primitive get(Object key) {
    return myMap.get(key);
  }


  /**
   * Get an array from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the array, or the default
   */
  public JArray getArray(String key, @Nonnull Function<String, JArray> dflt) {
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
  @Nonnull
  public JArray getArray(String key, @Nonnull JArray dflt) {
    return getQuiet(JArray.class, key, dflt);
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
   * @param key  the key
   * @param dflt the default
   *
   * @return the big decimal, or the default
   */
  @Nonnull
  public BigDecimal getBigDecimal(String key, @Nonnull BigDecimal dflt) {
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
  public BigDecimal getBigDecimal(String key, @Nonnull Function<String, BigDecimal> dflt) {
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
  @Nonnull
  public BigDecimal getBigDecimal(String key) {
    Number n = getSafe(Number.class, ValueType.NUMBER, key);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big integer from the object.
   *
   * @param key  the key
   * @param dflt the default
   *
   * @return the big integer, or the default
   */
  @Nonnull
  public BigInteger getBigInteger(String key, @Nonnull BigInteger dflt) {
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
  public BigInteger getBigInteger(String key, @Nonnull Function<String, BigInteger> dflt) {
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
  @Nonnull
  public BigInteger getBigInteger(String key) {
    Number n = getSafe(Number.class, ValueType.NUMBER, key);
    return Primitive.toBigInteger(n);
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
  public boolean getBoolean(String key, @Nonnull Predicate<String> dflt) {
    Boolean value = getQuiet(Boolean.class, key);
    return (value != null) ? value.booleanValue() : dflt.test(key);
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
   * @param key  the key
   * @param dflt the default
   *
   * @return the double, or the default
   */
  public double getDouble(String key, double dflt) {
    Double n = optDouble(key);
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
  public double getDouble(String key, @Nonnull ToDoubleFunction<String> dflt) {
    Double n = optDouble(key);
    return (n != null) ? n.doubleValue() : dflt.applyAsDouble(key);
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
    return n.doubleValue();
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
  public int getInt(String key, @Nonnull ToIntFunction<String> dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.intValue() : dflt.applyAsInt(key);
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
    return getArray(name);
  }


  @Override
  public JsonNumber getJsonNumber(String name) {
    Primitive p = getPrimitive(name);
    return (p != null) ? (PNumber) p : null;
  }


  @Override
  public JsonObject getJsonObject(String name) {
    return getObject(name);
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
   * @param key  the value's key
   * @param dflt the default value
   *
   * @return the value
   */
  public JsonValue getJsonValue(String key, JsonValue dflt) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      return dflt;
    }
    return primitive;
  }


  /**
   * Get a JsonValue from this object. If the value does not exist, the function is invoked to create a value.
   *
   * @param key  the value's key
   * @param dflt supplier of a default value
   *
   * @return the value
   */
  public JsonValue getJsonValue(String key, @Nonnull Function<String, JsonValue> dflt) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      return dflt.apply(key);
    }
    return primitive;
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
  public long getLong(String key, @Nonnull ToLongFunction<String> dflt) {
    Number n = getQuiet(Number.class, key);
    return (n != null) ? n.longValue() : dflt.applyAsLong(key);
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
   * @param key  the key
   * @param dflt the default
   *
   * @return the object, or the default
   */
  public JObject getObject(String key, @Nonnull Function<String, JObject> dflt) {
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
  public JObject getObject(String key) {
    return getSafe(JObject.class, ValueType.OBJECT, key);
  }


  @Override
  public JsonValue getOrDefault(Object key, JsonValue defaultValue) {
    return myMap.getOrDefault(key, Primitive.cast(defaultValue));
  }


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


  private <T> T getQuiet(Class<T> clazz, String key, T dflt) {
    return getQuiet(clazz, key, (Function<String, T>) k -> dflt);
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
   * @param key  the key
   * @param dflt the default
   *
   * @return the String, or the default
   */
  public String getString(String key, @Nonnull UnaryOperator<String> dflt) {
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
  public String getString(String key) {
    return getSafe(String.class, ValueType.STRING, key);
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
    return ValueType.OBJECT;
  }


  @Override
  public int hashCode() {
    return myMap.hashCode();
  }


  @Override
  public NavigableMap<String, JsonValue> headMap(String toKey, boolean inclusive) {
    return new JObject(myMap.headMap(toKey, inclusive), false);
  }


  @Override
  public SortedMap<String, JsonValue> headMap(String toKey) {
    return new JObject(myMap.headMap(toKey, false), false);
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
    Primitive p = getPrimitive(name);
    if (p == null) {
      throw new MissingItemException(name, ValueType.NULL);
    }
    return p.getValueType() == ValueType.NULL;
  }


  public boolean isType(String key, ValueType type) {
    Primitive primitive = getPrimitive(key);
    if (primitive == null) {
      throw new MissingItemException(key, type);
    }
    return primitive.getValueType() == type;
  }


  @Override
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
    final BinaryOperator<Primitive> myFunction = (v1, v2) -> Primitive.cast(remappingFunction.apply(v1, v2));
    return myMap.merge(key, Primitive.cast(value), myFunction);
  }


  @Override
  public NavigableSet<String> navigableKeySet() {
    return myMap.navigableKeySet();
  }


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
    return (n != null) ? Integer.valueOf(n.intValue()) : null;
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
    return (n != null) ? Long.valueOf(n.longValue()) : null;
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
  public void optimiseStorage() {
    optimiseStorage(new HashMap<>());
  }


  /**
   * Ensure that all Strings and Numbers have a single representation in memory.
   *
   * @param values the unique values
   */
  void optimiseStorage(HashMap<Primitive, Primitive> values) {
    for (Entry<String, Primitive> e : myMap.entrySet()) {
      Primitive current = e.getValue();
      switch (current.getValueType()) {
        case ARRAY:
          // recurse into array
          ((JArray) current).optimiseStorage(values);
          break;
        case OBJECT:
          // recurse into object
          ((JObject) current).optimiseStorage(values);
          break;
        default:
          Primitive single = values.computeIfAbsent(current, c -> c);
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
    return myMap.put(key, Primitive.cast(value));
  }


  public Primitive put(String key, Primitive value) {
    return myMap.put(key, value);
  }


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


  public void putAll(Map<? extends String, ? extends JsonValue> m) {
    if (m instanceof JObject) {
      myMap.putAll(((JObject) m).myMap);
    } else {
      for (Entry<? extends String, ? extends JsonValue> e : m.entrySet()) {
        put(e.getKey(), e.getValue());
      }
    }
  }


  @Override
  public JsonValue putIfAbsent(String key, JsonValue value) {
    return myMap.putIfAbsent(key, Primitive.cast(value));
  }


  @Override
  public Primitive remove(Object key) {
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
  public boolean replace(String key, JsonValue oldValue, JsonValue newValue) {
    return myMap.replace(key, Primitive.cast(oldValue), Primitive.cast(newValue));
  }


  @Override
  public JsonValue replace(String key, JsonValue value) {
    return myMap.replace(key, Primitive.cast(value));
  }


  @Override
  public void replaceAll(BiFunction<? super String, ? super JsonValue, ? extends JsonValue> function) {
    final BiFunction<String, Primitive, Primitive> myFunction = (k, v) -> Primitive.cast(function.apply(k, v));
    myMap.replaceAll(myFunction);
  }


  @Override
  public int size() {
    return myMap.size();
  }


  @Override
  public NavigableMap<String, JsonValue> subMap(String fromKey, boolean fromInclusive, String toKey, boolean toInclusive) {
    return new JObject(myMap.subMap(fromKey, fromInclusive, toKey, toInclusive), false);
  }


  @Override
  public SortedMap<String, JsonValue> subMap(String fromKey, String toKey) {
    return new JObject(myMap.subMap(fromKey, true, toKey, false), false);
  }


  @Override
  public NavigableMap<String, JsonValue> tailMap(String fromKey, boolean inclusive) {
    return new JObject(myMap.tailMap(fromKey, inclusive), false);
  }


  @Override
  public SortedMap<String, JsonValue> tailMap(String fromKey) {
    return new JObject(myMap.tailMap(fromKey, true), false);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('{');
    for (Map.Entry<String, JsonValue> e : entrySet()) {
      PString.format(buf, e.getKey());
      buf.append(':');
      buf.append(e.getValue());
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

      PString.format(writer, e.getKey());
      writer.append(':');
      ((Primitive) e.getValue()).writeTo(writer);
    }
    writer.append('}');
  }

}
