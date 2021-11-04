package io.setl.json.primitive.numbers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue.ValueType;

import org.junit.Test;

/**
 * @author Simon Greatrix on 25/01/2020.
 */
public class CJBigDecimalTest {

  private BigDecimal v = BigDecimal.valueOf(Math.PI);

  private CJBigDecimal bd = new CJBigDecimal(v, true);


  @Test
  public void bigDecimalValue() {
    assertEquals(0, v.compareTo(bd.bigDecimalValue()));
  }


  @Test
  public void bigIntegerValue() {
    BigInteger i2 = BigInteger.ONE.shiftLeft(70);
    BigDecimal v2 = new BigDecimal(i2).add(new BigDecimal("0.3"));
    CJBigDecimal bd = new CJBigDecimal(v2, true);
    assertEquals(0, i2.compareTo(bd.bigIntegerValue()));
  }


  @Test
  public void bigIntegerValueExact() {
    BigInteger i2 = BigInteger.ONE.shiftLeft(70);
    BigDecimal v2 = new BigDecimal(i2);
    CJBigDecimal bd = new CJBigDecimal(v2, true);
    assertEquals(0, i2.compareTo(bd.bigIntegerValueExact()));
  }


  @Test
  public void copy() {
    assertSame(bd, bd.copy());
  }


  @Test
  public void doubleValue() {
    assertEquals(Math.PI, bd.doubleValue(), Math.ulp(Math.PI));
  }


  @Test
  public void equalsValueBigDecimal() {
    assertTrue(bd.equalsValue(v));
  }


  @Test
  public void equalsValueBigInteger() {
    assertFalse(bd.equalsValue(BigInteger.TEN));
  }


  @Test
  public void equalsValueLong() {
    assertFalse(bd.equalsValue(3L));
  }


  @Test
  public void getNumberType() {
    assertEquals(CJNumber.TYPE_DECIMAL, bd.getNumberType());
  }


  @Test
  public void getValue() {
    assertEquals(0, v.compareTo((BigDecimal) bd.getValue()));
  }


  @Test
  public void getValueType() {
    assertEquals(ValueType.NUMBER, bd.getValueType());
  }


  @Test
  public void intValue() {
    assertEquals(3, bd.intValue());
  }


  @Test(expected = ArithmeticException.class)
  public void intValueExact() {
    bd.intValueExact();
  }


  @Test
  public void isIntegral() {
    assertFalse(bd.isIntegral());

    BigInteger i2 = BigInteger.ONE.shiftLeft(70);
    BigDecimal v2 = new BigDecimal(i2);
    CJBigDecimal bd2 = new CJBigDecimal(v2, true);
    assertTrue(bd2.isIntegral());
  }


  @Test
  public void longValue() {
    assertEquals(3L, bd.longValue());
  }


  @Test(expected = ArithmeticException.class)
  public void longValueExact() {
    bd.longValueExact();
  }


  @Test
  public void numberValue() {
    Number n = bd.numberValue();
    assertEquals(0, v.compareTo((BigDecimal) n));
  }


  @Test
  public void testNumberValue() {
    assertEquals(0, v.compareTo((BigDecimal) bd.numberValue()));
  }


  @Test
  public void testToString() {
    assertNotNull(bd.toString());

    BigDecimal bd = new BigDecimal("1e+31");
    CJBigDecimal pbd = new CJBigDecimal(bd, true);
    assertEquals(bd.toBigIntegerExact().toString(), pbd.toString());

    bd = new BigDecimal("1.234e+40");
    pbd = new CJBigDecimal(bd, true);
    assertEquals(bd.toBigIntegerExact().toString(), pbd.toString());

    bd = new BigDecimal("-1234e+31");
    pbd = new CJBigDecimal(bd, true);
    assertEquals(bd.toBigIntegerExact().toString(), pbd.toString());
  }

}
