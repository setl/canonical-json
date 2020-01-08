package io.setl.json.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.setl.json.JType;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue.ValueType;
import org.junit.Test;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PNumberTest {

  @Test
  public void bigDecimalValue() {
    BigDecimal bd = BigDecimal.valueOf(Math.PI);
    PNumber pn = new PNumber(bd);
    assertEquals(bd, pn.bigDecimalValue());

    pn = new PNumber(123);
    assertEquals(BigDecimal.valueOf(123L), pn.bigDecimalValue());
  }


  @Test
  public void bigIntegerValue() {
    PNumber pn = new PNumber(123);
    assertEquals(BigInteger.valueOf(123), pn.bigIntegerValue());
  }


  @Test
  public void bigIntegerValueExact1() {
    PNumber pn = new PNumber(1234);
    assertEquals(BigInteger.valueOf(1234), pn.bigIntegerValueExact());
  }


  @Test(expected = ArithmeticException.class)
  public void bigIntegerValueExact2() {
    PNumber pn = new PNumber(123.4);
    pn.bigIntegerValueExact();
  }


  @Test
  public void doubleValue() {
    PNumber pn = new PNumber(123.4);
    assertEquals(123.4, pn.doubleValue(), Math.ulp(123.4));
  }


  @Test
  public void getType() {
    PNumber pn = new PNumber(567);
    assertEquals(JType.NUMBER, pn.getType());
  }


  @Test
  public void getValue() {
    Long v = Long.valueOf(123456);
    PNumber pn = new PNumber(v);
    assertEquals(v, pn.getValue());
  }


  @Test
  public void getValueType() {
    PNumber pn = new PNumber(567);
    assertEquals(ValueType.NUMBER, pn.getValueType());
  }


  @Test
  public void intValue() {
    PNumber pn = new PNumber(567);
    assertEquals(567, pn.intValue());
  }


  @Test
  public void intValueExact() {
    PNumber pn = new PNumber(567);
    assertEquals(567, pn.intValueExact());
  }


  @Test(expected = ArithmeticException.class)
  public void intValueExact1() {
    PNumber pn = new PNumber(4_000_000_000L);
    pn.intValueExact();
  }


  @Test(expected = ArithmeticException.class)
  public void intValueExact2() {
    PNumber pn = new PNumber(567.8);
    pn.intValueExact();
  }


  @Test(expected = ArithmeticException.class)
  public void intValueExact3() {
    PNumber pn = new PNumber(1.3e+100);
    pn.intValueExact();
  }


  @Test
  public void intValueExact4() {
    PNumber pn = new PNumber(1.3e+3);
    assertEquals(1300, pn.intValueExact());
  }


  @Test
  public void isIntegral() {
    assertTrue(new PNumber(123).isIntegral());
    assertFalse(new PNumber(123.4).isIntegral());

    BigDecimal bd = new BigDecimal("1E+10");
    assertTrue(new PNumber(bd).isIntegral());
    bd = new BigDecimal("1E-10");
    assertFalse(new PNumber(bd).isIntegral());
  }


  @Test
  public void longValue() {
    PNumber pn = new PNumber(567);
    assertEquals(567, pn.longValue());
  }


  @Test
  public void longValueExact() {
    PNumber pn = new PNumber(567);
    assertEquals(567L, pn.longValueExact());
  }


  @Test(expected = ArithmeticException.class)
  public void longValueExact1() {
    PNumber pn = new PNumber(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
    pn.longValueExact();
  }


  @Test(expected = ArithmeticException.class)
  public void longValueExact2() {
    PNumber pn = new PNumber(567.8);
    pn.longValueExact();
  }


  @Test(expected = ArithmeticException.class)
  public void longValueExact3() {
    PNumber pn = new PNumber(1.3e+100);
    pn.intValueExact();
  }


  @Test
  public void longValueExact4() {
    PNumber pn = new PNumber(1.3e+10);
    assertEquals(13000000000L, pn.longValueExact());
  }


  @Test
  public void numberValue() {
    Long v = Long.valueOf(123456);
    PNumber pn = new PNumber(v);
    assertEquals(v, pn.numberValue());
  }


  @Test
  public void testToString() {
    PNumber pn = new PNumber(123);
    assertEquals("123", pn.toString());

    pn = new PNumber(12.3);
    assertEquals("1.23E1", pn.toString());
  }


  @Test
  public void writeTo() throws IOException {
    PNumber pn = new PNumber(123);
    StringWriter writer = new StringWriter();
    pn.writeTo(writer);
    assertEquals("123", writer.toString());

    pn = new PNumber(12.3);
    writer = new StringWriter();
    pn.writeTo(writer);
    assertEquals("1.23E1", writer.toString());
  }
}