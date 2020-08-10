package io.setl.json;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;
import javax.annotation.Nonnull;
import javax.json.JsonValue;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.setl.json.jackson.JsonObjectSerializer;

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
public class JNavigableObject extends JObject implements NavigableMap<String, JsonValue> {


  JNavigableObject(NavigableMap<String, Primitive> map) {
    super(map, false);
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
  public Comparator<? super String> comparator() {
    return myMap.comparator();
  }


  @Override
  public NavigableSet<String> descendingKeySet() {
    return myMap.descendingKeySet();
  }


  @Override
  public NavigableMap<String, JsonValue> descendingMap() {
    return new JNavigableObject(myMap.descendingMap());
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
  public NavigableMap<String, JsonValue> headMap(String toKey, boolean inclusive) {
    return new JNavigableObject(myMap.headMap(toKey, inclusive));
  }


  @Override
  @Nonnull
  public SortedMap<String, JsonValue> headMap(String toKey) {
    return new JNavigableObject(myMap.headMap(toKey, false));
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
  public NavigableSet<String> navigableKeySet() {
    return myMap.navigableKeySet();
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
  public NavigableMap<String, JsonValue> subMap(String fromKey, boolean fromInclusive, String toKey, boolean toInclusive) {
    return new JNavigableObject(myMap.subMap(fromKey, fromInclusive, toKey, toInclusive));
  }


  @Override
  @Nonnull
  public SortedMap<String, JsonValue> subMap(String fromKey, String toKey) {
    return new JNavigableObject(myMap.subMap(fromKey, true, toKey, false));
  }


  @Override
  public NavigableMap<String, JsonValue> tailMap(String fromKey, boolean inclusive) {
    return new JNavigableObject(myMap.tailMap(fromKey, inclusive));
  }


  @Override
  @Nonnull
  public SortedMap<String, JsonValue> tailMap(String fromKey) {
    return new JNavigableObject(myMap.tailMap(fromKey, true));
  }

}
