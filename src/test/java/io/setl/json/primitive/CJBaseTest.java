package io.setl.json.primitive;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
public class CJBaseTest {

  // shameless coverage tests
  @SuppressWarnings("java:S5785") // Allow use of equals in tests, as we are testing equals.
  @Test
  public void testEquals() {
    CJString string = CJString.create("Wibble");
    CJString same = CJString.create("Wibble");
    CJString other = CJString.create("Wobble");
    assertTrue(string.equals(string));
    assertFalse(string.equals(null));
    assertFalse(string.equals(other));
    assertTrue(string.equals(same));
  }
}