package io.setl.json;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

public class GeneratorTest {

  @Before
  public void setUp() throws Exception {}


  @Test
  public void testByte() {
    assertEquals("42", Canonical.format(Byte.valueOf((byte) 42)));
    assertEquals("-24", Canonical.format(Byte.valueOf((byte) -24)));
  }


  @Test
  public void testShort() {
    assertEquals("4321", Canonical.format(Short.valueOf((short) 4321)));
    assertEquals("-12345", Canonical.format(Short.valueOf((short) -12345)));
  }


  @Test
  public void testInt() {
    assertEquals("7654321", Canonical.format(Integer.valueOf(7654321)));
    assertEquals("-123456789", Canonical.format(Integer.valueOf(-123456789)));
  }


  @Test
  public void testLong() {
    assertEquals("987654321987654321", Canonical.format(Long.valueOf(987654321987654321L)));
    assertEquals("-123456789123456789", Canonical.format(Long.valueOf(-123456789123456789L)));
  }


  @Test
  public void testBigInteger() {
    assertEquals("987654321987654321987654321987654321", Canonical.format(new BigInteger("987654321987654321987654321987654321")));
  }
  

  @Test
  public void testFloat() {
    assertEquals("1.2345E1", Canonical.format(12.345f));
    assertEquals("0",Canonical.format(0.0f));
    assertEquals("0",Canonical.format(-0.0f));
  }

  @Test
  public void testDouble() {
    assertEquals("340282346638528900000000000000000000000", Canonical.format(Double.valueOf(Math.ulp((double) Float.MAX_VALUE) + Float.MAX_VALUE)));
    assertEquals("1.4012984643248174E-45", Canonical.format(Double.valueOf(Math.ulp((double) Float.MIN_VALUE) + Float.MIN_VALUE)));
  }
  
  @Test
  public void testBigDecimal() {
    assertEquals("1.234E0", Canonical.format(new BigDecimal("1.234")));
    assertEquals("-1.234E0", Canonical.format(new BigDecimal("-1.234")));
    assertEquals("1.0E-1", Canonical.format(new BigDecimal(".1")));
  }
  
  @Test
  public void testString() {
    assertEquals("\"\"", Canonical.format(""));
    assertEquals("\"abc\"", Canonical.format("abc"));
    assertEquals("\"\\u0007 \\b \\t \\n \\u000B \\f \\r \\u000E \\\\\\\"\"", Canonical.format("\u0007 \u0008 \t \n \u000b \u000c \r \u000e \\\""));
    
    char[] chars = new char[] { '.', '.', '.', '.' };
    
    // isolated high surrogate
    chars[1] = (char) 0xd801;
    assertEquals("\".\\uD801..\"",Canonical.format(new String(chars)));

    // isolated low surrogate
    chars[1] = (char) 0xdc01;
    assertEquals("\".\\uDC01..\"",Canonical.format(new String(chars)));

    // reversed surrogate pair
    chars[1] = (char) 0xdc01;
    chars[2] = (char) 0xd801;
    assertEquals("\".\\uDC01\\uD801.\"",Canonical.format(new String(chars)));

    // actual surrogate pair
    chars[1] = (char) 0xd801;
    chars[2] = (char) 0xdc01;
    assertEquals("\".\uD801\uDC01.\"",Canonical.format(new String(chars)));
    
    // all surrogates
    chars[0] = (char) 0xdc01;
    chars[1] = (char) 0xdc02;
    chars[2] = (char) 0xd801;
    chars[3] = (char) 0xd802;
    assertEquals("\"\\uDC01\\uDC02\\uD801\\uD802\"",Canonical.format(new String(chars)));
    
    // surrogates at start and end
    chars[0] = (char) 0xdc01;
    chars[1] = '.';
    chars[2] = '.';
    chars[3] = (char) 0xdc05;
    assertEquals("\"\\uDC01..\\uDC05\"",Canonical.format(new String(chars)));
    
    // surrogates at start and end
    chars[0] = (char) 0xd801;
    chars[1] = '.';
    chars[2] = '.';
    chars[3] = (char) 0xd805;
    assertEquals("\"\\uD801..\\uD805\"",Canonical.format(new String(chars)));

  }
}
