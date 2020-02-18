package io.setl.json.pointer.tree;

import javax.json.JsonArray;
import javax.json.JsonObject;

import io.setl.json.pointer.PathElement;

/**
 * Filter that allows access to all descendants.
 *
 * @author Simon Greatrix on 17/02/2020.
 */
public class FilterAccept implements Filter {

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
