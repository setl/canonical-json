package io.setl.json.primitive.numbers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import jakarta.json.JsonValue.ValueType;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 25/01/2020.
 */
public class PIntTest {

  private int v = 1234567890;

  private CJInt pi = new CJInt(v);


  @Test
  public void bigDecimalValue() {
    assertEquals(new BigDecimal(v), pi.bigDecimalValue());
  }


  @Test
  public void bigIntegerValue() {
    assertEquals(BigInteger.valueOf(v), pi.bigIntegerValue());
  }


  @Test
  public void bigIntegerValueExact() {
    assertEquals(BigInteger.valueOf(v), pi.bigIntegerValueExact());
  }


  @Test
  public void copy() {
    assertSame(pi, pi.copy());
  }


  @Test
  public void doubleValue() {
    double d = Double.valueOf("1234567890");
    assertEquals(d, pi.doubleValue(), Math.ulp(d));
  }


  @Test
  public void equalsValueBigDecimal() {
    assertTrue(pi.equalsValue(new BigDecimal(v)));
    assertFalse(pi.equalsValue(new BigDecimal(v).scaleByPowerOfTen(20)));
  }


  @Test
  public void equalsValueBigInteger() {
    assertTrue(pi.equalsValue(BigInteger.valueOf(v)));
    assertFalse(pi.equalsValue(BigInteger.valueOf(v).shiftLeft(20)));
  }


  @Test
  public void equalsValueLong() {
    assertTrue(pi.equalsValue(v));
  }


  @Test
  public void getNumberType() {
    assertEquals(CJNumber.TYPE_INT, pi.getNumberType());
  }


  @Test
  public void getValue() {
    assertEquals(Integer.valueOf(v), pi.getValue());
  }


  @Test
  public void getValueType() {
    assertEquals(ValueType.NUMBER, pi.getValueType());
  }


  @Test
  public void intValue() {
    assertNotEquals(3, pi.intValue());
  }


  @Test
  public void intValueExact() {
    assertEquals(v, pi.intValueExact());
  }


  @Test
  public void isIntegral() {
    assertTrue(pi.isIntegral());
  }


  @Test
  public void longValue() {
    assertEquals(v, pi.longValue());
  }


  @Test
  public void longValueExact() {
    assertEquals(v, pi.longValueExact());
  }


  @Test
  public void numberValue() {
    Number n = pi.numberValue();
    assertEquals(Integer.valueOf(v), n);
  }


  @Test
  public void testToString() {
    assertNotNull(pi.toString());
  }

}
