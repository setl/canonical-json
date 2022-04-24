package io.setl.json.primitive.numbers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue.ValueType;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 25/01/2020.
 */
public class PLongTest {

  private long v = 1234567890123456789L;

  private CJLong pl = new CJLong(v);


  @Test
  public void bigDecimalValue() {
    assertEquals(new BigDecimal(v), pl.bigDecimalValue());
  }


  @Test
  public void bigIntegerValue() {
    assertEquals(BigInteger.valueOf(v), pl.bigIntegerValue());
  }


  @Test
  public void bigIntegerValueExact() {
    assertEquals(BigInteger.valueOf(v), pl.bigIntegerValueExact());
  }


  @Test
  public void copy() {
    assertSame(pl, pl.copy());
  }


  @Test
  public void doubleValue() {
    double d = Double.valueOf("1234567890123456789");
    assertEquals(d, pl.doubleValue(), Math.ulp(d));
  }


  @Test
  public void equalsValueBigDecimal() {
    assertTrue(pl.equalsValue(new BigDecimal(v)));
    assertFalse(pl.equalsValue(new BigDecimal(v).scaleByPowerOfTen(20)));
  }


  @Test
  public void equalsValueBigInteger() {
    assertTrue(pl.equalsValue(BigInteger.valueOf(v)));
    assertFalse(pl.equalsValue(BigInteger.valueOf(v).shiftLeft(20)));
  }


  @Test
  public void equalsValueLong() {
    assertTrue(pl.equalsValue(v));
  }


  @Test
  public void getNumberType() {
    assertEquals(CJNumber.TYPE_LONG, pl.getNumberType());
  }


  @Test
  public void getValue() {
    assertEquals(Long.valueOf(v), pl.getValue());
  }


  @Test
  public void getValueType() {
    assertEquals(ValueType.NUMBER, pl.getValueType());
  }


  @Test
  public void intValue() {
    assertNotEquals(3, pl.intValue());
  }


  @Test
  public void intValueExact() {
    assertThrows(ArithmeticException.class, () -> pl.intValueExact());
  }


  @Test
  public void isIntegral() {
    assertTrue(pl.isIntegral());
  }


  @Test
  public void longValue() {
    assertEquals(v, pl.longValue());
  }


  @Test
  public void longValueExact() {
    assertEquals(v, pl.longValueExact());
  }


  @Test
  public void numberValue() {
    Number n = pl.numberValue();
    assertEquals(Long.valueOf(v), n);
  }


  @Test
  public void testToString() {
    assertNotNull(pl.toString());
  }

}
