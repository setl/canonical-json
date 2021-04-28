package io.setl.json.io;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import io.setl.json.Canonical;
import io.setl.json.primitive.CJNull;

/**
 * A JSON generator implementation.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public abstract class Generator implements JsonGenerator {

  @Override
  public Generator write(JsonValue value) {
    return write(Canonical.cast(value));
  }


  public abstract Generator write(Canonical value);


  @Override
  public Generator write(String name, String value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  public Generator write(String name, BigInteger value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  public Generator write(String name, BigDecimal value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  public Generator write(String name, int value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  public Generator write(String name, long value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  public Generator write(String name, double value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  public Generator write(String name, boolean value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  public Generator write(String value) {
    return write(Canonical.create(value));
  }


  @Override
  public Generator write(BigDecimal value) {
    return write(Canonical.create(value));
  }


  @Override
  public Generator write(BigInteger value) {
    return write(Canonical.create(value));
  }


  @Override
  public Generator write(int value) {
    return write(Canonical.create(value));
  }


  @Override
  public Generator write(long value) {
    return write(Canonical.create(value));
  }


  @Override
  public Generator write(double value) {
    return write(Canonical.create(value));
  }


  @Override
  public Generator write(String name, JsonValue value) {
    return writeKey(name).write(value);
  }


  public Generator write(String name, Canonical value) {
    return writeKey(name).write(value);
  }


  @Override
  public Generator write(boolean value) {
    return write(Canonical.create(value));
  }


  @Override
  public abstract Generator writeEnd();


  @Override
  public abstract Generator writeKey(String name);


  @Override
  public Generator writeNull(String name) {
    return writeKey(name).write(CJNull.NULL);
  }


  @Override
  public Generator writeNull() {
    return write(CJNull.NULL);
  }


  @Override
  public abstract Generator writeStartArray();


  @Override
  public Generator writeStartArray(String name) {
    return writeKey(name).writeStartArray();
  }


  @Override
  public abstract Generator writeStartObject();


  @Override
  public Generator writeStartObject(String name) {
    return writeKey(name).writeStartObject();
  }

}
