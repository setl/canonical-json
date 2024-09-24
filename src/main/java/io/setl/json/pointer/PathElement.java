package io.setl.json.pointer;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import io.setl.json.pointer.JsonExtendedPointer.ResultOfAdd;

/**
 * An element of a pointer's path.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public interface PathElement {

  /**
   * Add the value to the target. Note this path element must be an array type.
   *
   * @param target the target (an array)
   * @param value  the value
   */
  void add(JsonArray target, JsonValue value);

  /**
   * Add the value to the target.
   *
   * @param target the target (an object)
   * @param value  the value
   */
  void add(JsonObject target, JsonValue value);

  /**
   * Test if this path element contains another.
   *
   * @param other the other path element
   *
   * @return true if this path element is a parent of the other
   */
  boolean contains(PathElement other);

  /**
   * Test if the target contains the value. This path element must be an array type.
   *
   * @param target the target (an array)
   *
   * @return true if the target contains the value
   */
  boolean containsValue(JsonArray target);

  /**
   * Test if the target contains the value.
   *
   * @param target the target (an object)
   *
   * @return true if the target contains the value
   */
  boolean containsValue(JsonObject target);

  /**
   * Copy the value of this path from the source to the target.
   *
   * @param source the source value
   * @param target the target value
   */
  void copy(JsonArray source, JsonArray target);

  /**
   * Copy the value of this path from the source to the target.
   *
   * @param source the source value
   * @param target the target value
   */
  void copy(JsonObject source, JsonObject target);

  /**
   * Get the child path of this element.
   *
   * @return the child or null
   */
  PathElement getChild();

  /**
   * Get the index in the array.
   *
   * @return the index (-1 for object keys)
   */
  int getIndex();

  /**
   * Get the object key.
   *
   * @return the key
   */
  String getKey();

  /**
   * Get the value at this path element.
   *
   * @param target the target
   *
   * @return the value
   *
   * @throws io.setl.json.exception.NoSuchValueException if the path does not exist
   */
  JsonValue getValue(JsonArray target);

  /**
   * Get the value at this path element.
   *
   * @param target the target
   *
   * @return the value
   *
   * @throws io.setl.json.exception.NoSuchValueException if the path does not exist
   */
  JsonValue getValue(JsonObject target);

  /**
   * Is this path element an array type?.
   *
   * @return true if this is array type.
   */
  boolean isArrayType();

  /**
   * Get the value at this path element.
   *
   * @param target the target
   *
   * @return the value or null if missing
   */
  JsonValue optValue(JsonArray target);

  /**
   * Get the value at this path element.
   *
   * @param target the target
   *
   * @return the value or null if missing
   */
  JsonValue optValue(JsonObject target);

  /**
   * Remove this path from the target structure.
   *
   * @param target the target
   */
  void remove(JsonArray target);

  /**
   * Remove this path from the target structure.
   *
   * @param target the target
   */
  void remove(JsonObject target);

  /**
   * Replace this path in the target structure with the value.
   *
   * @param target the target
   * @param value  the value
   */
  void replace(JsonArray target, JsonValue value);

  /**
   * Replace this path in the target structure with the value.
   *
   * @param target the target
   * @param value  the value
   */
  void replace(JsonObject target, JsonValue value);

  /**
   * Test what an add operation would do to the target.
   *
   * @param target the target
   *
   * @return the result
   */
  ResultOfAdd testAdd(JsonArray target);

  /**
   * Test what an add operation would do to the target.
   *
   * @param target the target
   *
   * @return the result
   */
  ResultOfAdd testAdd(JsonObject target);

}
