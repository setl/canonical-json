package io.setl.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.TreeMap;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import org.junit.Before;
import org.junit.Test;

import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.numbers.PNumber;

@SuppressWarnings("checkstyle:AvoidEscapedUnicodeCharacters")
public class JNavigableObjectTest {

  private JNavigableObject json = new JNavigableObject(new TreeMap<>(JObject.CODE_POINT_ORDER));


  @Test
  public void asJsonObject() {
    assertSame(json, json.asJsonObject());
  }


  @Test
  public void ceilingEntry() {
    Entry<String, JsonValue> e = json.ceilingEntry("n");
    assertEquals("null", e.getKey());
  }


  @Test
  public void ceilingKey() {
    assertEquals("null", json.ceilingKey("n"));
  }


  @Test
  public void comparator() {
    assertEquals(JObject.CODE_POINT_ORDER, json.comparator());
  }


  @Test
  public void compute() {
    json.compute("null", (k, v) -> JsonValue.TRUE);
    assertEquals(JsonValue.TRUE, json.get("null"));
  }


  @Test
  public void computeIfAbsent() {
    json.computeIfAbsent("null", k -> JsonValue.TRUE);
    assertEquals(JsonValue.NULL, json.get("null"));
  }


  @Test
  public void computeIfPresent() {
    json.computeIfPresent("null", (k, v) -> JsonValue.TRUE);
    assertEquals(JsonValue.TRUE, json.get("null"));
  }


  @Test
  public void descendingKeySet() {
    NavigableSet<String> set = json.descendingKeySet();
    assertEquals("\07", set.last());
  }


  @Test
  public void descendingMap() {
    NavigableMap<String, JsonValue> map = json.descendingMap();
    assertEquals("\07", map.lastKey());
  }


  @Test
  public void entryEquals() {
    Entry<String, JsonValue> entry = json.firstEntry();
    assertTrue(entry.equals(new SimpleEntry<>("\07", PString.create("bell"))));
  }


  @Test(expected = UnsupportedOperationException.class)
  public void entrySetAdd() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    entrySet.add(new SimpleEntry<>("a", JsonValue.EMPTY_JSON_ARRAY));
  }


  @Test(expected = UnsupportedOperationException.class)
  public void entrySetAddAll() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    entrySet.addAll(Arrays.asList(
        new SimpleEntry<>("a", JsonValue.EMPTY_JSON_ARRAY),
        new SimpleEntry<>("\0", Primitive.create(3))
    ));
  }


  @Test
  public void entrySetClear() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    assertFalse(entrySet.isEmpty());
    entrySet.clear();
    assertTrue(entrySet.isEmpty());
    assertTrue(json.isEmpty());
  }


  @Test
  public void entrySetContains() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    assertFalse(entrySet.contains("x"));
    assertFalse(entrySet.contains(new SimpleEntry<>(1, 1)));
    assertFalse(entrySet.contains(new SimpleEntry<>("x", 1)));
    assertTrue(entrySet.contains(new SimpleEntry<>("null", null)));
    assertTrue(entrySet.contains(new SimpleEntry<>("object", JsonValue.EMPTY_JSON_OBJECT)));
    assertTrue(entrySet.contains(new SimpleEntry<>("string", Primitive.cast("string"))));
  }


  @Test
  public void entrySetContainsAll() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    assertFalse(entrySet.containsAll(Arrays.asList("x")));
    assertTrue(entrySet.containsAll(Arrays.asList(
        new SimpleEntry<>("null", null),
        new SimpleEntry<>("string", Primitive.cast("string"))
    )));
  }


  @Test
  public void entrySetEquals() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    HashSet<Entry<String, JsonValue>> copy = new HashSet<>(entrySet);
    assertTrue(entrySet.equals(copy));
  }


  @Test
  public void entrySetHashCode() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    int h = entrySet.hashCode();
    entrySet.removeIf(e -> "\07".equals(e.getKey()));
    assertNotEquals(h, entrySet.hashCode());
  }


  @Test
  public void entrySetIterator() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    Iterator<Entry<String, JsonValue>> iterator = entrySet.iterator();
    assertTrue(iterator.hasNext());
    Entry<String, JsonValue> e = iterator.next();
    assertEquals(Primitive.cast("bell"), e.getValue());
    assertEquals("\07", e.getKey());
    iterator.remove();
    assertFalse(json.containsKey("\07"));
  }


  @Test
  public void entrySetOthers() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();

    // The current implementation of these is trivial. There is really nothing for us to test.
    assertNotNull(entrySet.stream());
    assertNotNull(entrySet.toArray());
    assertNotNull(entrySet.toArray(new Entry[0]));
    assertNotNull(entrySet.toArray(Entry[]::new));
    assertNotNull(entrySet.toString());
  }


  @Test
  public void entrySetParallelStream() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    assertEquals(11, entrySet.parallelStream().count());
  }


  @Test
  public void entrySetRemove() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    entrySet.remove(new SimpleEntry<>("string", Primitive.cast("string")));
    assertEquals(10, entrySet.size());
  }


  @Test
  public void entrySetRemoveAll() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    entrySet.removeAll(Set.of(
        new SimpleEntry<>("string", Primitive.cast("string")),
        new SimpleEntry<>("boolean", Primitive.cast("false"))
    ));
    assertEquals(10, entrySet.size());
  }


  @Test
  public void entrySetRetainAll() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    entrySet.retainAll(Set.of(
        new SimpleEntry<>("string", Primitive.cast("string")),
        new SimpleEntry<>("boolean", Primitive.cast("false"))
    ));
    assertEquals(1, entrySet.size());
  }


  @Test
  public void entrySetSpliterator() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    Spliterator<Entry<String, JsonValue>> spliterator = entrySet.spliterator();
    assertTrue(spliterator.hasCharacteristics(Spliterator.ORDERED));
    assertEquals(11, spliterator.estimateSize());
    assertEquals(11, spliterator.getExactSizeIfKnown());
    assertTrue(spliterator.tryAdvance(e -> e.setValue(JsonValue.FALSE)));
    assertTrue(json.containsValue(JsonValue.FALSE));
    assertNotNull(spliterator.trySplit());
  }


  @Test
  public void entrySetValue() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    Entry<String, JsonValue> entry = entrySet.iterator().next();
    entry.setValue(PNumber.create(5));
    assertEquals("5", json.get("\07").toString());
  }


  @Test
  public void firstKey() {
    assertEquals("\07", json.firstKey());
  }


  @Test
  public void floorEntry() {
    assertEquals("null", json.floorEntry("nullllll").getKey());
  }


  @Test
  public void floorKey() {
    assertEquals("null", json.floorKey("nullllll"));
  }


  @Test
  public void getJsonArray() {
    assertNotNull(json.getJsonArray("array"));
  }


  @Test
  public void getJsonNumber() {
    assertNotNull(json.getJsonNumber("small number"));
  }


  @Test
  public void getJsonObject() {
    assertNotNull(json.getJsonObject("object"));
  }


  @Test
  public void getJsonString() {
    assertNotNull(json.getJsonString("string"));
  }


  @Test
  public void getOrDefault() {
    JsonValue v = json.getOrDefault("boolean", JsonValue.EMPTY_JSON_ARRAY);
    assertEquals(ValueType.TRUE, v.getValueType());
  }


  @Test
  public void getValue1() {
    assertEquals("hello", json.getValue(String.class, "hello"));
    assertSame(json, json.getValue(Map.class, null));
  }


  @Test
  public void getValueSafe() {
    assertSame(json, json.getValueSafe(Primitive.class));
  }


  @Test
  public void headMap() {
    assertEquals(1, json.headMap("\07", true).size());
    assertEquals(0, json.headMap("\07").size());
  }


  @Test
  public void higherEntry() {
    String k = json.higherKey("l");
    Entry<String, JsonValue> e = json.higherEntry("l");
    assertEquals(k, e.getKey());
  }


  @Test
  public void isNull() {
    assertTrue(json.isNull("null"));
    assertFalse(json.isNull("string"));
  }


  @Test(expected = MissingItemException.class)
  public void isNull2() {
    json.isNull("zzz");
  }


  @Test
  public void lastEntry() {
    String k = json.lastKey();
    Entry<String, JsonValue> e = json.lastEntry();

  }


  @Test
  public void lowerEntry() {
    String k = json.lowerKey("l");
    Entry<String, JsonValue> e = json.lowerEntry("l");
    assertEquals(k, e.getKey());
  }


  @Test
  public void merge() {
    json.merge("foo", JsonValue.TRUE, (k, v) -> PNumber.create(String.valueOf(v).length()));
    assertTrue(json.getBoolean("foo"));
    json.merge("foo", JsonValue.TRUE, (k, v) -> PNumber.create(String.valueOf(v).length()));
    assertEquals(4, json.getInt("foo"));
  }


  @Test
  public void navigableKeySet() {
    assertNotNull(json.navigableKeySet());
  }


  @Test
  public void optimiseStorage() {
    // how to test this?
    json.optimiseStorage();
  }


  @Test
  public void poll() {
    json.pollFirstEntry();
    json.pollLastEntry();
    assertEquals(9, json.size());
  }


  @Test
  public void putAll() {
    json.putAll(Map.of("foo", JsonValue.FALSE));
    assertEquals(12, json.size());
  }


  @Test
  public void putIfAbsent() {
    json.putIfAbsent("a", PString.create("a"));
    assertTrue(json.containsKey("a"));
    json.putIfAbsent("a", PString.create("b"));
    assertFalse(json.containsValue("b"));
  }


  @Test
  public void remove2() {
    assertFalse(json.remove("null", JsonValue.TRUE));
  }


  @Test
  public void replace1() {
    assertEquals(JsonValue.TRUE, json.replace("boolean", JsonValue.FALSE));
    assertEquals(JsonValue.FALSE, json.replace("boolean", JsonValue.FALSE));
  }


  @Test
  public void replace2() {
    assertTrue(json.replace("boolean", JsonValue.TRUE, JsonValue.FALSE));
    assertFalse(json.replace("boolean", JsonValue.TRUE, JsonValue.FALSE));
  }


  @Test
  public void replaceAll() {
    json.replaceAll((k, v) -> PNumber.create(v.toString().length()));
    assertEquals(4, json.getInt("null"));
  }


  @Before
  public void setUp() {
    json.put("string", "string");
    json.put("\07", "bell");
    json.put("\uD83D\uDE09", "winking");
    json.put("\uD83D\uDE2A", "sleepy");
    json.put("\ueeee", "private use");
    json.put("null");
    json.put("small number", new BigDecimal("1e-5"));
    json.put("big number", new BigDecimal("1e+5"));
    json.put("object", new JObject());
    json.put("array", new JArray());
    json.put("boolean", true);
  }


  @Test
  public void subMap() {
    assertEquals(7, json.subMap("a", "z").size());
    assertEquals(7, json.subMap("a", false, "z", false).size());
  }


  @Test
  public void tailMap() {
    assertEquals(6, json.tailMap("null", false).size());
    assertEquals(7, json.tailMap("null").size());
  }


  @Test
  public void testFixMap() {
    HashMap<Object, Object> hm = new HashMap<>();
    hm.put("a", Arrays.asList(1, 2, 3));
    hm.put("b", true);
    hm.put("c", null);
    hm.put("d", 5);
    hm.put("e", new HashMap<>());
    hm.put("f", "text");
    hm.put("g", Primitive.TRUE);

    JObject fixed = JObject.asJObject(hm);
    assertEquals("{\"a\":[1,2,3],\"b\":true,\"c\":null,\"d\":5,\"e\":{},\"f\":\"text\",\"g\":true}", fixed.toString());
    JObject fixed2 = JObject.asJObject(fixed);
    assertSame(fixed, fixed2);

    HashMap<String, Object> hm2 = new HashMap<>();
    hm.forEach((k, v) -> hm2.put(String.valueOf(k), v));
    JObject fixed3 = new JObject(hm2);
    assertEquals(fixed, fixed3);
    assertNotSame(fixed, fixed3);

    hm.clear();
    hm.put(1, 2);
    try {
      JObject.asJObject(hm);
      fail();
    } catch (IllegalArgumentException e) {
      // correct
    }

    hm.clear();
    hm.put(null, null);
    try {
      JObject.asJObject(hm);
      fail();
    } catch (IllegalArgumentException e) {
      // correct
    }
  }


  @Test
  public void testGetArray() {
    assertNotNull(json.getArray("array"));
    try {
      json.getArray("string");
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      json.getArray("n/a");
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetArrayStringFunctionOfStringJsonArray() {
    JArray a1 = new JArray();
    a1.add(1);
    json.put("array", a1);
    final JArray a2 = new JArray();
    a1.add(2);

    assertEquals(a2, json.getArray("null", k -> a2));
    assertEquals(a2, json.getArray("n/a", k -> a2));
    assertEquals(a2, json.getArray("string", k -> a2));
    assertEquals(a1, json.getArray("array", k -> a2));
  }


  @Test
  public void testGetArrayStringJsonArray() {
    JArray a1 = new JArray();
    a1.add(1);
    json.put("array", a1);
    final JArray a2 = new JArray();
    a1.add(2);

    assertEquals(a2, json.getArray("null", a2));
    assertEquals(a2, json.getArray("n/a", a2));
    assertEquals(a2, json.getArray("string", a2));
    assertEquals(a1, json.getArray("array", a2));
  }


  @Test
  public void testGetBigDecimal() {
    assertNotNull(json.getBigDecimal("big number"));
    try {
      json.getBigDecimal("string");
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      json.getBigDecimal("n/a");
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetBigDecimalStringBigDecimal() {
    BigDecimal bd = new BigDecimal(Math.PI);
    BigDecimal bd2 = new BigDecimal(Math.E);
    json.put("bd", bd);
    assertEquals(bd2, json.getBigDecimal("null", bd2));
    assertEquals(bd2, json.getBigDecimal("n/a", bd2));
    assertEquals(bd2, json.getBigDecimal("string", bd2));
    assertEquals(bd, json.getBigDecimal("bd", bd2));
  }


  @Test
  public void testGetBigDecimalStringFunctionOfStringBigDecimal() {
    BigDecimal bd = new BigDecimal(Math.PI);
    BigDecimal bd2 = new BigDecimal(Math.E);
    json.put("bd", bd);
    assertEquals(bd2, json.getBigDecimal("null", k -> bd2));
    assertEquals(bd2, json.getBigDecimal("n/a", k -> bd2));
    assertEquals(bd2, json.getBigDecimal("string", k -> bd2));
    assertEquals(bd, json.getBigDecimal("bd", k -> bd2));
  }


  @Test
  public void testGetBigIntegerSafe() {
    assertNotNull(json.getBigInteger("big number"));
    try {
      json.getBigInteger("string");
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      json.getBigInteger("n/a");
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetBigIntegerStringBigInteger() {
    BigInteger bi = new BigInteger("1234567890");
    BigInteger b2 = new BigInteger("9876543210");
    json.put("bi", bi);
    assertEquals(b2, json.getBigInteger("null", b2));
    assertEquals(b2, json.getBigInteger("n/a", b2));
    assertEquals(b2, json.getBigInteger("string", b2));
    assertEquals(bi, json.getBigInteger("bi", b2));
  }


  @Test
  public void testGetBigIntegerStringFunctionOfStringBigInteger() {
    BigInteger bi = new BigInteger("1234567890");
    BigInteger b2 = new BigInteger("9876543210");
    json.put("bi", bi);
    assertEquals(b2, json.getBigInteger("null", k -> b2));
    assertEquals(b2, json.getBigInteger("n/a", k -> b2));
    assertEquals(b2, json.getBigInteger("string", k -> b2));
    assertEquals(bi, json.getBigInteger("bi", k -> b2));
  }


  @Test
  public void testGetBoolean() {
    json.put("a", true);
    assertTrue(json.getBoolean("a"));
    try {
      json.getBoolean("string");
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      json.getBoolean("n/a");
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetBooleanStringBoolean() {
    json.put("a", true);
    assertFalse(json.getBoolean("null", false));
    assertFalse(json.getBoolean("n/a", false));
    assertFalse(json.getBoolean("string", false));
    assertTrue(json.getBoolean("a", false));
  }


  @Test
  public void testGetBooleanStringPredicateOfString() {
    json.put("a", true);
    assertFalse(json.getBoolean("null", k -> false));
    assertFalse(json.getBoolean("n/a", k -> false));
    assertFalse(json.getBoolean("string", k -> false));
    assertTrue(json.getBoolean("a", k -> false));
  }


  @Test
  public void testGetDouble() {
    json.put("a", Math.PI);
    assertEquals(Math.PI, json.getDouble("a"), 0.0000001d);
    try {
      json.getDouble("string");
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      json.getDouble("n/a");
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetDoubleStringDouble() {
    json.put("a", Math.PI);
    assertEquals(Math.E, json.getDouble("null", Math.E), 0.0000001d);
    assertEquals(Math.E, json.getDouble("n/a", Math.E), 0.0000001d);
    assertEquals(Math.E, json.getDouble("string", Math.E), 0.0000001d);
    assertEquals(Math.PI, json.getDouble("a", Math.E), 0.0000001d);
  }


  @Test
  public void testGetDoubleStringToDoubleFunctionOfString() {
    json.put("a", Math.PI);
    assertEquals(Math.E, json.getDouble("null", k -> Math.E), 0.0000001d);
    assertEquals(Math.E, json.getDouble("n/a", k -> Math.E), 0.0000001d);
    assertEquals(Math.E, json.getDouble("string", k -> Math.E), 0.0000001d);
    assertEquals(Math.PI, json.getDouble("a", k -> Math.E), 0.0000001d);
  }


  @Test
  public void testGetInt() {
    json.put("a", 3);
    assertEquals(3, json.getInt("a"));
    try {
      json.getInt("string");
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      json.getInt("n/a");
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetIntStringInt() {
    json.put("a", 3);
    assertEquals(-1, json.getInt("null", -1));
    assertEquals(-1, json.getInt("n/a", -1));
    assertEquals(-1, json.getInt("string", -1));
    assertEquals(3, json.getInt("a", -1));
  }


  @Test
  public void testGetIntStringToIntFunctionOfString() {
    json.put("a", 3);
    assertEquals(-1, json.getInt("null", k -> -1));
    assertEquals(-1, json.getInt("n/a", k -> -1));
    assertEquals(-1, json.getInt("string", k -> -1));
    assertEquals(3, json.getInt("a", k -> -1));
  }


  @Test
  public void testGetLongSafe() {
    json.put("a", 3L);
    assertEquals(3L, json.getLong("a"));
    try {
      json.getLong("string");
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      json.getLong("n/a");
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetLongStringLong() {
    json.put("a", 3);
    assertEquals(-1, json.getLong("null", -1));
    assertEquals(-1, json.getLong("n/a", -1));
    assertEquals(-1, json.getLong("string", -1));
    assertEquals(3L, json.getLong("a", -1));
  }


  @Test
  public void testGetLongStringToLongFunctionOfString() {
    json.put("a", 3);
    assertEquals(-1, json.getLong("null", k -> -1));
    assertEquals(-1, json.getLong("n/a", k -> -1));
    assertEquals(-1, json.getLong("string", k -> -1));
    assertEquals(3L, json.getLong("a", k -> -1));
  }


  @Test
  public void testGetObject() {
    assertNotNull(json.getObject("object"));
    try {
      json.getObject("string");
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      json.getObject("n/a");
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetObjectStringFunctionOfStringJsonObject() {
    JObject o1 = new JObject();
    o1.put("a", 3);
    JObject o2 = new JObject();
    o1.put("a", "three");

    json.put("object", o1);
    assertEquals(o2, json.getObject("null", o2));
    assertEquals(o2, json.getObject("n/a", o2));
    assertEquals(o2, json.getObject("string", o2));
    assertEquals(o1, json.getObject("object", o2));
  }


  @Test
  public void testGetObjectStringJsonObject() {
    JObject o1 = new JObject();
    o1.put("a", 3);
    JObject o2 = new JObject();
    o1.put("a", "three");

    json.put("object", o1);
    assertEquals(o2, json.getObject("null", k -> o2));
    assertEquals(o2, json.getObject("n/a", k -> o2));
    assertEquals(o2, json.getObject("string", k -> o2));
    assertEquals(o1, json.getObject("object", k -> o2));
  }


  @Test
  public void testGetString() {
    json.put("string", "text");
    assertEquals("text", json.getString("string"));
    try {
      json.getString("array");
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      json.getString("n/a");
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetStringStringFunctionOfStringString() {
    String s1 = "text";
    String s2 = "not-present";
    json.put("string", s1);
    assertEquals(s2, json.getString("null", k -> s2));
    assertEquals(s2, json.getString("n/a", k -> s2));
    assertEquals(s2, json.getString("array", k -> s2));
    assertEquals(s1, json.getString("string", k -> s2));
  }


  @Test
  public void testGetStringStringString() {
    String s1 = "text";
    String s2 = "not-present";
    json.put("string", s1);
    assertEquals(s2, json.getString("null", s2));
    assertEquals(s2, json.getString("n/a", s2));
    assertEquals(s2, json.getString("array", s2));
    assertEquals(s1, json.getString("string", s1));
  }


  @Test
  public void testIsType() {
    assertTrue(json.isType("string", ValueType.STRING));
    assertFalse(json.isType("big number", ValueType.STRING));
    assertTrue(json.isType("big number", ValueType.NUMBER));
  }


  @Test(expected = MissingItemException.class)
  public void testIsType2() {
    json.isType("n/a", ValueType.NUMBER);
  }


  @Test
  public void testJsonObject() {
    assertEquals(11, json.size());
    assertEquals(
        "{\"\\u0007\":\"bell\",\"array\":[],\"big number\":100000,\"boolean\":true,\"null\":null,\"object\":{},\"small number\":1.0E-5,"
            + "\"string\":\"string\",\"\uEEEE\":\"private use\",\"\uD83D\uDE09\":\"winking\",\"\uD83D\uDE2A\":\"sleepy\"}",
        json.toString()
    );

    json.clear();
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < 4; i++) {
      buf.append("\uD83D\uDE2A");
      json.put(buf.toString(), i);
    }
    assertEquals(
        "{\"\uD83D\uDE2A\":0,\"\uD83D\uDE2A\uD83D\uDE2A\":1,\"\uD83D\uDE2A\uD83D\uDE2A\uD83D\uDE2A\":2,\"\uD83D\uDE2A\uD83D\uDE2A\uD83D\uDE2A\uD83D\uDE2A\":3}",
        json.toString()
    );
  }


  @Test
  public void testOptArrayString() {
    JArray a1 = new JArray();
    a1.add(1.0);
    json.put("array", a1);
    assertNull(json.optArray("null"));
    assertNull(json.optArray("n/a"));
    assertNull(json.optArray("string"));
    assertEquals(a1, json.optArray("array"));
  }


  @Test
  public void testOptBigDecimalString() {
    BigDecimal bd = new BigDecimal(Math.PI);
    json.put("bd", bd);
    assertNull(json.optBigDecimal("null"));
    assertNull(json.optBigDecimal("n/a"));
    assertNull(json.optBigDecimal("string"));
    assertEquals(bd, json.optBigDecimal("bd"));
  }


  @Test
  public void testOptBigIntegerString() {
    BigInteger bi = new BigInteger("1234567890");
    json.put("bi", bi);
    assertNull(json.optBigInteger("null"));
    assertNull(json.optBigInteger("n/a"));
    assertNull(json.optBigInteger("string"));
    assertEquals(bi, json.optBigInteger("bi"));
  }


  @Test
  public void testOptBooleanString() {
    json.put("a", true);
    assertNull(json.optBoolean("null"));
    assertNull(json.optBoolean("n/a"));
    assertNull(json.optBoolean("string"));
    assertEquals(Boolean.TRUE, json.optBoolean("a"));
  }


  @Test
  public void testOptDoubleString() {
    Double d = Math.PI;
    json.put("a", d);
    assertNull(json.optDouble("null"));
    assertNull(json.optDouble("n/a"));
    assertNull(json.optDouble("string"));
    assertEquals(d, json.optDouble("a"));
  }


  @Test
  public void testOptIntString() {
    json.put("a", 3);
    assertNull(json.optInt("null"));
    assertNull(json.optInt("n/a"));
    assertNull(json.optInt("string"));
    assertEquals(Integer.valueOf(3), json.optInt("a"));
  }


  @Test
  public void testOptLong() {
    json.put("a", 3);
    assertNull(json.optLong("null"));
    assertNull(json.optLong("n/a"));
    assertNull(json.optLong("string"));
    assertEquals(Long.valueOf(3), json.optLong("a"));
  }


  @Test
  public void testOptObject() {
    JObject o1 = new JObject();
    o1.put("a", 3);
    json.put("object", o1);
    assertNull(json.optObject("null"));
    assertNull(json.optObject("n/a"));
    assertNull(json.optObject("string"));
    assertEquals(o1, json.optObject("object"));
  }


  @Test
  public void testOptString() {
    String s1 = "text";
    json.put("string", s1);
    assertNull(json.optString("null"));
    assertNull(json.optString("n/a"));
    assertNull(json.optString("array"));
    assertEquals(s1, json.optString("string"));
  }


  @Test
  public void testPutString() {
    assertFalse(json.containsKey("a"));
    json.put("a");
    assertTrue(json.containsKey("a"));
    assertEquals(Primitive.NULL, json.get("a"));
  }


  @Test
  public void testPutStringBoolean() {
    assertFalse(json.containsKey("a"));
    json.put("a", true);
    assertTrue(json.containsKey("a"));
    assertEquals(Boolean.TRUE, json.getBoolean("a"));

    assertFalse(json.containsKey("b"));
    json.put("b", (Boolean) null);
    assertTrue(json.containsKey("b"));
    assertEquals(Primitive.NULL, json.get("b"));
  }


  @Test
  public void testPutStringJsonArray() {
    JArray a1 = new JArray();
    a1.add(3.5);
    assertFalse(json.containsKey("a"));
    json.put("a", a1);
    assertTrue(json.containsKey("a"));
    assertEquals(a1, json.getArray("a"));

    assertFalse(json.containsKey("b"));
    json.put("b", (JArray) null);
    assertTrue(json.containsKey("b"));
    assertEquals(Primitive.NULL, json.get("b"));
  }


  @Test
  public void testPutStringJsonObject() {
    JObject o1 = new JObject();
    o1.put("x", 3.5);
    assertFalse(json.containsKey("a"));
    json.put("a", o1);
    assertTrue(json.containsKey("a"));
    assertEquals(o1, json.getObject("a"));

    assertFalse(json.containsKey("b"));
    json.put("b", (JObject) null);
    assertTrue(json.containsKey("b"));
    assertEquals(Primitive.NULL, json.get("b"));
  }


  @Test
  public void testPutStringNumber() {
    Double n = 3.5;
    assertFalse(json.containsKey("a"));
    json.put("a", n);
    assertTrue(json.containsKey("a"));
    assertEquals(n, json.optDouble("a"));

    assertFalse(json.containsKey("b"));
    json.put("b", (Number) null);
    assertTrue(json.containsKey("b"));
    assertEquals(Primitive.NULL, json.get("b"));
  }


  @Test
  public void testPutStringString() {
    String s = "text";
    assertFalse(json.containsKey("a"));
    json.put("a", s);
    assertTrue(json.containsKey("a"));
    assertEquals(s, json.getString("a"));

    assertFalse(json.containsKey("b"));
    json.put("b", (String) null);
    assertTrue(json.containsKey("b"));
    assertEquals(Primitive.NULL, json.get("b"));
  }


  @Test
  public void testRemoveArray() {
    String k1 = "array";
    String k2 = "big number";
    assertTrue(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
    JArray array = json.removeArray(k1);
    assertNotNull(array);
    assertTrue(array.isEmpty());
    assertNull(json.removeArray(k2));
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }


  @Test
  public void testRemoveBoolean() {
    String k1 = "boolean";
    String k2 = "big number";
    assertTrue(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
    assertNotNull(json.removeBoolean(k1));
    assertNull(json.removeBoolean(k2));
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }


  @Test
  public void testRemoveNull() {
    String k1 = "null";
    String k2 = "big number";
    assertTrue(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
    json.removeNull(k1);
    json.removeNull(k2);
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }


  @Test
  public void testRemoveNumber() {
    String k2 = "boolean";
    String k1 = "big number";
    assertTrue(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
    assertNull(json.removeNumber(k2));
    Number n = json.removeNumber(k1);
    assertTrue(n instanceof Integer);
    assertEquals(100_000,n);
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }


  @Test
  public void testRemoveObject() {
    String k1 = "object";
    String k2 = "big number";
    assertTrue(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
    assertNotNull(json.removeObject(k1));
    assertNull(json.removeObject(k2));
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }


  @Test
  public void testRemoveString() {
    String k1 = "string";
    String k2 = "big number";
    assertTrue(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
    assertEquals("string",json.removeString(k1));
    assertNull(json.removeString(k2));
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }


  @Test
  public void toArray() {
    assertEquals(11, json.values().toArray().length);
    assertEquals(11, json.values().toArray(new JsonValue[0]).length);
    assertEquals(11, json.values().toArray(JsonValue[]::new).length);
  }


  @Test(expected = UnsupportedOperationException.class)
  public void valuesAdd() {
    json.values().add(JsonValue.NULL);
  }


  @Test(expected = UnsupportedOperationException.class)
  public void valuesAddAll() {
    json.values().addAll(Set.of(JsonValue.NULL, JsonValue.TRUE));
  }


  @Test
  public void valuesClear() {
    Collection<JsonValue> v = json.values();
    assertFalse(v.isEmpty());
    v.clear();
    assertTrue(json.isEmpty());
    assertTrue(v.isEmpty());
  }


  @Test
  public void valuesContains() {
    assertTrue(json.values().contains(JsonValue.TRUE));
    assertFalse(json.values().contains(PNumber.create(555)));
  }


  @Test
  public void valuesContainsAll() {
    assertTrue(json.values().containsAll(Set.of(JsonValue.TRUE, JsonValue.NULL)));
  }


  @Test
  public void valuesEquals() {
    assertFalse(json.values().equals(new JArray()));
  }


  @Test
  public void valuesIterator() {
    Collection<JsonValue> v = json.values();
    Iterator<JsonValue> iterator = v.iterator();
    assertTrue(iterator.hasNext());
    assertEquals(PString.create("bell"), iterator.next());
    iterator.remove();
    assertFalse(json.containsValue(PString.create("bell")));
  }


  @Test
  public void valuesParallelStream() {
    assertEquals(11, json.values().parallelStream().count());
  }


  @Test
  public void valuesRemove() {
    assertTrue(json.values().remove(JsonValue.TRUE));
    assertFalse(json.containsValue(JsonValue.TRUE));
  }


  @Test
  public void valuesRemoveAll() {
    assertTrue(json.values().removeAll(Set.of(JsonValue.TRUE, JsonValue.FALSE)));
    assertFalse(json.containsValue(JsonValue.TRUE));
  }


  @Test
  public void valuesRemoveIf() {
    assertTrue(json.values().removeIf(v -> v.getValueType() != ValueType.NUMBER));
    assertFalse(json.containsKey("null"));
    assertTrue(json.containsKey("small number"));
  }


  @Test
  public void valuesRetainAll() {
    assertTrue(json.values().retainAll(Set.of(JsonValue.TRUE, JsonValue.EMPTY_JSON_OBJECT)));
    assertEquals(2, json.size());
  }


  @Test
  public void valuesSpliterator() {
    // Spliterator implementation is tested in JArray
    assertNotNull(json.values().spliterator());
  }


  @Test
  public void valuesStream() {
    assertEquals(11, json.values().stream().count());
  }
}
