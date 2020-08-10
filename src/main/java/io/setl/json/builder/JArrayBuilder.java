package io.setl.json.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import io.setl.json.JArray;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JArrayBuilder implements JsonArrayBuilder {

  /** The array being built. */
  private final JArray array = new JArray();


  @Override
  public JsonArrayBuilder add(JsonValue value) {
    array.add(value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(String value) {
    array.add(value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(BigDecimal value) {
    array.add(value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(BigInteger value) {
    array.add(value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(int value) {
    array.add(value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(long value) {
    array.add(value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(double value) {
    array.add(value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(boolean value) {
    array.add(value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(JsonObjectBuilder builder) {
    array.add(builder.build());
    return this;
  }


  @Override
  public JsonArrayBuilder add(JsonArrayBuilder builder) {
    array.add(builder.build());
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, JsonValue value) {
    array.add(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, String value) {
    array.add(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, BigDecimal value) {
    array.add(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, BigInteger value) {
    array.add(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, int value) {
    array.add(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, long value) {
    array.add(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, double value) {
    array.add(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, boolean value) {
    array.add(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, JsonObjectBuilder builder) {
    array.add(index, builder.build());
    return this;
  }


  @Override
  public JsonArrayBuilder add(int index, JsonArrayBuilder builder) {
    array.add(index, builder.build());
    return this;
  }


  @Override
  public JsonArrayBuilder addAll(JsonArrayBuilder builder) {
    array.addAll(builder.build());
    return this;
  }


  @Override
  public JsonArrayBuilder addNull() {
    array.addNull();
    return this;
  }


  @Override
  public JsonArrayBuilder addNull(int index) {
    array.addNull(index);
    return this;
  }


  @Override
  public JsonArray build() {
    return array;
  }


  @Override
  public JsonArrayBuilder remove(int index) {
    array.remove(index);
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, JsonValue value) {
    array.set(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, String value) {
    array.set(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, BigDecimal value) {
    array.set(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, BigInteger value) {
    array.set(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, int value) {
    array.set(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, long value) {
    array.set(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, double value) {
    array.set(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, boolean value) {
    array.set(index, value);
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, JsonObjectBuilder builder) {
    array.set(index, builder.build());
    return this;
  }


  @Override
  public JsonArrayBuilder set(int index, JsonArrayBuilder builder) {
    array.set(index, builder.build());
    return this;
  }


  @Override
  public JsonArrayBuilder setNull(int index) {
    array.setNull(index);
    return this;
  }


  @Override
  public String toString() {
    return array.toString();
  }

}
