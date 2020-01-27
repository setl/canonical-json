package io.setl.json.primitive.numbers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.setl.json.io.Input;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class NumberParserTest {

  private PNumber call(Number n) {
    return call(n.toString());
  }

  private PNumber call(String s) {
    return invoke(new BigDecimal(s));
  }


  private PNumber invoke(BigDecimal value) {
    Input input = new Input(new StringReader(value.toString()));
    NumberParser np = new NumberParser(input);
    PNumber result = np.parse(input.read());
    assertTrue(value.compareTo(new BigDecimal(result.toString())) == 0);
    return result;
  }


  @Test
  public void test() {
    PNumber pn = call(0);
    assertEquals(PNumber.TYPE_INT,pn.getNumberType());
    pn = call(1000000);
    assertEquals(PNumber.TYPE_INT,pn.getNumberType());
    pn = call("-1000000.000");
    assertEquals(PNumber.TYPE_INT,pn.getNumberType());

    pn = call(Integer.MIN_VALUE);
    assertEquals(PNumber.TYPE_INT,pn.getNumberType());
    pn = call(Integer.MIN_VALUE/10);
    assertEquals(PNumber.TYPE_INT,pn.getNumberType());

    pn = call(Integer.MAX_VALUE);
    assertEquals(PNumber.TYPE_INT,pn.getNumberType());
    pn = call(Integer.MAX_VALUE/10);
    assertEquals(PNumber.TYPE_INT,pn.getNumberType());

    pn = call(-1L + Integer.MIN_VALUE);
    assertEquals(PNumber.TYPE_LONG,pn.getNumberType());
    pn = call(Long.MIN_VALUE);
    assertEquals(PNumber.TYPE_LONG,pn.getNumberType());
    pn = call(Long.MIN_VALUE/10);
    assertEquals(PNumber.TYPE_LONG,pn.getNumberType());

    pn = call(1L+Integer.MAX_VALUE);
    assertEquals(PNumber.TYPE_LONG,pn.getNumberType());
    pn = call(Long.MAX_VALUE);
    assertEquals(PNumber.TYPE_LONG,pn.getNumberType());
    pn = call(Long.MAX_VALUE/10);
    assertEquals(PNumber.TYPE_LONG,pn.getNumberType());

    pn = call(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
    assertEquals(PNumber.TYPE_BIG_INT,pn.getNumberType());

    pn = call(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE));
    assertEquals(PNumber.TYPE_BIG_INT,pn.getNumberType());

    // 30 zeros
    pn = call("1000000000000000000000000000000");
    assertEquals(PNumber.TYPE_BIG_INT,pn.getNumberType());

    // 31 zeros
    pn = call("10000000000000000000000000000000");
    assertEquals(PNumber.TYPE_DECIMAL,pn.getNumberType());

    pn = call("1000000000000000000000000000000010000000000000000000000000000000");
    assertEquals(PNumber.TYPE_DECIMAL,pn.getNumberType());
  }

}