package io.setl.json.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import io.setl.json.Primitive;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JBuilderFactory implements JsonBuilderFactory {


  @Override
  public JsonArrayBuilder createArrayBuilder() {
    return new JArrayBuilder();
  }


  @Override
  public JsonArrayBuilder createArrayBuilder(JsonArray array) {
    JsonArrayBuilder builder = new JArrayBuilder();
    array.forEach(o -> builder.add(Primitive.cast(o).copy()));
    return builder;
  }


  @Override
  public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
    JsonArrayBuilder builder = new JArrayBuilder();
    collection.forEach(o -> builder.add(Primitive.create(o).copy()));
    return builder;
  }


  @Override
  public JsonObjectBuilder createObjectBuilder() {
    return new JObjectBuilder();
  }


  @Override
  public JsonObjectBuilder createObjectBuilder(JsonObject object) {
    JsonObjectBuilder builder = new JObjectBuilder();
    object.forEach((k, v) -> builder.add(k, Primitive.cast(v).copy()));
    return builder;
  }


  @Override
  public JsonObjectBuilder createObjectBuilder(Map<String, Object> object) {
    JsonObjectBuilder builder = new JObjectBuilder();
    object.forEach((k, v) -> builder.add(k, Primitive.create(v).copy()));
    return builder;
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    return Collections.emptyMap();
  }

}
