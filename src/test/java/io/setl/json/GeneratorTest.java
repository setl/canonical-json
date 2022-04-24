package io.setl.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import io.setl.json.primitive.CJString;
import io.setl.json.primitive.numbers.CJNumber;

public class GeneratorTest {


  @Test
  public void testBigDecimal() {
    assertEquals("1.234E0", CJNumber.cast(new BigDecimal("1.234")).toString());
    assertEquals("-1.234E0", CJNumber.cast(new BigDecimal("-1.234")).toString());
    assertEquals("1.0E-1", CJNumber.cast(new BigDecimal(".1")).toString());
  }


  @Test
  public void testBigInteger() {
    assertEquals("987654321987654321987654321987654321", CJNumber.cast(new BigInteger("987654321987654321987654321987654321")).toString());
  }


  @Test
  public void testByte() {
    assertEquals("42", CJNumber.create(Byte.valueOf((byte) 42)).toString());
    assertEquals("-24", CJNumber.create(Byte.valueOf((byte) -24)).toString());
  }


  @Test
  public void testDouble() {
    assertEquals("340282346638528900000000000000000000000", CJNumber.cast(Double.valueOf(Math.ulp((double) Float.MAX_VALUE) + Float.MAX_VALUE)).toString());
    assertEquals("1.4012984643248174E-45", CJNumber.cast(Double.valueOf(Math.ulp((double) Float.MIN_VALUE) + Float.MIN_VALUE)).toString());
  }


  @Test
  public void testFloat() {
    assertEquals("1.2345E1", CJNumber.cast(12.345f).toString());
    assertEquals("0", CJNumber.cast(0.0f).toString());
    assertEquals("0", CJNumber.cast(-0.0f).toString());
  }


  @Test
  public void testInt() {
    assertEquals("7654321", CJNumber.cast(Integer.valueOf(7654321)).toString());
    assertEquals("-123456789", CJNumber.cast(Integer.valueOf(-123456789)).toString());
  }


  @Test
  public void testLong() {
    assertEquals("987654321987654321", CJNumber.cast(Long.valueOf(987654321987654321L)).toString());
    assertEquals("-123456789123456789", CJNumber.cast(Long.valueOf(-123456789123456789L)).toString());
  }


  @Test
  public void testShort() {
    assertEquals("4321", CJNumber.cast(Short.valueOf((short) 4321)).toString());
    assertEquals("-12345", CJNumber.cast(Short.valueOf((short) -12345)).toString());
  }


  @Test
  @SuppressWarnings({"checkstyle:IllegalTokenText", "checkstyle:AvoidEscapedUnicodeCharacters"})
  public void testString() {
    assertEquals("\"\"", CJString.create("").toString());
    assertEquals("\"abc\"", CJString.create("abc").toString());
    assertEquals("\"\\u0007 \\b \\t \\n \\u000B \\f \\r \\u000E \\\\\\\"\"", CJString.create("\u0007 \u0008 \t \n \u000b \u000c \r \u000e \\\"").toString());

    char[] chars = new char[]{'.', '.', '.', '.'};

    // isolated high surrogate
    chars[1] = (char) 0xd801;
    assertEquals("\".\\uD801..\"", CJString.create(new String(chars)).toString());

    // isolated low surrogate
    chars[1] = (char) 0xdc01;
    assertEquals("\".\\uDC01..\"", CJString.create(new String(chars)).toString());

    // reversed surrogate pair
    chars[1] = (char) 0xdc01;
    chars[2] = (char) 0xd801;
    assertEquals("\".\\uDC01\\uD801.\"", CJString.create(new String(chars)).toString());

    // actual surrogate pair
    chars[1] = (char) 0xd801;
    chars[2] = (char) 0xdc01;
    assertEquals("\".\uD801\uDC01.\"", CJString.create(new String(chars)).toString());

    // all surrogates
    chars[0] = (char) 0xdc01;
    chars[1] = (char) 0xdc02;
    chars[2] = (char) 0xd801;
    chars[3] = (char) 0xd802;
    assertEquals("\"\\uDC01\\uDC02\\uD801\\uD802\"", CJString.create(new String(chars)).toString());

    // surrogates at start and end
    chars[0] = (char) 0xdc01;
    chars[1] = '.';
    chars[2] = '.';
    chars[3] = (char) 0xdc05;
    assertEquals("\"\\uDC01..\\uDC05\"", CJString.create(new String(chars)).toString());

    // surrogates at start and end
    chars[0] = (char) 0xd801;
    chars[1] = '.';
    chars[2] = '.';
    chars[3] = (char) 0xd805;
    assertEquals("\"\\uD801..\\uD805\"", CJString.create(new String(chars)).toString());

  }

}
