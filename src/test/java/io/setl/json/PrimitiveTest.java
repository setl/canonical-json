package io.setl.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import io.setl.json.primitive.PNull;
import io.setl.json.primitive.numbers.PNumber;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.numbers.PInt;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

public class PrimitiveTest {

  @Test
  public void test() {
    Primitive primitive = Primitive.create("123");
    assertEquals(JType.STRING, primitive.getType());
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
    testCreate(JType.NULL, null, null);
    testCreate(JType.NULL, null, Primitive.NULL);
    testCreate(JType.BOOLEAN, true, true);
    testCreate(JType.BOOLEAN, false, false);
    testCreate(JType.STRING, "abc", "abc");
    testCreate(JType.NUMBER, 123, 123);
    testCreate(JType.ARRAY, new JArray(), new JArray());
    testCreate(JType.OBJECT, new JObject(), new JObject());
    testCreate(JType.ARRAY, new JArray(), new ArrayList<>());
    testCreate(JType.OBJECT, new JObject(), new HashMap<>());

    try {
      Primitive.create(this.getClass());
      fail();
    } catch (IllegalArgumentException e) {
      // correct
    }
  }


  private void testCreate(JType type, Object check, Object value) {
    Primitive primitive = Primitive.create(value);
    assertEquals(type, primitive.getType());
    assertEquals(check, primitive.getValue());
  }


  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void testEquals() {
    Primitive primitive = Primitive.create("123");
    primitive.hashCode();
    Primitive.NULL.hashCode();
    assertEquals(primitive, primitive);
    assertNotEquals(null, primitive);
    assertEquals(primitive, new PString("123"));
    assertNotEquals(primitive, new PString("456"));
    assertNotEquals(primitive, new PInt(123));
    assertNotEquals(primitive, PNull.NULL);
    assertEquals(Primitive.NULL, PNull.NULL);
    assertNotEquals(Primitive.NULL, new PString("123"));
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
    assertEquals("\"abc\"", new PString("abc").toString());
    assertEquals("true", Primitive.TRUE.toString());
    assertEquals("5.0E-1", PNumber.create(0.5).toString());
  }
}
