package io.setl.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("checkstyle:AvoidEscapedUnicodeCharacters")
public class JObjectTest {

  private JObject json = new JObject();


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
  public void entrySet() {
    Set<Entry<String, JsonValue>> entrySet = json.entrySet();
    // TODO
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

    JObject fixed = JObject.fixMap(hm);
    assertEquals("{\"a\":[1,2,3],\"b\":true,\"c\":null,\"d\":5,\"e\":{},\"f\":\"text\",\"g\":true}", fixed.toString());
    JObject fixed2 = JObject.fixMap(fixed);
    assertSame(fixed, fixed2);

    HashMap<String, Object> hm2 = new HashMap<>();
    hm.forEach((k, v) -> hm2.put(String.valueOf(k), v));
    JObject fixed3 = new JObject(hm2);
    assertEquals(fixed, fixed3);
    assertNotSame(fixed, fixed3);

    hm.clear();
    hm.put(1, 2);
    try {
      JObject.fixMap(hm);
      fail();
    } catch (IllegalArgumentException e) {
      // correct
    }

    hm.clear();
    hm.put(null, null);
    try {
      JObject.fixMap(hm);
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
    assertFalse(json.isType("n/a", ValueType.NUMBER));
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
    json.removeArray(k1);
    json.removeArray(k2);
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }


  @Test
  public void testRemoveBoolean() {
    String k1 = "boolean";
    String k2 = "big number";
    assertTrue(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
    json.removeBoolean(k1);
    json.removeBoolean(k2);
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
    json.removeNumber(k1);
    json.removeNumber(k2);
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }


  @Test
  public void testRemoveObject() {
    String k1 = "object";
    String k2 = "big number";
    assertTrue(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
    json.removeObject(k1);
    json.removeObject(k2);
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }


  @Test
  public void testRemoveString() {
    String k1 = "string";
    String k2 = "big number";
    assertTrue(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
    json.removeString(k1);
    json.removeString(k2);
    assertFalse(json.containsKey(k1));
    assertTrue(json.containsKey(k2));
  }
}
