package io.setl.json.primitive.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A BigInteger number.
 *
 * @author Simon Greatrix on 24/01/2020.
 */
public class CJBigInteger extends CJNumber {

  /**
   * Max zero divider. If a BigInteger has a modulus of zero relative to this value, it should be a BigDecimal.
   */
  public static final BigInteger MAX_ZEROS = BigInteger.TEN.pow(31);

  /**
   * The maximum number of trailing zeros to allow before a big decimal is preferred to a big integer. The largest BigInteger is around "1E+16777216" which
   * takes 6,966,592 bytes of storage just for the zeros. This allows for an attack where small messages consume large amounts of memory, so we represent
   * numbers with lots of trailing zeros as BigDecimals.
   *
   * <p>BigDecimal's scale is negative for zeros before the decimal point, so the maximum amount of trailing integer zeros is a minimum scale.</p>
   */
  public static final int MIN_SCALE = -30;

  private final BigInteger value;


  CJBigInteger(BigInteger value) {
    this.value = value;
  }


  @Override
  public BigDecimal bigDecimalValue() {
    return new BigDecimal(value);
  }


  @Override
  public BigInteger bigIntegerValue() {
    return value;
  }


  @Override
  public BigInteger bigIntegerValueExact() {
    return value;
  }


  @Override
  public double doubleValue() {
    return value.doubleValue();
  }


  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }


  @Override
  protected boolean equalsValue(long other) {
    try {
      return other == value.longValueExact();
    } catch (ArithmeticException e) {
      return false;
    }
  }


  @Override
  protected boolean equalsValue(BigInteger other) {
    return value.compareTo(other) == 0;
  }


  @Override
  protected boolean equalsValue(BigDecimal other) {
    return new BigDecimal(value).compareTo(other) == 0;
  }


  @Override
  public int getNumberType() {
    return TYPE_BIG_INT;
  }


  @Override
  public Object getValue() {
    return value;
  }


  @Override
  public int hashCode() {
    return super.hashCode();
  }


  @Override
  public int intValue() {
    return value.intValue();
  }


  @Override
  public int intValueExact() {
    return value.intValueExact();
  }


  @Override
  public boolean isIntegral() {
    return true;
  }


  @Override
  public long longValue() {
    return value.longValue();
  }


  @Override
  public long longValueExact() {
    return value.longValueExact();
  }


  @Override
  public Number numberValue() {
    return value;
  }


  @Override
  public String toString() {
    return value.toString();
  }

}
