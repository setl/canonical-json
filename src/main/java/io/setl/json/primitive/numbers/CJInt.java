package io.setl.json.primitive.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A number which is a Java int.
 *
 * @author Simon Greatrix on 24/01/2020.
 */
public class CJInt extends CJNumber {

  private final int value;


  CJInt(int value) {
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
    return value;
  }


  @Override
  protected boolean equalsValue(long other) {
    return other == value;
  }


  @Override
  protected boolean equalsValue(BigInteger other) {
    try {
      return value == other.intValueExact();
    } catch (ArithmeticException e) {
      return false;
    }
  }


  @Override
  protected boolean equalsValue(BigDecimal other) {
    try {
      return value == other.intValueExact();
    } catch (ArithmeticException e) {
      return false;
    }
  }


  @Override
  public int getNumberType() {
    return TYPE_INT;
  }


  @Override
  public Object getValue() {
    return value;
  }


  @Override
  public int intValue() {
    return value;
  }


  @Override
  public int intValueExact() {
    return value;
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
    return Integer.toString(value);
  }

}
