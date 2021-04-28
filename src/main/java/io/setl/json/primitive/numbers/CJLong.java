package io.setl.json.primitive.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A number which is a Java long.
 *
 * @author Simon Greatrix on 24/01/2020.
 */
public class CJLong extends CJNumber {

  private final long value;


  CJLong(long value) {
    this.value = value;
  }


  @Override
  public BigDecimal bigDecimalValue() {
    return BigDecimal.valueOf(value);
  }


  @Override
  public BigInteger bigIntegerValue() {
    return BigInteger.valueOf(value);
  }


  @Override
  public BigInteger bigIntegerValueExact() {
    return bigIntegerValue();
  }


  @Override
  public double doubleValue() {
    return (double) value;
  }


  @Override
  protected boolean equalsValue(long other) {
    return value == other;
  }


  @Override
  protected boolean equalsValue(BigInteger other) {
    try {
      return value == other.longValueExact();
    } catch (ArithmeticException e) {
      return false;
    }
  }


  @Override
  protected boolean equalsValue(BigDecimal other) {
    try {
      return value == other.longValueExact();
    } catch (ArithmeticException e) {
      return false;
    }
  }


  @Override
  public int getNumberType() {
    return TYPE_LONG;
  }


  @Override
  public Object getValue() {
    return value;
  }


  @Override
  public int intValue() {
    return (int) value;
  }


  @Override
  public int intValueExact() {
    return Math.toIntExact(value);
  }


  @Override
  public boolean isIntegral() {
    return true;
  }


  @Override
  public long longValue() {
    return value;
  }


  @Override
  public long longValueExact() {
    return value;
  }


  @Override
  public Number numberValue() {
    return value;
  }


  @Override
  public String toString() {
    return Long.toString(value);
  }

}
