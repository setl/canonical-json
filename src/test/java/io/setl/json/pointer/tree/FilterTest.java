package io.setl.json.pointer.tree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.JsonValue;

import org.junit.Test;

import io.setl.json.pointer.ObjectTerminal;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class FilterTest {

  @Test
  public void add1() {
    FilterAccept.ACCEPT_ALL.add(new ObjectTerminal("boo", "boo"));
    assertTrue(FilterAccept.ACCEPT_ALL.containsAll(JsonValue.EMPTY_JSON_OBJECT));
  }


  @Test(expected = UnsupportedOperationException.class)
  public void add2() {
    FilterDeny.DENY.add(new ObjectTerminal("boo", "boo"));
  }


  @Test
  public void allowValue() {
    assertTrue(FilterAccept.ACCEPT_ALL.allowValue());
    assertFalse(FilterDeny.DENY.allowValue());
  }


  @Test
  public void containsValue() {
    assertTrue(FilterAccept.ACCEPT_ALL.containsAll(JsonValue.EMPTY_JSON_ARRAY));
    assertTrue(FilterAccept.ACCEPT_ALL.containsAll(JsonValue.EMPTY_JSON_OBJECT));
    assertFalse(FilterDeny.DENY.containsAll(JsonValue.EMPTY_JSON_ARRAY));
    assertFalse(FilterDeny.DENY.containsAll(JsonValue.EMPTY_JSON_OBJECT));
  }

}