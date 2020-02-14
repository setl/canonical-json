package io.setl.json.pointer;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public interface PathElement {

  void add(JsonArray target, JsonValue value);

  void add(JsonObject target, JsonValue value);

  boolean contains(PathElement other);

  boolean containsValue(JsonArray target);

  boolean containsValue(JsonObject target);

  void copy(JsonArray source, JsonArray target);

  void copy(JsonObject source, JsonObject target);

  PathElement getChild();

  int getIndex();

  String getKey();

  JsonValue getValue(JsonArray target);

  JsonValue getValue(JsonObject target);

  boolean isArrayType();

  void remove(JsonArray target);

  void remove(JsonObject target);

  void replace(JsonArray target, JsonValue value);

  void replace(JsonObject target, JsonValue value);

}
