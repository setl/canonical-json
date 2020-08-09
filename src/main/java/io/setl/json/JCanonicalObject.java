package io.setl.json;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Comparator;
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
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.json.JsonValue;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.setl.json.JArray.MySpliterator;
import io.setl.json.exception.MissingItemException;
import io.setl.json.jackson.JsonObjectSerializer;
import io.setl.json.primitive.PString;

/**
 * Representation of an object in JSON.
 *
 * <p>No value in the object can be null. If you try to add one, it will be replaced by a Primitive instance holding a null.
 *
 * <p>As JSON objects can contain mixed content, this class provides type-checking accessors to the object properties. There are multiple varieties of each
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
 * call <code>getIntSafe(key)</code> and the key maps to the Long value 1L<<50, then the call returns the value of Integer.MAX_VALUE, as would be expected
 * for a narrowing primitive conversion, rather than throwing a <code>IncorrectTypeException</code>.
 */

@JsonSerialize(using = JsonObjectSerializer.class)
public class JCanonicalObject extends JObject implements NavigableMap<String, JsonValue> {

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

    private final Entry<String, Primitive> me;


    public MyEntry(Entry<String, Primitive> me) {
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



  private final NavigableMap<String, Primitive> myMap;


  public JCanonicalObject() {
    myMap = new TreeMap<>(CODE_POINT_ORDER);
  }


  /**
   * Create a new instance as a deep copy of the provided map.
   *
   * @param map the map to copy.
   */
  public JCanonicalObject(Map<String, ?> map) {
    myMap = new TreeMap<>(CODE_POINT_ORDER);
    for (Entry<String, ?> e : map.entrySet()) {
      String k = e.getKey();
      myMap.put(k, Primitive.create(e.getValue()));
    }
  }


  private JCanonicalObject(NavigableMap<String, Primitive> map, boolean makeCopy) {
    if (makeCopy) {
      myMap = new TreeMap<>(CODE_POINT_ORDER);
      for (Entry<String, Primitive> e : map.entrySet()) {
        String k = e.getKey();
        myMap.put(k, e.getValue().copy());
      }
    } else {
      myMap = map;
    }
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
  public Primitive compute(String key, @Nonnull BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
    final BiFunction<String, Primitive, Primitive> myFunction = (k, v) -> Primitive.cast(remappingFunction.apply(k, v));
    return myMap.compute(key, myFunction);
  }


  @Override
  public Primitive computeIfAbsent(String key, @Nonnull Function<? super String, ? extends JsonValue> mappingFunction) {
    final Function<String, Primitive> myFunction = k -> Primitive.cast(mappingFunction.apply(k));
    return myMap.computeIfAbsent(key, myFunction);
  }


  @Override
  public Primitive computeIfPresent(String key, @Nonnull BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
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
    return new JCanonicalObject(myMap, true);
  }


  @Override
  public NavigableSet<String> descendingKeySet() {
    return myMap.descendingKeySet();
  }


  @Override
  public NavigableMap<String, JsonValue> descendingMap() {
    return new JCanonicalObject(myMap.descendingMap(), false);
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
  public Primitive get(Object key) {
    return myMap.get(key);
  }


  @Override
  public JsonValue getOrDefault(Object key, JsonValue defaultValue) {
    return myMap.getOrDefault(key, Primitive.cast(defaultValue));
  }


  @Override
  public Primitive get(String name) {
    return myMap.get(name);
  }


  @Override
  public int hashCode() {
    return myMap.hashCode();
  }


  @Override
  public NavigableMap<String, JsonValue> headMap(String toKey, boolean inclusive) {
    return new JCanonicalObject(myMap.headMap(toKey, inclusive), false);
  }


  @Override
  @Nonnull
  public SortedMap<String, JsonValue> headMap(String toKey) {
    return new JCanonicalObject(myMap.headMap(toKey, false), false);
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
  public JsonValue merge(String key, @Nonnull JsonValue value, @Nonnull BiFunction<? super JsonValue, ? super JsonValue, ? extends JsonValue> remappingFunction) {
    final BinaryOperator<Primitive> myFunction = (v1, v2) -> Primitive.cast(remappingFunction.apply(v1, v2));
    return myMap.merge(key, Primitive.cast(value), myFunction);
  }


  @Override
  public NavigableSet<String> navigableKeySet() {
    return myMap.navigableKeySet();
  }


  /**
   * Ensure that all Strings and Numbers have a single representation in memory.
   */
  @Override
  public void optimiseStorage() {
    optimiseStorage(new HashMap<>());
  }


  /**
   * Ensure that all Strings and Numbers have a single representation in memory.
   *
   * @param values the unique values
   */
  @Override
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


  @Override
  public Primitive put(String key, Primitive value) {
    return myMap.put(key, value);
  }


  /**
   * Put all the contents of the supplied map into this.
   *
   * @param m the map of values
   */
  public void putAll(@Nonnull Map<? extends String, ? extends JsonValue> m) {
    if (m instanceof JCanonicalObject) {
      myMap.putAll(((JCanonicalObject) m).myMap);
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
    return new JCanonicalObject(myMap.subMap(fromKey, fromInclusive, toKey, toInclusive), false);
  }


  @Override
  @Nonnull
  public SortedMap<String, JsonValue> subMap(String fromKey, String toKey) {
    return new JCanonicalObject(myMap.subMap(fromKey, true, toKey, false), false);
  }


  @Override
  public NavigableMap<String, JsonValue> tailMap(String fromKey, boolean inclusive) {
    return new JCanonicalObject(myMap.tailMap(fromKey, inclusive), false);
  }


  @Override
  @Nonnull
  public SortedMap<String, JsonValue> tailMap(String fromKey) {
    return new JCanonicalObject(myMap.tailMap(fromKey, true), false);
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

      PString.format(writer, e.getKey());
      writer.append(':');
      ((Primitive) e.getValue()).writeTo(writer);
    }
    writer.append('}');
  }

}
