package io.setl.json.primitive.numbers;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.stream.JsonParsingException;

/**
 * An invalid number parsed in the JSON.
 *
 * @author Simon Greatrix on 05/02/2020.
 */
class BadNumber extends CJNumber {

  private final JsonParsingException failure;


  public BadNumber(JsonParsingException failure) {
    this.failure = failure;
  }


  @Override
  public BigDecimal bigDecimalValue() {
    throw unsupported();
  }


  @Override
  public BigInteger bigIntegerValue() {
    throw unsupported();
  }


  @Override
  public BigInteger bigIntegerValueExact() {
    throw unsupported();
  }


  protected void check() {
    throw new JsonParsingException("Numeric value could not be created", failure, failure.getLocation());
  }


  @Override
  public double doubleValue() {
    throw unsupported();
  }


  @Override
  protected boolean equalsValue(long other) {
    throw unsupported();
  }


  @Override
  protected boolean equalsValue(BigInteger other) {
    throw unsupported();
  }


  @Override
  protected boolean equalsValue(BigDecimal other) {
    throw unsupported();
  }


  @Override
  public int getNumberType() {
    throw unsupported();
  }


  @Override
  public Object getValue() {
    throw unsupported();
  }


  @Override
  public int intValue() {
    throw unsupported();
  }


  @Override
  public int intValueExact() {
    throw unsupported();
  }


  @Override
  public boolean isIntegral() {
    throw unsupported();
  }


  @Override
  public long longValue() {
    throw unsupported();
  }


  @Override
  public long longValueExact() {
    throw unsupported();
  }


  @Override
  public Number numberValue() {
    throw unsupported();
  }


  public String toString() {
    return "Invalid number: " + failure.getClass() + " : " + failure.getMessage();
  }


  private UnsupportedOperationException unsupported() {
    return new UnsupportedOperationException();
  }


  @Override
  public void writeTo(OutputStream out) {
    throw unsupported();
  }

}
