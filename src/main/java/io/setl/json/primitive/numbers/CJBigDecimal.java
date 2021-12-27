package io.setl.json.primitive.numbers;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A BigDecimal number.
 *
 * @author Simon Greatrix on 24/01/2020.
 */
public class CJBigDecimal extends CJNumber {

  /** Some zeros for batching the output of zeros in very big integers. */
  private static final String SOME_ZEROS = "00000000000000000000000000000000";

  private final BigDecimal value;


  CJBigDecimal(BigDecimal value, boolean doStrip) {
    this.value = doStrip ? value.stripTrailingZeros() : value;
  }


  @Override
  public BigDecimal bigDecimalValue() {
    return value;
  }


  @Override
  public BigInteger bigIntegerValue() {
    return value.toBigInteger();
  }


  @Override
  public BigInteger bigIntegerValueExact() {
    return value.toBigIntegerExact();
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
    return value.compareTo(new BigDecimal(other)) == 0;
  }


  @Override
  protected boolean equalsValue(BigDecimal other) {
    return value.compareTo(other) == 0;
  }


  @Override
  public int getNumberType() {
    return TYPE_DECIMAL;
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
    return value.scale() <= 0;
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
    try (StringWriter writer = new StringWriter()) {
      writeTo(writer);
      return writer.toString();
    } catch (IOException e) {
      throw new InternalError("I/O exception without I/O", e);
    }
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    // Handle zero
    if (value.signum() == 0) {
      writer.append("0");
      return;
    }

    // Strip trailing zeros and see if we have an integer
    if (value.scale() <= 0) {
      // This is an integer. We do not convert it
      writer.append(value.unscaledValue().toString());
      int s = -value.scale();
      int b = SOME_ZEROS.length();
      while (s > b) {
        writer.append(SOME_ZEROS);
        s -= b;
      }
      writer.append(SOME_ZEROS, 0, s);
      return;
    }

    // It's a floating point number. First deal with a leading minus sign.
    BigDecimal myValue;
    String sign;
    if (value.signum() == 1) {
      sign = "";
      myValue = value;
    } else {
      sign = "-";
      myValue = value.negate();
    }

    // Get the digits, insert the decimal separator, and append the exponent.
    String unscaled = myValue.unscaledValue().toString(10);
    String unscaledInt;
    String unscaledFraction;
    if (unscaled.length() == 1) {
      // A value like "0.03" has an unscaled value of "3" but the canonical representation requires a non-empty fractional part, so we have to add it.
      unscaledInt = unscaled;
      unscaledFraction = "0";
    } else {
      // insert the decimal separator just after the first digit
      unscaledInt = unscaled.substring(0, 1);
      unscaledFraction = unscaled.substring(1);
    }

    // use the scale and precision to calculate the correct exponent
    int scale = myValue.scale();
    int precision = myValue.precision();
    writer.append(sign);
    writer.append(unscaledInt);
    writer.append('.');
    writer.append(unscaledFraction);
    writer.append('E');
    writer.append(Integer.toString(precision - scale - 1));
  }

}
