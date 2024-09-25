package io.setl.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import static io.setl.json.CJArray.asArray;
import static io.setl.json.CJArray.fixCollection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Spliterator;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

import org.junit.jupiter.api.Test;

import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import io.setl.json.primitive.CJTrue;
import io.setl.json.primitive.numbers.CJNumber;

public class CJArrayTest {

  @Test
  public void indexOf() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3, null, 2, 3, null));
    assertEquals(1, ja.indexOf(2));
    assertEquals(2, ja.indexOf(CJNumber.create(3)));
    assertEquals(3, ja.indexOf(null));
  }


  @Test
  public void isNull() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3, null));
    assertFalse(ja.isNull(2));
    assertTrue(ja.isNull(3));
  }


  @Test
  public void lastIndexOf() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3, null, 2, 3, null));
    assertEquals(4, ja.lastIndexOf(2));
    assertEquals(5, ja.lastIndexOf(CJNumber.create(3)));
    assertEquals(6, ja.lastIndexOf(null));
  }


  @Test
  public void optValue() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3, null));
    assertNull(ja.optValue(-3));
    assertNotNull(ja.optValue(3));
  }


  @Test
  public void optimiseStorage() {
    CJArray ja1 = new CJArray(Arrays.asList(1, 1, 2));
    CJArray ja2 = new CJArray(Arrays.asList(1, 2, 3, ja1, JsonValue.EMPTY_JSON_OBJECT));
    ja2.optimiseStorage();
    assertSame(ja1.get(0), ja1.get(1));
    assertSame(ja1.get(0), ja2.get(0));
    assertSame(ja1.get(2), ja2.get(1));
    assertEquals("[1,2,3,[1,1,2],{}]", ja2.toString());
  }


  @Test
  public void parallelStream() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3, null));
    assertNotNull(ja.parallelStream());
    assertEquals(4, ja.parallelStream().count());
  }


  @Test
  public void remove() {
    CJArray ja = new CJArray(Arrays.asList(1, "2", 3, null));
    assertTrue(ja.remove("2"));
    assertFalse(ja.remove("2"));
  }


  @Test
  public void removeAll() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3, 4, 5, 6));
    assertTrue(ja.removeAll(Arrays.asList(1, 2, 3)));
    assertFalse(ja.removeAll(Arrays.asList(1, 2, 3)));
  }


  @Test
  public void sort() {
    CJArray ja = new CJArray(Arrays.asList(3, 1, 4, 1, 5, 2, null, 9, 6));
    ja.sort(Comparator.comparing(JsonValue::toString));
    assertEquals("[1,1,2,3,4,5,6,9,null]", ja.toString());
  }


  @Test
  public void stream() {
    CJArray ja = new CJArray(Arrays.asList(3, 1, 4, 1, 5, 2, null, 9, 6));
    assertEquals(ja.size(), ja.stream().count());
  }


  @Test
  public void subList() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    List<JsonValue> ja2 = ja.subList(4, 7);
    ja2.set(1, Canonical.create("5"));
    assertEquals("[0,1,2,3,4,\"5\",6,7,8,9]", ja.toString());
  }


  @Test
  public void testAddAllCollectionOfQextendsPrimitive() {
    CJArray ja = new CJArray();
    ja.addAll(Arrays.asList(Canonical.TRUE, null));
    assertEquals(Canonical.TRUE, ja.get(0));
    assertEquals(Canonical.NULL, ja.get(1));
  }


  @Test
  public void testAddAllIntCollectionOfQextendsPrimitive() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ja.addAll(1, Arrays.asList(Canonical.TRUE, null));
    assertEquals(Canonical.TRUE, ja.get(1));
    assertEquals(Canonical.NULL, ja.get(2));
  }


  @Test
  public void testAddBoolean() {
    CJArray ja = new CJArray();
    ja.add(false);
    ja.add(true);
    ja.add((Boolean) null);
    assertEquals(3, ja.size());
    assertEquals(Boolean.FALSE, ja.optBoolean(0));
    assertEquals(Boolean.TRUE, ja.optBoolean(1));
    assertNull(ja.optBoolean(2));
  }


  @Test
  public void testAddIntBoolean() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ja.add(2, true);
    ja.add(2, false);
    ja.add(2, (Boolean) null);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.optBoolean(2));
    assertFalse(ja.optBoolean(3));
    assertTrue(ja.optBoolean(4));
    assertEquals(2, ja.getInt(5, -1));
  }


  @Test
  public void testAddIntJsonArray() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    CJArray ja2 = new CJArray(Arrays.asList(8, 9));
    ja.add(2, ja2);
    ja.add(2, (CJArray) null);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.optArray(2));
    assertEquals(ja2, ja.optArray(3));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddIntJsonObject() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    CJObject jo = new CJObject(Collections.singletonMap("a", 1));
    ja.add(2, jo);
    ja.add(2, (CJObject) null);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.optObject(2));
    assertEquals(jo, ja.optObject(3));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddIntNumber() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ja.add(2, 10);
    ja.add(2, (Number) null);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.optInt(2));
    assertEquals(10, ja.getInt(3, -1));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddIntPrimitive() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ja.add(2, Canonical.TRUE);
    ja.add(2, (Canonical) null);
    assertEquals(1, ja.getInt(1, -1));
    assertEquals(Canonical.NULL, ja.get(2));
    assertEquals(Canonical.TRUE, ja.get(3));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddIntString() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ja.add(2, "abc");
    ja.add(2, (String) null);
    assertEquals(1, ja.getInt(1, -1));
    assertEquals(Canonical.NULL, ja.get(2));
    assertEquals("abc", ja.optString(3));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddJsonArray() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    CJArray ja2 = new CJArray(Arrays.asList(8, 9));
    ja.add(ja2);
    ja.add((CJArray) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals(ja2, ja.optArray(3));
    assertNull(ja.optArray(4));
  }


  @Test
  public void testAddJsonObject() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    CJObject jo = new CJObject(Collections.singletonMap("a", 1));
    ja.add(jo);
    ja.add((CJObject) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals(jo, ja.optObject(3));
    assertNull(ja.optObject(4));
  }


  @Test
  public void testAddNull() {
    CJArray ja = new CJArray();
    ja.addNull();
    assertEquals(1, ja.size());
    assertEquals(Canonical.NULL, ja.get(0));
  }


  @Test
  public void testAddNullInt() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ja.addNull(2);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.optInt(2));
    assertEquals(2, ja.getInt(3, -1));
  }


  @Test
  public void testAddNumber() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ja.add(3);
    ja.add((Number) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals(3, ja.getInt(3, -1));
    assertNull(ja.optInt(4));
  }


  @Test
  public void testAddPrimitive() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ja.add(Canonical.TRUE);
    ja.add((Canonical) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals(Canonical.TRUE, ja.get(3));
    assertEquals(Canonical.NULL, ja.get(4));
  }


  @Test
  public void testAddString() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ja.add("abc");
    ja.add((String) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals("abc", ja.optString(3));
    assertNull(ja.optString(4));
  }


  @Test
  public void testAsJsonArray() {
    CJArray ja = new CJArray();
    assertSame(ja, ja.asJsonArray());
  }


  @Test
  public void testAsJsonObject() {
    CJArray ja = new CJArray();
    IncorrectTypeException e = assertThrows(IncorrectTypeException.class, () -> ja.asJsonObject());
    assertEquals("Item has type ARRAY. Required OBJECT.", e.getMessage());
  }


  @Test
  public void testContains() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    assertTrue(ja.contains(0));
    assertTrue(ja.contains(CJNumber.create(1)));
    assertFalse(ja.contains("x"));
  }


  @Test
  public void testContainsAll() {
    CJArray ja = new CJArray(Arrays.asList(0, null, true, 1, 2));
    assertTrue(ja.containsAll(Arrays.asList(JsonValue.NULL, true, 2)));
    assertFalse(ja.containsAll(Arrays.asList(JsonValue.NULL, true, 2, 3, 4, 5)));
  }


  @Test
  public void testFixCollection() {
    CJArray ja = new CJArray();
    assertSame(ja, asArray(ja));
    ja.add("abc");
    ja.add(123);
    ja.addNull();
    ja.add(true);
    assertEquals(ja, asArray(Arrays.asList("abc", CJNumber.create(123), null, JsonValue.TRUE)));
  }


  @Test
  public void testFixPrimitiveCollection() {
    CJArray ja = new CJArray();
    ja.add(true);
    ja.addNull();
    ja.add(123);

    Collection<? extends JsonValue> fixed = fixCollection(Arrays.asList(Canonical.TRUE, null, Canonical.create(123)));
    assertEquals(ja, fixed);
  }


  @Test
  public void testGetArrayInt() {
    List<Integer> int1 = Arrays.asList(8, 9);
    List<Integer> int2 = Arrays.asList(-8, -9);

    CJArray ja = new CJArray(Arrays.asList(0, int1, 4));
    CJArray j1 = new CJArray(int1);
    CJArray j2 = new CJArray(int2);

    assertNull(ja.optArray(0));
    assertEquals(j1, ja.optArray(1));

    assertEquals(j2, ja.getArray(0, j2));
    assertEquals(j1, ja.getArray(1, j2));

    assertEquals(j2, ja.getArray(0, k -> j2));
    assertEquals(j1, ja.getArray(1, k -> j2));

    assertEquals(j1, ja.getArray(1));
    try {
      ja.getArray(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getArray(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getArray(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetBigDecimalInt() {
    BigDecimal bd1 = new BigDecimal(123);
    BigDecimal bd2 = new BigDecimal(789);

    CJArray ja = new CJArray(Arrays.asList("a", bd1, "d"));

    assertNull(ja.optBigDecimal(0));
    assertEquals(bd1, ja.optBigDecimal(1));

    assertEquals(bd2, ja.getBigDecimal(0, bd2));
    assertEquals(bd1, ja.getBigDecimal(1, bd2));

    assertEquals(bd2, ja.getBigDecimal(0, k -> bd2));
    assertEquals(bd1, ja.getBigDecimal(1, k -> bd2));

    assertEquals(bd1, ja.getBigDecimal(1));
    try {
      ja.getBigDecimal(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getBigDecimal(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getBigDecimal(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetBigInteger() {
    BigInteger bd1 = new BigInteger("123");
    BigInteger bd2 = new BigInteger("789");

    CJArray ja = new CJArray(Arrays.asList("a", bd1, "d"));

    assertNull(ja.optBigInteger(0));
    assertEquals(bd1, ja.optBigInteger(1));

    assertEquals(bd2, ja.getBigInteger(0, bd2));
    assertEquals(bd1, ja.getBigInteger(1, bd2));

    assertEquals(bd2, ja.getBigInteger(0, k -> bd2));
    assertEquals(bd1, ja.getBigInteger(1, k -> bd2));

    assertEquals(bd1, ja.getBigInteger(1));
    try {
      ja.getBigInteger(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getBigInteger(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getBigInteger(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetBooleanInt() {
    CJArray ja = new CJArray(Arrays.asList("a", true, "d"));

    assertNull(ja.optBoolean(0));
    assertEquals(Boolean.TRUE, ja.optBoolean(1));

    assertEquals(Boolean.FALSE, ja.getBoolean(0, Boolean.FALSE));
    assertEquals(Boolean.TRUE, ja.getBoolean(1, Boolean.FALSE));

    assertEquals(Boolean.FALSE, ja.getBoolean(0, k -> Boolean.FALSE));
    assertEquals(Boolean.FALSE, ja.getBoolean(-5, k -> Boolean.FALSE));
    assertEquals(Boolean.FALSE, ja.getBoolean(10, k -> Boolean.FALSE));
    assertEquals(Boolean.TRUE, ja.getBoolean(1, k -> Boolean.FALSE));

    assertEquals(Boolean.TRUE, ja.getBoolean(1));
    try {
      ja.getBoolean(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getBoolean(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getBoolean(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetDoubleInt() {
    CJArray ja = new CJArray(Arrays.asList("a", 3.5, "d"));

    assertNull(ja.optDouble(0));
    assertEquals(3.5, ja.optDouble(1), 0.0000001);

    assertEquals(-1.0, ja.getDouble(0, -1.0), 0.0000001);
    assertEquals(3.5, ja.getDouble(1, -1.0), 0.0000001);

    assertEquals(-1.0, ja.getDouble(0, k -> -1.0), 0.0000001);
    assertEquals(3.5, ja.getDouble(1, k -> -1.0), 0.0000001);

    assertEquals(3.5, ja.getDouble(1), 0.0000001);
    try {
      ja.getDouble(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getDouble(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getDouble(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }

    ja = new CJArray(Arrays.asList(Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
        Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY
    ));
    assertEquals("[\"NaN\",\"-Infinity\",\"Infinity\",\"NaN\",\"-Infinity\",\"Infinity\"]", ja.toString());
    assertTrue(Double.isNaN(ja.optDouble(0)));
    assertTrue(Double.isInfinite(ja.getDouble(1)));
    assertTrue(Double.isInfinite(ja.getDouble(2, 5.0)));
  }


  @Test
  public void testGetInt() {
    CJArray ja = new CJArray(Arrays.asList("a", 35, "d"));

    assertNull(ja.optInt(0));
    assertEquals(Integer.valueOf(35), ja.optInt(1));

    assertEquals(-1, ja.getInt(0, -1));
    assertEquals(35, ja.getInt(1, -1));

    assertEquals(-1, ja.getInt(0, k -> -1));
    assertEquals(35, ja.getInt(1, k -> -1));

    assertEquals(35, ja.getInt(1));
    try {
      ja.getInt(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getInt(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getInt(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetJsonArray() {
    CJArray ja = new CJArray(Arrays.asList(JsonValue.EMPTY_JSON_ARRAY, JsonValue.EMPTY_JSON_OBJECT, 123, "abc"));
    assertNotNull(ja.getJsonArray(0));
  }


  @Test
  public void testGetJsonNumber() {
    CJArray ja = new CJArray(Arrays.asList(JsonValue.EMPTY_JSON_ARRAY, JsonValue.EMPTY_JSON_OBJECT, 123, "abc"));
    assertNotNull(ja.getJsonNumber(2));
  }


  @Test
  public void testGetJsonObject() {
    CJArray ja = new CJArray(Arrays.asList(JsonValue.EMPTY_JSON_ARRAY, JsonValue.EMPTY_JSON_OBJECT, 123, "abc"));
    assertNotNull(ja.getJsonObject(1));
  }


  @Test
  public void testGetJsonString() {
    CJArray ja = new CJArray(Arrays.asList(JsonValue.EMPTY_JSON_ARRAY, JsonValue.EMPTY_JSON_OBJECT, 123, "abc"));
    assertNotNull(ja.getJsonString(3));
  }


  @Test
  public void testGetLong() {
    CJArray ja = new CJArray(Arrays.asList("a", 35, "d"));

    assertNull(ja.optLong(0));
    assertEquals(Long.valueOf(35), ja.optLong(1));

    assertEquals(-1, ja.getLong(0, -1));
    assertEquals(35, ja.getLong(1, -1));

    assertEquals(-1, ja.getLong(0, k -> -1));
    assertEquals(35, ja.getLong(1, k -> -1));

    assertEquals(35, ja.getLong(1));
    try {
      ja.getLong(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getLong(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getLong(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetObject() {
    Map<String, String> map1 = Collections.singletonMap("a", "a");
    Map<String, Number> map2 = Collections.singletonMap("b", 1);

    CJArray ja = new CJArray(Arrays.asList(0, map1, 4));
    CJObject j1 = new CJObject(map1);
    CJObject j2 = new CJObject(map2);

    assertNull(ja.optObject(0));
    assertEquals(j1, ja.optObject(1));

    assertEquals(j2, ja.getObject(0, j2));
    assertEquals(j1, ja.getObject(1, j2));

    assertEquals(j2, ja.getObject(0, k -> j2));
    assertEquals(j1, ja.getObject(1, k -> j2));

    assertEquals(j1, ja.getObject(1));
    try {
      ja.getObject(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getObject(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getObject(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetStringInt() {
    CJArray ja = new CJArray(Arrays.asList(1, "xyz", 2));

    assertNull(ja.optString(0));
    assertEquals("xyz", ja.optString(1));

    assertEquals("abc", ja.getString(0, "abc"));
    assertEquals("xyz", ja.getString(1, "abc"));

    assertEquals("abc", ja.getString(0, k -> "abc"));
    assertEquals("xyz", ja.getString(1, k -> "abc"));

    assertEquals("xyz", ja.getString(1));
    try {
      ja.getString(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getString(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getString(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetValue2() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    assertNotNull(ja.getValue(List.class, null));
    assertNull(ja.getValue(Map.class, null));
  }


  @Test
  public void testGetValueAs() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2));
    ClassCastException e = assertThrows(ClassCastException.class, () -> ja.getValuesAs(JsonString.class));
    assertEquals("Cannot cast io.setl.json.primitive.numbers.CJInt to jakarta.json.JsonString", e.getMessage());
  }


  @Test
  public void testHashCode() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    int h = ja.hashCode();
    ja.add(4);
    assertNotEquals(h, ja.hashCode());
  }


  @Test
  public void testJsonArray() {
    CJArray ja = new CJArray();
    assertTrue(ja.isEmpty());
    assertEquals(0, ja.size());
  }


  @Test
  public void testJsonArrayCollectionOfQ() {
    CJArray ja = new CJArray(Arrays.asList("abc", 123, true, null));
    assertEquals(4, ja.size());
    assertEquals(123, ja.getInt(1, -1));
  }


  @Test
  public void testListIterator() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    ListIterator<JsonValue> iter = ja.listIterator();
    assertTrue(iter.hasNext());
    assertFalse(iter.hasPrevious());
    assertEquals(0, iter.nextIndex());
    assertEquals(-1, iter.previousIndex());
    assertEquals(CJNumber.create(1), iter.next());
    iter.remove();
    assertEquals(2, ja.size());
    iter.next();
    iter.set(JsonValue.TRUE);

    Canonical p = ja.getCanonical(0);
    assertTrue(p instanceof CJTrue);

    iter.add(JsonValue.EMPTY_JSON_ARRAY);
    iter.previous();
    assertEquals("[true,[],3]", ja.toString());
  }


  @Test
  public void testRemoveIf() {
    CJArray ja = new CJArray(Arrays.asList(1, "2", 3));
    ja.removeIf(jv -> jv.getValueType() == ValueType.NUMBER);
    assertEquals(1, ja.size());
  }


  @Test
  public void testReplaceAllUnaryOperatorOfPrimitive() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    ja.replaceAll(p -> Canonical.create(p.toString()));
    assertEquals("1", ja.optString(0));
  }


  @Test
  public void testRetainAll() {
    CJArray ja = new CJArray(Arrays.asList(1, "2", 3, 4, 5));
    ja.retainAll(Arrays.asList(1, 2, 3, 4, 5));
    assertEquals(4, ja.size());
    assertFalse(ja.contains("2"));
  }


  @Test
  public void testSetArray() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    CJArray j2 = new CJArray(Arrays.asList(4, 5, 6));
    ja.set(0, (CJArray) null);
    ja.set(1, j2);
    assertEquals(Canonical.NULL, ja.get(0));
    assertEquals(j2, ja.optArray(1));
  }


  @Test
  public void testSetBoolean() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    ja.set(0, (Boolean) null);
    ja.set(1, true);
    ja.set(2, false);
    assertEquals(Canonical.NULL, ja.get(0));
    assertTrue(ja.optBoolean(1));
    assertFalse(ja.optBoolean(2));
  }


  @Test
  public void testSetNull() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    ja.setNull(0);
    assertEquals(Canonical.NULL, ja.get(0));
  }


  @Test
  public void testSetNumber() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    ja.set(0, (Number) null);
    ja.set(1, 123456);
    assertEquals(Canonical.NULL, ja.get(0));
    assertEquals(123456, ja.getInt(1, -1));
  }


  @Test
  public void testSetObject() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    CJObject j2 = new CJObject(Collections.singletonMap("x", "y"));
    ja.set(0, (CJObject) null);
    ja.set(1, j2);
    assertEquals(Canonical.NULL, ja.get(0));
    assertEquals(j2, ja.optObject(1));
  }


  @Test
  public void testSetPrimitive() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    ja.set(0, (Canonical) null);
    ja.set(1, Canonical.TRUE);
    assertEquals(Canonical.NULL, ja.get(0));
    assertEquals(Canonical.TRUE, ja.get(1));
  }


  @Test
  public void testSetString() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    ja.set(0, (String) null);
    ja.set(1, "xyz");
    assertEquals(Canonical.NULL, ja.get(0));
    assertEquals("xyz", ja.optString(1));
  }


  @Test
  public void testSpliterator() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3, true));
    Spliterator<JsonValue> spliterator = ja.spliterator();
    assertEquals(4, spliterator.estimateSize());
    assertEquals(4, spliterator.getExactSizeIfKnown());
    assertTrue(spliterator.hasCharacteristics(Spliterator.ORDERED));
    assertTrue(spliterator.tryAdvance(a -> {
    }));
    assertNotNull(spliterator.trySplit());
    assertNotNull(spliterator.trySplit());
    assertNull(spliterator.trySplit());
  }


  @Test
  public void testToString() {
    CJArray ja = new CJArray(Arrays.asList(1, 2, 3));
    assertEquals("[1,2,3]", ja.toString());
    ja.clear();
    assertEquals("[]", ja.toString());
  }


  @Test
  public void toArray() {
    CJArray ja = new CJArray(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    assertEquals(10, ja.toArray().length);
    assertEquals(10, ja.toArray(new JsonValue[5]).length);
    assertEquals(10, ja.toArray(JsonValue[]::new).length);
  }

}
