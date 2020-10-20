package io.setl.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import io.setl.json.primitive.PNull;
import io.setl.json.primitive.numbers.PNumber;
import io.setl.json.primitive.PString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.JsonValue.ValueType;
import org.junit.Test;

public class PrimitiveTest {

  @Test
  public void test() {
    Primitive primitive = Primitive.create("123");
    assertEquals(ValueType.STRING, primitive.getValueType());
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
    testCreate(ValueType.NULL, null, null);
    testCreate(ValueType.NULL, null, Primitive.NULL);
    testCreate(ValueType.TRUE, true, true);
    testCreate(ValueType.FALSE, false, false);
    testCreate(ValueType.STRING, "abc", "abc");
    testCreate(ValueType.NUMBER, 123, 123);
    testCreate(ValueType.ARRAY, new JArray(), new JArray());
    testCreate(ValueType.OBJECT, new JCanonicalObject(), new JCanonicalObject());
    testCreate(ValueType.ARRAY, new JArray(), new ArrayList<>());
    testCreate(ValueType.OBJECT, new JCanonicalObject(), new HashMap<>());

    try {
      Primitive.create(this.getClass());
      fail();
    } catch (IllegalArgumentException e) {
      // correct
    }
  }


  private void testCreate(ValueType type, Object check, Object value) {
    Primitive primitive = Primitive.create(value);
    assertEquals(type, primitive.getValueType());
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
    assertEquals(primitive, PString.create("123"));
    assertNotEquals(primitive, PString.create("456"));
    assertNotEquals(primitive, PNumber.create(123));
    assertNotEquals(primitive, PNull.NULL);
    assertEquals(Primitive.NULL, PNull.NULL);
    assertNotEquals(Primitive.NULL, PString.create("123"));
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
    assertEquals("\"abc\"", PString.create("abc").toString());
    assertEquals("true", Primitive.TRUE.toString());
    assertEquals("5.0E-1", PNumber.cast(0.5).toString());
  }
}
