package io.setl.json.pointer.tree;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import io.setl.json.pointer.PathElement;

/**
 * Filter that allows access to all descendants.
 *
 * @author Simon Greatrix on 17/02/2020.
 */
class FilterAccept implements Filter {

  /** The singleton instance. */
  public static final Filter ACCEPT_ALL = new FilterAccept();


  private FilterAccept() {
    // this is a singleton
  }


  @Override
  public void add(PathElement element) {
    // do nothing
  }


  @Override
  public boolean allowValue() {
    return true;
  }


  @Override
  public boolean containsAll(JsonObject jsonObject) {
    return true;
  }


  @Override
  public boolean containsAll(JsonArray jsonArray) {
    return true;
  }

}
