package io.setl.json.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import io.setl.json.Canonical;

/**
 * Factory for creating array and object builders.
 *
 * @author Simon Greatrix on 10/01/2020.
 */
public class BuilderFactory implements JsonBuilderFactory {


  @Override
  public JsonArrayBuilder createArrayBuilder() {
    return new ArrayBuilder();
  }


  @Override
  public JsonArrayBuilder createArrayBuilder(JsonArray array) {
    JsonArrayBuilder builder = new ArrayBuilder();
    array.forEach(o -> builder.add(Canonical.cast(o).copy()));
    return builder;
  }


  @Override
  public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
    JsonArrayBuilder builder = new ArrayBuilder();
    collection.forEach(o -> builder.add(Canonical.create(o).copy()));
    return builder;
  }


  @Override
  public JsonObjectBuilder createObjectBuilder() {
    return new ObjectBuilder();
  }


  @Override
  public JsonObjectBuilder createObjectBuilder(JsonObject object) {
    JsonObjectBuilder builder = new ObjectBuilder();
    object.forEach((k, v) -> builder.add(k, Canonical.cast(v).copy()));
    return builder;
  }


  @Override
  public JsonObjectBuilder createObjectBuilder(Map<String, Object> object) {
    JsonObjectBuilder builder = new ObjectBuilder();
    object.forEach((k, v) -> builder.add(k, Canonical.create(v).copy()));
    return builder;
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    return Collections.emptyMap();
  }

}
