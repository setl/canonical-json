package io.setl.json.primitive;

import io.setl.json.Canonical;
import io.setl.json.JType;
import io.setl.json.Primitive;
import io.setl.json.parser.NumberParser;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.json.JsonNumber;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PNumber extends PBase implements JsonNumber {

  private static final PString REP_INFINITY = new PString("Infinity");

  private static final PString REP_NAN = new PString("NaN");

  private static final PString REP_NEG_INFINITY = new PString("-Infinity");


  public static PBase create(Number value) {
    if (value instanceof Double || value instanceof Float) {
      double d = value.doubleValue();
      if (Double.isNaN(d)) {
        return REP_NAN;
      }
      if (Double.isInfinite(d)) {
        return d < 0 ? REP_NEG_INFINITY : REP_INFINITY;
      }
      value = new BigDecimal(value.toString());
    }
    return new PNumber(value);
  }


  /**
   * Recover a double from a primitive, allowing for NaN, Infinity and -Infinity.
   *
   * @param primitive the primitive
   *
   * @return the Double, or null if it wasn't a double
   */
  public static Double toDouble(Primitive primitive) {
    if (primitive instanceof PNumber) {
      PNumber pNumber = (PNumber) primitive;
      return pNumber.doubleValue();
    }
    if (primitive instanceof PString) {
      PString pString = (PString) primitive;
      switch (pString.getString().toLowerCase()) {
        case "nan":
          return Double.NaN;
        case "inf": // falls through
        case "+inf": // falls through
        case "infinity": // falls through
        case "+infinity": // falls through
          return Double.POSITIVE_INFINITY;
        case "-inf":
        case "-infinity":
          return Double.NEGATIVE_INFINITY;
      }
    }
    return null;
  }


  private final Number value;


  public PNumber(double value) {
    if (Double.isInfinite(value) || Double.isNaN(value)) {
      throw new IllegalArgumentException("JSON cannot represent NaN, +Infinity nor -Infinity: " + value);
    }
    this.value = BigDecimal.valueOf(value);
  }


  public PNumber(int value) {
    this.value = value;
  }


  public PNumber(long value) {
    this.value = value;
  }


  public PNumber(BigDecimal value) {
    this.value = value;
  }


  public PNumber(BigInteger value) {
    this.value = value;
  }


  /**
   * New instance.
   *
   * @param value the numeric value
   */
  public PNumber(Number value) {
    if( value instanceof Double || value instanceof Float ) {
      double d = value.doubleValue();
      if (Double.isInfinite(d) || Double.isNaN(d)) {
        throw new IllegalArgumentException("JSON cannot represent NaN, +Infinity nor -Infinity: " + value);
      }
    }
    this.value = value;
  }


  public PNumber(NumberParser parse) {
    this(parse.getResult());
  }


  @Override
  public BigDecimal bigDecimalValue() {
    return Primitive.toBigDecimal(value);
  }


  @Override
  public BigInteger bigIntegerValue() {
    return Primitive.toBigInteger(value);
  }


  @Override
  public BigInteger bigIntegerValueExact() {
    if (isIntegral()) {
      return bigIntegerValue();
    }
    throw new ArithmeticException("Not an integer: " + value);
  }


  @Override
  public double doubleValue() {
    return value.doubleValue();
  }


  /**
   * The JSON API requires we test for equality via BigDecimal values. As the canonical JSON does not retain trailing zeros, we actually test for equality by
   * the total ordering of real numbers.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof JsonNumber)) {
      return false;
    }

    // API requires comparison as BigDecimals.
    PNumber pNumber = (PNumber) o;
    Number myNumber = numberValue();
    Number otherNumber = numberValue();

    // TODO finish me
    if (myNumber.equals(otherNumber)) {
      return true;
    }
    return Objects.equals(value, pNumber.value);
  }


  @Override
  public JType getType() {
    return JType.NUMBER;
  }


  @Override
  public Object getValue() {
    return value;
  }


  @Override
  public ValueType getValueType() {
    return ValueType.NUMBER;
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
    if (value instanceof Integer || value instanceof Short || value instanceof Byte || value instanceof AtomicInteger) {
      return value.intValue();
    }
    if (value instanceof Long || value instanceof AtomicLong) {
      return Math.toIntExact(value.longValue());
    }
    if (value instanceof BigInteger) {
      return ((BigInteger) value).intValueExact();
    }
    if (value instanceof BigDecimal) {
      return ((BigDecimal) value).intValueExact();
    }

    // Should be a Double or a Float, so we work with doubles.
    double d = value.doubleValue();
    if ((d % 1.0) != 0.0) {
      throw new ArithmeticException("Not an integer");
    }

    // Convert from double to int to double and see if we still have the same value.
    int v = (int) d;
    if (((double) v) != d) {
      // Not an exact conversion
      throw new ArithmeticException("Out of int range: " + d);
    }
    return v;
  }


  @Override
  public boolean isIntegral() {
    if (value instanceof Long || value instanceof Integer || value instanceof BigInteger || value instanceof Short || value instanceof Byte) {
      return true;
    }
    if (value instanceof Double || value instanceof Float) {
      return (value.doubleValue() % 1.0) == 0.0;
    }

    BigDecimal bd = (value instanceof BigDecimal) ? ((BigDecimal) value) : new BigDecimal(value.toString());
    bd = bd.stripTrailingZeros();
    return bd.scale() <= 0;
  }


  @Override
  public long longValue() {
    return value.longValue();
  }


  @Override
  public long longValueExact() {
    if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte || value instanceof AtomicLong
        || value instanceof AtomicInteger) {
      return value.longValue();
    }
    if (value instanceof BigInteger) {
      return ((BigInteger) value).longValueExact();
    }
    if (value instanceof BigDecimal) {
      return ((BigDecimal) value).longValueExact();
    }

    // Should be a Double or a Float, so we work with doubles.
    double d = value.doubleValue();
    if ((d % 1.0) != 0.0) {
      throw new ArithmeticException("Not an integer");
    }

    // convert to long and back to double and check if we still have the same value
    long l = (long) d;
    if (((double) l) != d) {
      // Not an exact conversion
      throw new ArithmeticException("Out of long range: " + d);
    }
    return l;
  }


  @Override
  public Number numberValue() {
    return value;
  }


  @Override
  public String toString() {
    return Canonical.format(value);
  }


  @Override
  public void writeTo(Writer writer) throws IOException {
    Canonical.format(writer, value);
  }
}
