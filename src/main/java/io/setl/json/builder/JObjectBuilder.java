package io.setl.json.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import io.setl.json.JObject;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JObjectBuilder implements JsonObjectBuilder {

  private final JObject object = new JObject();


  @Override
  public JsonObjectBuilder add(String name, JsonValue value) {
    object.put(name, value);
    return this;
  }


  @Override
  public JsonObjectBuilder add(String name, String value) {
    object.put(name, value);
    return this;
  }


  @Override
  public JsonObjectBuilder add(String name, BigInteger value) {
    object.put(name, value);
    return this;
  }


  @Override
  public JsonObjectBuilder add(String name, BigDecimal value) {
    object.put(name, value);
    return this;
  }


  @Override
  public JsonObjectBuilder add(String name, int value) {
    object.put(name, value);
    return this;
  }


  @Override
  public JsonObjectBuilder add(String name, long value) {
    object.put(name, value);
    return this;
  }


  @Override
  public JsonObjectBuilder add(String name, double value) {
    object.put(name, value);
    return this;
  }


  @Override
  public JsonObjectBuilder add(String name, boolean value) {
    object.put(name, value);
    return this;
  }


  @Override
  public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
    object.put(name, builder.build());
    return this;
  }


  @Override
  public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
    object.put(name, builder.build());
    return this;
  }


  @Override
  public JsonObjectBuilder addAll(JsonObjectBuilder builder) {
    object.putAll(builder.build());
    return this;
  }


  @Override
  public JsonObjectBuilder addNull(String name) {
    object.put(name);
    return this;
  }


  @Override
  public JsonObject build() {
    return object;
  }


  @Override
  public JsonObjectBuilder remove(String name) {
    object.remove(name);
    return this;
  }


  @Override
  public String toString() {
    return object.toString();
  }

}
