package io.setl.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class JsonArrayTest extends JsonArray {

  @Test
  public void testAddAllCollectionOfQextendsPrimitive() {
    JsonArray ja = new JsonArray();
    ja.addAll(Arrays.asList(Primitive.TRUE, null));
    assertEquals(Primitive.TRUE, ja.get(0));
    assertEquals(Primitive.NULL, ja.get(1));
  }


  @Test
  public void testAddAllIntCollectionOfQextendsPrimitive() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    ja.addAll(1, Arrays.asList(Primitive.TRUE, null));
    assertEquals(Primitive.TRUE, ja.get(1));
    assertEquals(Primitive.NULL, ja.get(2));
  }


  @Test
  public void testAddBoolean() {
    JsonArray ja = new JsonArray();
    ja.add(false);
    ja.add(true);
    ja.add((Boolean) null);
    assertEquals(3, ja.size());
    assertEquals(Boolean.FALSE, ja.getBoolean(0));
    assertEquals(Boolean.TRUE, ja.getBoolean(1));
    assertNull(ja.getBoolean(2));
  }


  @Test
  public void testAddIntBoolean() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    ja.add(2, true);
    ja.add(2, false);
    ja.add(2, (Boolean) null);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.getBoolean(2));
    assertFalse(ja.getBoolean(3));
    assertTrue(ja.getBoolean(4));
    assertEquals(2, ja.getInt(5, -1));
  }


  @Test
  public void testAddIntJsonArray() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    JsonArray ja2 = new JsonArray(Arrays.asList(8, 9));
    ja.add(2, ja2);
    ja.add(2, (JsonArray) null);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.getArray(2));
    assertEquals(ja2, ja.getArray(3));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddIntJsonObject() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    JsonObject jo = new JsonObject(Collections.singletonMap("a", 1));
    ja.add(2, jo);
    ja.add(2, (JsonObject) null);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.getObject(2));
    assertEquals(jo, ja.getObject(3));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddIntNumber() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    ja.add(2, 10);
    ja.add(2, (Number) null);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.getInt(2));
    assertEquals(10, ja.getInt(3, -1));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddIntPrimitive() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    ja.add(2, Primitive.TRUE);
    ja.add(2, (Primitive) null);
    assertEquals(1, ja.getInt(1, -1));
    assertEquals(Primitive.NULL, ja.get(2));
    assertEquals(Primitive.TRUE, ja.get(3));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddIntString() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    ja.add(2, "abc");
    ja.add(2, (String) null);
    assertEquals(1, ja.getInt(1, -1));
    assertEquals(Primitive.NULL, ja.get(2));
    assertEquals("abc", ja.getString(3));
    assertEquals(2, ja.getInt(4, -1));
  }


  @Test
  public void testAddJsonArray() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    JsonArray ja2 = new JsonArray(Arrays.asList(8, 9));
    ja.add(ja2);
    ja.add((JsonArray) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals(ja2, ja.getArray(3));
    assertNull(ja.getArray(4));
  }


  @Test
  public void testAddJsonObject() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    JsonObject jo = new JsonObject(Collections.singletonMap("a", 1));
    ja.add(jo);
    ja.add((JsonObject) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals(jo, ja.getObject(3));
    assertNull(ja.getObject(4));
  }


  @Test
  public void testAddNull() {
    JsonArray ja = new JsonArray();
    ja.addNull();
    assertEquals(1, ja.size());
    assertEquals(Primitive.NULL, ja.get(0));
  }


  @Test
  public void testAddNullInt() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    ja.addNull(2);
    assertEquals(1, ja.getInt(1, -1));
    assertNull(ja.getInt(2));
    assertEquals(2, ja.getInt(3, -1));
  }


  @Test
  public void testAddNumber() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    ja.add(3);
    ja.add((Number) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals(3, ja.getInt(3, -1));
    assertNull(ja.getInt(4));
  }


  @Test
  public void testAddPrimitive() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    ja.add(Primitive.TRUE);
    ja.add((Primitive) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals(Primitive.TRUE, ja.get(3));
    assertEquals(Primitive.NULL, ja.get(4));
  }


  @Test
  public void testAddString() {
    JsonArray ja = new JsonArray(Arrays.asList(0, 1, 2));
    ja.add("abc");
    ja.add((String) null);
    assertEquals(2, ja.getInt(2, -1));
    assertEquals("abc", ja.getString(3));
    assertNull(ja.getString(4));
  }


  @Test
  public void testFixCollection() {
    JsonArray ja = new JsonArray();
    assertSame(ja, fixCollection(ja));
    ja.add("abc");
    ja.add(123);
    ja.addNull();
    ja.add(true);
    assertEquals(ja, fixCollection(Arrays.asList("abc", 123, null, true)));
  }


  @Test
  public void testFixPrimitveCollection() {
    JsonArray ja = new JsonArray();
    ja.add(true);
    ja.addNull();
    ja.add(123);
    assertEquals(ja, fixPrimitiveCollection(Arrays.asList(Primitive.TRUE, null, Primitive.create(123))));
  }


  @Test
  public void testGetArrayInt() {
    List<Integer> int1 = Arrays.asList(8, 9);
    List<Integer> int2 = Arrays.asList(-8, -9);

    JsonArray ja = new JsonArray(Arrays.asList(0, int1, 4));
    JsonArray j1 = new JsonArray(int1);
    JsonArray j2 = new JsonArray(int2);

    assertNull(ja.getArray(0));
    assertEquals(j1, ja.getArray(1));

    assertEquals(j2, ja.getArray(0, j2));
    assertEquals(j1, ja.getArray(1, j2));

    assertEquals(j2, ja.getArray(0, k -> j2));
    assertEquals(j1, ja.getArray(1, k -> j2));

    assertEquals(j1, ja.getArraySafe(1));
    try {
      ja.getArraySafe(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getArraySafe(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getArraySafe(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetBigDecimalInt() {
    BigDecimal bd1 = new BigDecimal(123);
    BigDecimal bd2 = new BigDecimal(789);

    JsonArray ja = new JsonArray(Arrays.asList("a", bd1, "d"));

    assertNull(ja.getBigDecimal(0));
    assertEquals(bd1, ja.getBigDecimal(1));

    assertEquals(bd2, ja.getBigDecimal(0, bd2));
    assertEquals(bd1, ja.getBigDecimal(1, bd2));

    assertEquals(bd2, ja.getBigDecimal(0, k -> bd2));
    assertEquals(bd1, ja.getBigDecimal(1, k -> bd2));

    assertEquals(bd1, ja.getBigDecimalSafe(1));
    try {
      ja.getBigDecimalSafe(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getBigDecimalSafe(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getBigDecimalSafe(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetBigInteger() {
    BigInteger bd1 = new BigInteger("123");
    BigInteger bd2 = new BigInteger("789");

    JsonArray ja = new JsonArray(Arrays.asList("a", bd1, "d"));

    assertNull(ja.getBigInteger(0));
    assertEquals(bd1, ja.getBigInteger(1));

    assertEquals(bd2, ja.getBigInteger(0, bd2));
    assertEquals(bd1, ja.getBigInteger(1, bd2));

    assertEquals(bd2, ja.getBigInteger(0, k -> bd2));
    assertEquals(bd1, ja.getBigInteger(1, k -> bd2));

    assertEquals(bd1, ja.getBigIntegerSafe(1));
    try {
      ja.getBigIntegerSafe(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getBigIntegerSafe(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getBigIntegerSafe(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetBooleanInt() {
    JsonArray ja = new JsonArray(Arrays.asList("a", true, "d"));

    assertNull(ja.getBoolean(0));
    assertEquals(Boolean.TRUE, ja.getBoolean(1));

    assertEquals(Boolean.FALSE, ja.getBoolean(0, Boolean.FALSE));
    assertEquals(Boolean.TRUE, ja.getBoolean(1, Boolean.FALSE));

    assertEquals(Boolean.FALSE, ja.getBoolean(0, k -> Boolean.FALSE));
    assertEquals(Boolean.FALSE, ja.getBoolean(-5, k -> Boolean.FALSE));
    assertEquals(Boolean.FALSE, ja.getBoolean(10, k -> Boolean.FALSE));
    assertEquals(Boolean.TRUE, ja.getBoolean(1, k -> Boolean.FALSE));

    assertEquals(Boolean.TRUE, ja.getBooleanSafe(1));
    try {
      ja.getBooleanSafe(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getBooleanSafe(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getBooleanSafe(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetDoubleInt() {
    JsonArray ja = new JsonArray(Arrays.asList("a", 3.5, "d"));

    assertNull(ja.getDouble(0));
    assertEquals(3.5, ja.getDouble(1), 0.0000001);

    assertEquals(-1.0, ja.getDouble(0, -1.0), 0.0000001);
    assertEquals(3.5, ja.getDouble(1, -1.0), 0.0000001);

    assertEquals(-1.0, ja.getDouble(0, k -> -1.0), 0.0000001);
    assertEquals(3.5, ja.getDouble(1, k -> -1.0), 0.0000001);

    assertEquals(3.5, ja.getDoubleSafe(1), 0.0000001);
    try {
      ja.getDoubleSafe(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getDoubleSafe(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getDoubleSafe(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetInt() {
    JsonArray ja = new JsonArray(Arrays.asList("a", 35, "d"));

    assertNull(ja.getInt(0));
    assertEquals(Integer.valueOf(35), ja.getInt(1));

    assertEquals(-1, ja.getInt(0, -1));
    assertEquals(35, ja.getInt(1, -1));

    assertEquals(-1, ja.getInt(0, k -> -1));
    assertEquals(35, ja.getInt(1, k -> -1));

    assertEquals(35, ja.getIntSafe(1));
    try {
      ja.getIntSafe(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getIntSafe(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getIntSafe(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetLong() {
    JsonArray ja = new JsonArray(Arrays.asList("a", 35, "d"));

    assertNull(ja.getLong(0));
    assertEquals(Long.valueOf(35), ja.getLong(1));

    assertEquals(-1, ja.getLong(0, -1));
    assertEquals(35, ja.getLong(1, -1));

    assertEquals(-1, ja.getLong(0, k -> -1));
    assertEquals(35, ja.getLong(1, k -> -1));

    assertEquals(35, ja.getLongSafe(1));
    try {
      ja.getLongSafe(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getLongSafe(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getLongSafe(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetObject() {
    Map<String, String> map1 = Collections.singletonMap("a", "a");
    Map<String, Number> map2 = Collections.singletonMap("b", 1);

    JsonArray ja = new JsonArray(Arrays.asList(0, map1, 4));
    JsonObject j1 = new JsonObject(map1);
    JsonObject j2 = new JsonObject(map2);

    assertNull(ja.getObject(0));
    assertEquals(j1, ja.getObject(1));

    assertEquals(j2, ja.getObject(0, j2));
    assertEquals(j1, ja.getObject(1, j2));

    assertEquals(j2, ja.getObject(0, k -> j2));
    assertEquals(j1, ja.getObject(1, k -> j2));

    assertEquals(j1, ja.getObjectSafe(1));
    try {
      ja.getObjectSafe(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getObjectSafe(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getObjectSafe(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testGetStringInt() {
    JsonArray ja = new JsonArray(Arrays.asList(1, "xyz", 2));

    assertNull(ja.getString(0));
    assertEquals("xyz", ja.getString(1));

    assertEquals("abc", ja.getString(0, "abc"));
    assertEquals("xyz", ja.getString(1, "abc"));

    assertEquals("abc", ja.getString(0, k -> "abc"));
    assertEquals("xyz", ja.getString(1, k -> "abc"));

    assertEquals("xyz", ja.getStringSafe(1));
    try {
      ja.getStringSafe(0);
      fail();
    } catch (IncorrectTypeException e) {
      // correct
    }
    try {
      ja.getStringSafe(10);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
    try {
      ja.getStringSafe(-1);
      fail();
    } catch (MissingItemException e) {
      // correct
    }
  }


  @Test
  public void testJsonArray() {
    JsonArray ja = new JsonArray();
    assertTrue(ja.isEmpty());
    assertEquals(0, ja.size());
  }


  @Test
  public void testJsonArrayCollectionOfQ() {
    JsonArray ja = new JsonArray(Arrays.asList("abc", 123, true, null));
    assertEquals(4, ja.size());
    assertEquals(123, ja.getInt(1, -1));
  }


  @Test
  public void testReplaceAllUnaryOperatorOfPrimitive() {
    JsonArray ja = new JsonArray(Arrays.asList(1, 2, 3));
    ja.replaceAll(p -> Primitive.create(p.toString()));
    assertEquals("1", ja.getString(0));
  }


  @Test
  public void testSetArray() {
    JsonArray ja = new JsonArray(Arrays.asList(1, 2, 3));
    JsonArray j2 = new JsonArray(Arrays.asList(4, 5, 6));
    ja.set(0, (JsonArray) null);
    ja.set(1, j2);
    assertEquals(Primitive.NULL, ja.get(0));
    assertEquals(j2, ja.getArray(1));
  }


  @Test
  public void testSetBoolean() {
    JsonArray ja = new JsonArray(Arrays.asList(1, 2, 3));
    ja.set(0, (Boolean) null);
    ja.set(1, true);
    ja.set(2, false);
    assertEquals(Primitive.NULL, ja.get(0));
    assertTrue(ja.getBoolean(1));
    assertFalse(ja.getBoolean(2));
  }


  @Test
  public void testSetNull() {
    JsonArray ja = new JsonArray(Arrays.asList(1, 2, 3));
    ja.setNull(0);
    assertEquals(Primitive.NULL, ja.get(0));
  }


  @Test
  public void testSetNumber() {
    JsonArray ja = new JsonArray(Arrays.asList(1, 2, 3));
    ja.set(0, (Number) null);
    ja.set(1, 123456);
    assertEquals(Primitive.NULL, ja.get(0));
    assertEquals(123456, ja.getInt(1, -1));
  }


  @Test
  public void testSetObject() {
    JsonArray ja = new JsonArray(Arrays.asList(1, 2, 3));
    JsonObject j2 = new JsonObject(Collections.singletonMap("x", "y"));
    ja.set(0, (JsonObject) null);
    ja.set(1, j2);
    assertEquals(Primitive.NULL, ja.get(0));
    assertEquals(j2, ja.getObject(1));
  }


  @Test
  public void testSetPrimitive() {
    JsonArray ja = new JsonArray(Arrays.asList(1, 2, 3));
    ja.set(0, (Primitive) null);
    ja.set(1, Primitive.TRUE);
    assertEquals(Primitive.NULL, ja.get(0));
    assertEquals(Primitive.TRUE, ja.get(1));
  }


  @Test
  public void testSetString() {
    JsonArray ja = new JsonArray(Arrays.asList(1, 2, 3));
    ja.set(0, (String) null);
    ja.set(1, "xyz");
    assertEquals(Primitive.NULL, ja.get(0));
    assertEquals("xyz", ja.getString(1));
  }


  @Test
  public void testToString() {
    JsonArray ja = new JsonArray(Arrays.asList(1, 2, 3));
    assertEquals("[1,2,3]", ja.toString());
    ja.clear();
    assertEquals("[]", ja.toString());
  }

}
