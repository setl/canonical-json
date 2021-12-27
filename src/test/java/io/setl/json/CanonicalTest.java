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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.json.JsonValue.ValueType;

import org.junit.Test;

import io.setl.json.exception.NotJsonException;
import io.setl.json.primitive.CJNull;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.numbers.CJNumber;

public class CanonicalTest {

  @Test
  public void test() {
    Canonical canonical = Canonical.create("123");
    assertEquals(ValueType.STRING, canonical.getValueType());
    assertEquals("123", canonical.getValue());
    assertEquals(123, canonical.getValue(Number.class, 123));
    assertEquals("123", canonical.getValue(String.class, "abc"));

    assertEquals("123", canonical.getValueSafe(String.class));
    try {
      canonical.getValueSafe(Number.class);
      fail();
    } catch (ClassCastException e) {
      // correct
    }
  }


  @Test(expected = NotJsonException.class)
  public void testCannotCreate() {
    Canonical.create(this.getClass());
  }


  @Test
  public void testCreate() {
    testCreate(ValueType.NULL, null, null);
    testCreate(ValueType.NULL, null, Canonical.NULL);
    testCreate(ValueType.TRUE, true, true);
    testCreate(ValueType.FALSE, false, false);
    testCreate(ValueType.FALSE, false, new AtomicBoolean(false));
    testCreate(ValueType.TRUE, true, new AtomicBoolean(true));
    testCreate(ValueType.STRING, "abc", "abc");
    testCreate(ValueType.NUMBER, 123, 123);
    testCreate(ValueType.NUMBER, 123, new AtomicLong(123));
    testCreate(ValueType.ARRAY, new CJArray(), new CJArray());
    testCreate(ValueType.OBJECT, new CJObject(), new CJObject());
    testCreate(ValueType.ARRAY, new CJArray(), new ArrayList<>());
    testCreate(ValueType.OBJECT, new CJObject(), new HashMap<>());
  }


  private void testCreate(ValueType type, Object check, Object value) {
    Canonical canonical = Canonical.create(value);
    assertEquals(type, canonical.getValueType());
    assertEquals(check, canonical.getValue());
  }


  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void testEquals() {
    Canonical canonical = Canonical.create("123");
    canonical.hashCode();
    Canonical.NULL.hashCode();
    assertEquals(canonical, canonical);
    assertNotEquals(null, canonical);
    assertEquals(canonical, CJString.create("123"));
    assertNotEquals(canonical, CJString.create("456"));
    assertNotEquals(canonical, CJNumber.create(123));
    assertNotEquals(canonical, CJNull.NULL);
    assertEquals(Canonical.NULL, CJNull.NULL);
    assertNotEquals(Canonical.NULL, CJString.create("123"));
    assertNotEquals("null", Canonical.NULL);
  }


  @Test
  public void testToBigDecimal() {
    assertNull(Canonical.toBigDecimal(null));

    BigDecimal bd = new BigDecimal(123);
    BigInteger bi = BigInteger.valueOf(123);
    assertSame(bd, Canonical.toBigDecimal(bd));
    assertEquals(bd, Canonical.toBigDecimal((byte) 123));
    assertEquals(bd, Canonical.toBigDecimal((short) 123));
    assertEquals(bd, Canonical.toBigDecimal(123));
    assertEquals(bd, Canonical.toBigDecimal((long) 123));
    assertEquals(bd, Canonical.toBigDecimal(bi));

    bd = new BigDecimal("0.5");
    assertEquals(bd, Canonical.toBigDecimal(0.5));
    assertEquals(bd, Canonical.toBigDecimal(0.5f));
  }


  @Test
  public void testToBigInteger() {
    assertNull(Canonical.toBigInteger(null));

    BigDecimal bd = new BigDecimal(123);
    BigInteger bi = BigInteger.valueOf(123);
    assertSame(bi, Canonical.toBigInteger(bi));
    assertEquals(bi, Canonical.toBigInteger((byte) 123));
    assertEquals(bi, Canonical.toBigInteger((short) 123));
    assertEquals(bi, Canonical.toBigInteger(123));
    assertEquals(bi, Canonical.toBigInteger((long) 123));
    assertEquals(bi, Canonical.toBigInteger(bd));

    assertEquals(bi, Canonical.toBigInteger(123.0));
    assertEquals(bi, Canonical.toBigInteger(123.0f));
  }


  @Test
  public void testToString() {
    assertEquals("\"abc\"", CJString.create("abc").toString());
    assertEquals("true", Canonical.TRUE.toString());
    assertEquals("5.0E-1", CJNumber.cast(0.5).toString());
  }

}
