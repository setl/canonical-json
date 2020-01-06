package io.setl.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

public class PrimitiveTest {

  @Test
  public void test() {
    try {
      new Primitive(Type.NULL, "");
      fail();
    } catch (IllegalArgumentException e) {
      // correct
    }
    try {
      new Primitive(Type.STRING, null);
      fail();
    } catch (IllegalArgumentException e) {
      // correct
    }
    try {
      new Primitive(Type.STRING, 123);
      fail();
    } catch (ClassCastException e) {
      // correct
    }
    Primitive primitive = new Primitive(Type.STRING, "123");
    assertEquals(Type.STRING, primitive.getType());
    assertEquals("123", primitive.getValue());
    assertEquals(123, primitive.getValue(Number.class, 123));
    assertEquals("123", primitive.getValue(String.class, "abc"));

    assertEquals("123", primitive.getValueSafe(String.class));
    try {
      primitive.getValueSafe(Number.class);
      fail();
    } catch (ClassCastException e) {
      // correct
    }
  }


  @Test
  public void testCreate() {
    testCreate(Type.NULL, null, null);
    testCreate(Type.NULL, null, Primitive.NULL);
    testCreate(Type.BOOLEAN, true, true);
    testCreate(Type.BOOLEAN, false, false);
    testCreate(Type.STRING, "abc", "abc");
    testCreate(Type.NUMBER, 123, 123);
    testCreate(Type.ARRAY, new JsonArray(), new JsonArray());
    testCreate(Type.OBJECT, new JsonObject(), new JsonObject());
    testCreate(Type.ARRAY, new JsonArray(), new ArrayList<>());
    testCreate(Type.OBJECT, new JsonObject(), new HashMap<>());

    try {
      Primitive.create(this.getClass());
      fail();
    } catch (IllegalArgumentException e) {
      // correct
    }
  }


  private void testCreate(Type type, Object check, Object value) {
    Primitive primitive = Primitive.create(value);
    assertEquals(type, primitive.getType());
    assertEquals(check, primitive.getValue());
  }


  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void testEquals() {
    Primitive primitive = new Primitive(Type.STRING, "123");
    primitive.hashCode();
    Primitive.NULL.hashCode();
    assertEquals(primitive, primitive);
    assertNotEquals(null, primitive);
    assertEquals(primitive, new Primitive(Type.STRING, "123"));
    assertNotEquals(primitive, new Primitive(Type.STRING, "456"));
    assertNotEquals(primitive, new Primitive(Type.NUMBER, 123));
    assertNotEquals(primitive, new Primitive(Type.NULL, null));
    assertEquals(Primitive.NULL, new Primitive(Type.NULL, null));
    assertNotEquals(Primitive.NULL, new Primitive(Type.STRING, "123"));
    assertNotEquals("null", Primitive.NULL);
  }


  @Test
  public void testToBigDecimal() {
    assertNull(Primitive.toBigDecimal(null));

    BigDecimal bd = new BigDecimal(123);
    BigInteger bi = BigInteger.valueOf(123);
    assertSame(bd, Primitive.toBigDecimal(bd));
    assertEquals(bd, Primitive.toBigDecimal((byte) 123));
    assertEquals(bd, Primitive.toBigDecimal((short) 123));
    assertEquals(bd, Primitive.toBigDecimal(123));
    assertEquals(bd, Primitive.toBigDecimal((long) 123));
    assertEquals(bd, Primitive.toBigDecimal(bi));

    bd = new BigDecimal("0.5");
    assertEquals(bd, Primitive.toBigDecimal(0.5));
    assertEquals(bd, Primitive.toBigDecimal(0.5f));
  }


  @Test
  public void testToBigInteger() {
    assertNull(Primitive.toBigInteger(null));

    BigDecimal bd = new BigDecimal(123);
    BigInteger bi = BigInteger.valueOf(123);
    assertSame(bi, Primitive.toBigInteger(bi));
    assertEquals(bi, Primitive.toBigInteger((byte) 123));
    assertEquals(bi, Primitive.toBigInteger((short) 123));
    assertEquals(bi, Primitive.toBigInteger(123));
    assertEquals(bi, Primitive.toBigInteger((long) 123));
    assertEquals(bi, Primitive.toBigInteger(bd));

    assertEquals(bi, Primitive.toBigInteger(123.0));
    assertEquals(bi, Primitive.toBigInteger(123.0f));
  }


  @Test
  public void testToString() {
    assertEquals("\"abc\"", new Primitive(Type.STRING, "abc").toString());
    assertEquals("true", Primitive.TRUE.toString());
    assertEquals("5.0E-1", new Primitive(Type.NUMBER, 0.5).toString());
  }
}
