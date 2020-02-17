package io.setl.json.pointer.tree;

import javax.json.JsonArray;
import javax.json.JsonObject;

import io.setl.json.pointer.PathElement;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
interface Filter {

  /**
   * Add a new path element to this filter.
   *
   * @param element the element to add
   */
  void add(PathElement element);

  /**
   * Check if all elements of the object are accessible by paths in this filter.
   *
   * @param jsonObject the object to check
   *
   * @return true if everything is accessible
   */
  boolean containsAll(JsonObject jsonObject);

  /**
   * Check if all elements of the array are accessible by paths in this filter.
   *
   * @param jsonArray array to check
   *
   * @return true if everything is accessible
   */
  boolean containsAll(JsonArray jsonArray);

}
