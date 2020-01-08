package io.setl.json.primitive;

import io.setl.json.Canonical;
import io.setl.json.JType;
import io.setl.json.Primitive;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.json.JsonNumber;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PNumber extends PBase implements JsonNumber {

  private final Number value;


  public PNumber(Number value) {
    this.value = value;
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
