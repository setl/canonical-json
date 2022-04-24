package io.setl.json.pointer.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.json.JsonValue;

import org.junit.jupiter.api.Test;

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


  @Test
  public void add2() {
    UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class, () -> FilterDeny.DENY.add(new ObjectTerminal("boo", "boo")));
    assertEquals("The DENY filter is immutable.", e.getMessage());
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