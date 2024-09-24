package io.setl.json.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import io.setl.json.CJObject;
import io.setl.json.FormattedJson;

/**
 * A builder for JSON Objects.
 *
 * @author Simon Greatrix on 10/01/2020.
 */
public class ObjectBuilder implements JsonObjectBuilder, FormattedJson {

  private final CJObject object = new CJObject();


  /** New instance. */
  public ObjectBuilder() {
    // default constructor
  }


  @Override
  public ObjectBuilder add(String name, JsonValue value) {
    object.put(name, value);
    return this;
  }


  @Override
  public ObjectBuilder add(String name, String value) {
    object.put(name, value);
    return this;
  }


  @Override
  public ObjectBuilder add(String name, BigInteger value) {
    object.put(name, value);
    return this;
  }


  @Override
  public ObjectBuilder add(String name, BigDecimal value) {
    object.put(name, value);
    return this;
  }


  @Override
  public ObjectBuilder add(String name, int value) {
    object.put(name, value);
    return this;
  }


  @Override
  public ObjectBuilder add(String name, long value) {
    object.put(name, value);
    return this;
  }


  @Override
  public ObjectBuilder add(String name, double value) {
    object.put(name, value);
    return this;
  }


  @Override
  public ObjectBuilder add(String name, boolean value) {
    object.put(name, value);
    return this;
  }


  @Override
  public ObjectBuilder add(String name, JsonObjectBuilder builder) {
    object.put(name, builder.build());
    return this;
  }


  @Override
  public ObjectBuilder add(String name, JsonArrayBuilder builder) {
    object.put(name, builder.build());
    return this;
  }


  @Override
  public ObjectBuilder addAll(JsonObjectBuilder builder) {
    object.putAll(builder.build());
    return this;
  }


  @Override
  public ObjectBuilder addNull(String name) {
    object.put(name);
    return this;
  }


  @Override
  public CJObject build() {
    return object;
  }


  @Override
  public ObjectBuilder remove(String name) {
    object.remove(name);
    return this;
  }


  @Override
  public String toCanonicalString() {
    return object.toCanonicalString();
  }


  @Override
  public String toPrettyString() {
    return object.toPrettyString();
  }


  @Override
  public String toString() {
    return object.toString();
  }

}
