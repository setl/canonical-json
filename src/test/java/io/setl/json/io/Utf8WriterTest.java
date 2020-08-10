package io.setl.json.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon Greatrix on 06/01/2020.
 */
public class Utf8WriterTest {

  static class MockOutputStream extends ByteArrayOutputStream {

    boolean isClosed = false;

    boolean isFlushed = false;


    @Override
    public void close() throws IOException {
      isClosed = true;
      super.close();
    }


    @Override
    public void flush() throws IOException {
      isFlushed = true;
      super.flush();
    }

  }



  Utf8Appendable appendable;

  MockOutputStream output = new MockOutputStream();

  Utf8Writer writer;


  @Test
  public void close() throws IOException {
    assertFalse(output.isClosed);
    writer.close();
    assertTrue(output.isClosed);
  }


  @Test
  public void flush() throws IOException {
    assertFalse(output.isFlushed);
    writer.flush();
    assertTrue(output.isFlushed);
  }


  @Before
  public void setUp() {
    output = new MockOutputStream();
    writer = new Utf8Writer(output);
    appendable = new Utf8Appendable(output);
  }


  @Test(expected = IOException.class)
  public void testBadSurrogates1() throws IOException {
    char[] pair = Character.toChars(0x14444);
    appendable.append(pair[0]);
    appendable.close();
  }


  @Test(expected = IOException.class)
  public void testBadSurrogates2() throws IOException {
    char[] pair = Character.toChars(0x14444);
    appendable.append(pair[0]);
    appendable.append('A');
    appendable.close();
  }


  @Test(expected = IOException.class)
  public void testBadSurrogates3() throws IOException {
    char[] pair = Character.toChars(0x14444);
    appendable.append(pair[1]);
  }


  @Test(expected = IOException.class)
  public void testBadSurrogates4() throws IOException {
    char[] pair = Character.toChars(0x14444);
    appendable.append(pair[0]);
    appendable.append(pair[0]);
  }


  @Test
  public void testUtf8() throws IOException {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < 10; i++) {
      buf.appendCodePoint(0x40 + i);
      buf.appendCodePoint(0x440 + i);
      buf.appendCodePoint(0xA40 + i);
      buf.appendCodePoint(0x14440 + i);
    }
    char[] chars = buf.toString().toCharArray();
    appendable.append(buf);
    verifyUtf8(chars);
  }


  private void verifyUtf8(char[] expected) throws IOException {
    output.flush();
    ByteBuffer byteBuffer = ByteBuffer.wrap(output.toByteArray());

    Charset utf8 = StandardCharsets.UTF_8;
    CharsetDecoder decoder = utf8.newDecoder();
    decoder.onMalformedInput(CodingErrorAction.REPORT);
    decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

    CharBuffer charBuffer = decoder.decode(byteBuffer);
    assertEquals(new String(expected), charBuffer.toString());
  }


  @Test
  public void writeCharArray() throws IOException {
    String test = "Hello, World!";
    writer.write(test.toCharArray(), 3, 6);
    verifyUtf8("lo, Wo".toCharArray());
  }


  @Test
  public void writeString() throws IOException {
    String test = "Hello, World!";
    writer.write(test, 3, 6);
    verifyUtf8("lo, Wo".toCharArray());
  }

}