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

  boolean containsValue(JsonArray target);

  boolean containsValue(JsonObject target);

  PathElement getChild();

  int getIndex();

  String getKey();

  String getEscapedKey();

  String getPath();

  JsonValue getValue(JsonArray target);

  JsonValue getValue(JsonObject target);

  void remove(JsonArray target);

  void remove(JsonObject target);

  void replace(JsonArray target, JsonValue value);

  void replace(JsonObject target, JsonValue value);

  void setChild(PathElement child);

  void setPath(String path);

  void buildPath(StringBuilder builder);
}
