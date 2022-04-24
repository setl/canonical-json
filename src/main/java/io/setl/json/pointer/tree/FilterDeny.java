package io.setl.json.pointer.tree;

import javax.json.JsonArray;
import javax.json.JsonObject;

import io.setl.json.pointer.PathElement;

/**
 * Filter that denies access to all descendants.
 *
 * @author Simon Greatrix on 17/02/2020.
 */
public class FilterDeny implements Filter {

  public static final Filter DENY = new FilterDeny();


  private FilterDeny() {
    // this is a singleton
  }


  @Override
  public void add(PathElement element) {
    throw new UnsupportedOperationException("The DENY filter is immutable.");
  }


  @Override
  public boolean allowValue() {
    return false;
  }


  @Override
  public boolean containsAll(JsonObject jsonObject) {
    return false;
  }


  @Override
  public boolean containsAll(JsonArray jsonArray) {
    return false;
  }

}
