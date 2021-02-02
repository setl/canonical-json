package io.setl.json.primitive;

import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * @author Simon Greatrix on 18/11/2020.
 */
public class CJBooleanTest {

  @Test
  public void test() {
    assertSame(CJBoolean.FALSE, CJFalse.FALSE);
    assertSame(CJBoolean.TRUE, CJTrue.TRUE);
  }

}