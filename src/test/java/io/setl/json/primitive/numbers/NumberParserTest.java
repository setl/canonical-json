package io.setl.json.primitive.numbers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import io.setl.json.io.Input;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class NumberParserTest {

  private CJNumber call(Number n) {
    return call(n.toString());
  }


  private CJNumber call(String s) {
    return invoke(new BigDecimal(s));
  }


  private CJNumber invoke(BigDecimal value) {
    Input input = new Input(new StringReader(value.toString()));
    NumberParser np = new NumberParser(input);
    CJNumber result = np.parse(input.read());
    assertEquals(0, value.compareTo(new BigDecimal(result.toString())));
    return result;
  }


  @Test
  public void testBig() {
    CJNumber pn = call(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
    assertEquals(CJNumber.TYPE_BIG_INT, pn.getNumberType());

    pn = call(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE));
    assertEquals(CJNumber.TYPE_BIG_INT, pn.getNumberType());

    // 30 zeros
    pn = call("1000000000000000000000000000000");
    assertEquals(CJNumber.TYPE_BIG_INT, pn.getNumberType());

    // 31 zeros
    pn = call("10000000000000000000000000000000");
    assertEquals(CJNumber.TYPE_DECIMAL, pn.getNumberType());

    pn = call("1000000000000000000000000000000010000000000000000000000000000000");
    assertEquals(CJNumber.TYPE_DECIMAL, pn.getNumberType());
  }


  @Test
  public void testInt() {
    CJNumber pn = call(0);
    assertEquals(CJNumber.TYPE_INT, pn.getNumberType());
    pn = call(1000000);
    assertEquals(CJNumber.TYPE_INT, pn.getNumberType());
    pn = call("-1000000.000");
    assertEquals(CJNumber.TYPE_INT, pn.getNumberType());

    pn = call(Integer.MIN_VALUE);
    assertEquals(CJNumber.TYPE_INT, pn.getNumberType());
    pn = call(Integer.MIN_VALUE / 10);
    assertEquals(CJNumber.TYPE_INT, pn.getNumberType());

    pn = call(Integer.MAX_VALUE);
    assertEquals(CJNumber.TYPE_INT, pn.getNumberType());
    pn = call(Integer.MAX_VALUE / 10);
    assertEquals(CJNumber.TYPE_INT, pn.getNumberType());
  }


  @Test
  public void testLong() {
    CJNumber pn = call(-1L + Integer.MIN_VALUE);
    assertEquals(CJNumber.TYPE_LONG, pn.getNumberType());
    pn = call(Long.MIN_VALUE);
    assertEquals(CJNumber.TYPE_LONG, pn.getNumberType());
    pn = call(Long.MIN_VALUE / 10);
    assertEquals(CJNumber.TYPE_LONG, pn.getNumberType());

    pn = call(1L + Integer.MAX_VALUE);
    assertEquals(CJNumber.TYPE_LONG, pn.getNumberType());
    pn = call(Long.MAX_VALUE);
    assertEquals(CJNumber.TYPE_LONG, pn.getNumberType());
    pn = call(Long.MAX_VALUE / 10);
    assertEquals(CJNumber.TYPE_LONG, pn.getNumberType());
  }

}
