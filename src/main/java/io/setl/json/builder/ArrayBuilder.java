package io.setl.json.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import io.setl.json.CJArray;
import io.setl.json.FormattedJson;

/**
 * Builder for JSON arrays.
 *
 * @author Simon Greatrix on 10/01/2020.
 */
public class ArrayBuilder implements JsonArrayBuilder, FormattedJson {

  /** The array being built. */
  private final CJArray array = new CJArray();


  /** New instance. */
  public ArrayBuilder() {
    // nothing to do
  }


  @Override
  public ArrayBuilder add(JsonValue value) {
    array.add(value);
    return this;
  }


  @Override
  public ArrayBuilder add(String value) {
    array.add(value);
    return this;
  }


  @Override
  public ArrayBuilder add(BigDecimal value) {
    array.add(value);
    return this;
  }


  @Override
  public ArrayBuilder add(BigInteger value) {
    array.add(value);
    return this;
  }


  @Override
  public ArrayBuilder add(int value) {
    array.add(value);
    return this;
  }


  @Override
  public ArrayBuilder add(long value) {
    array.add(value);
    return this;
  }


  @Override
  public ArrayBuilder add(double value) {
    array.add(value);
    return this;
  }


  @Override
  public ArrayBuilder add(boolean value) {
    array.add(value);
    return this;
  }


  @Override
  public ArrayBuilder add(JsonObjectBuilder builder) {
    array.add(builder.build());
    return this;
  }


  @Override
  public ArrayBuilder add(JsonArrayBuilder builder) {
    array.add(builder.build());
    return this;
  }


  @Override
  public ArrayBuilder add(int index, JsonValue value) {
    array.add(index, value);
    return this;
  }


  @Override
  public ArrayBuilder add(int index, String value) {
    array.add(index, value);
    return this;
  }


  @Override
  public ArrayBuilder add(int index, BigDecimal value) {
    array.add(index, value);
    return this;
  }


  @Override
  public ArrayBuilder add(int index, BigInteger value) {
    array.add(index, value);
    return this;
  }


  @Override
  public ArrayBuilder add(int index, int value) {
    array.add(index, value);
    return this;
  }


  @Override
  public ArrayBuilder add(int index, long value) {
    array.add(index, value);
    return this;
  }


  @Override
  public ArrayBuilder add(int index, double value) {
    array.add(index, value);
    return this;
  }


  @Override
  public ArrayBuilder add(int index, boolean value) {
    array.add(index, value);
    return this;
  }


  @Override
  public ArrayBuilder add(int index, JsonObjectBuilder builder) {
    array.add(index, builder.build());
    return this;
  }


  @Override
  public ArrayBuilder add(int index, JsonArrayBuilder builder) {
    array.add(index, builder.build());
    return this;
  }


  @Override
  public ArrayBuilder addAll(JsonArrayBuilder builder) {
    array.addAll(builder.build());
    return this;
  }


  @Override
  public ArrayBuilder addNull() {
    array.addNull();
    return this;
  }


  @Override
  public ArrayBuilder addNull(int index) {
    array.addNull(index);
    return this;
  }


  @Override
  public CJArray build() {
    return array;
  }


  @Override
  public ArrayBuilder remove(int index) {
    array.remove(index);
    return this;
  }


  @Override
  public ArrayBuilder set(int index, JsonValue value) {
    array.set(index, value);
    return this;
  }


  @Override
  public ArrayBuilder set(int index, String value) {
    array.set(index, value);
    return this;
  }


  @Override
  public ArrayBuilder set(int index, BigDecimal value) {
    array.set(index, value);
    return this;
  }


  @Override
  public ArrayBuilder set(int index, BigInteger value) {
    array.set(index, value);
    return this;
  }


  @Override
  public ArrayBuilder set(int index, int value) {
    array.set(index, value);
    return this;
  }


  @Override
  public ArrayBuilder set(int index, long value) {
    array.set(index, value);
    return this;
  }


  @Override
  public ArrayBuilder set(int index, double value) {
    array.set(index, value);
    return this;
  }


  @Override
  public ArrayBuilder set(int index, boolean value) {
    array.set(index, value);
    return this;
  }


  @Override
  public ArrayBuilder set(int index, JsonObjectBuilder builder) {
    array.set(index, builder.build());
    return this;
  }


  @Override
  public ArrayBuilder set(int index, JsonArrayBuilder builder) {
    array.set(index, builder.build());
    return this;
  }


  @Override
  public ArrayBuilder setNull(int index) {
    array.setNull(index);
    return this;
  }


  @Override
  public String toCanonicalString() {
    return array.toCanonicalString();
  }


  @Override
  public String toPrettyString() {
    return array.toPrettyString();
  }


  @Override
  public String toString() {
    return array.toString();
  }

}
