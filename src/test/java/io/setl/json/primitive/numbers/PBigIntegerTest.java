package io.setl.json.primitive.numbers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import io.setl.json.JType;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue.ValueType;
import org.junit.Test;

/**
 * @author Simon Greatrix on 25/01/2020.
 */
public class PBigIntegerTest {

  private BigInteger v = new BigInteger("123456789012345678901234567890");

  private PBigInteger bi1 = new PBigInteger(v);


  @Test
  public void bigDecimalValue() {
    assertEquals(new BigDecimal(v), bi1.bigDecimalValue());
  }


  @Test
  public void bigIntegerValue() {
    assertEquals(v,bi1.bigIntegerValue());
  }


  @Test
  public void bigIntegerValueExact() {
    assertEquals(v,bi1.bigIntegerValueExact());
  }


  @Test
  public void copy() {
    assertSame(bi1,bi1.copy());
  }


  @Test
  public void doubleValue() {
    double d = Double.valueOf("123456789012345678901234567890");
    assertEquals(d,bi1.doubleValue(),Math.ulp(d));
  }

  @Test
  public void equalsValueBigDecimal() {
    assertTrue(bi1.equalsValue(new BigDecimal(v)));
  }


  @Test
  public void equalsValueBigInteger() {
    assertTrue(bi1.equalsValue(v));
  }


  @Test
  public void equalsValueLong() {
    assertFalse(bi1.equalsValue(3L));
  }


  @Test
  public void getType() {
    assertEquals(JType.NUMBER, bi1.getType());
  }


  @Test
  public void getValue() {
    assertEquals(0, v.compareTo((BigInteger) bi1.getValue()));
  }


  @Test
  public void getValueType() {
    assertEquals(ValueType.NUMBER, bi1.getValueType());
  }


  @Test
  public void intValue() {
    assertNotEquals(3, bi1.intValue());
  }


  @Test(expected = ArithmeticException.class)
  public void intValueExact() {
    bi1.intValueExact();
  }


  @Test
  public void isIntegral() {
    assertTrue(bi1.isIntegral());
  }

  @Test
  public void getNumberType() {
    assertEquals(PNumber.TYPE_BIG_INT, bi1.getNumberType());
  }

  @Test
  public void longValue() {
    assertNotEquals(3L, bi1.longValue());
  }


  @Test(expected = ArithmeticException.class)
  public void longValueExact() {
    bi1.longValueExact();
  }


  @Test
  public void numberValue() {
    Number n = bi1.numberValue();
    assertEquals(0, v.compareTo((BigInteger) n));
  }

  @Test
  public void testToString() {
    assertNotNull(bi1.toString());
  }
}