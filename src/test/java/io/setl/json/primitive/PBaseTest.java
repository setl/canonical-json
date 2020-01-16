package io.setl.json.primitive;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
public class PBaseTest {

  // shameless coverage tests
  @Test
  public void testEquals() {
    PString string = new PString("Wibble");
    PString same = new PString("Wibble");
    PString other = new PString("Wobble");
    assertTrue(string.equals(string));
    assertFalse(string.equals(null));
    assertFalse(string.equals(other));
    assertTrue(string.equals(same));
  }
}