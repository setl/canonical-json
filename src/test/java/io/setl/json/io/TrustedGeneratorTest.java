package io.setl.json.io;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.setl.json.exception.JsonIOException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class TrustedGeneratorTest {

  TrustedGenerator generator;

  StringWriter writer;


  @Test
  public void close() throws IOException {
    Writer writer = Mockito.mock(Writer.class);
    generator = new TrustedGenerator(new NoOpFormatter(writer));
    generator.close();
    Mockito.verify(writer).close();
  }


  @Test(expected = JsonGenerationException.class)
  public void closeInArray() {
    generator.writeStartArray();
    generator.close();
  }


  @Test(expected = JsonGenerationException.class)
  public void closeInObject() {
    generator.writeStartObject();
    generator.close();
  }


  @Test
  public void flush() throws IOException {
    Writer writer = Mockito.mock(Writer.class);
    NoOpFormatter formatter = new NoOpFormatter(writer);
    generator = new TrustedGenerator(formatter);
    generator.writeStartObject();
    generator.write("a", 1);
    generator.flush();
    // In the middle of an object, but we flush because we trust the caller.
    verify(writer, times(1)).flush();
  }


  @Test(expected = JsonGenerationException.class)
  public void keysOutOfOrder() {
    generator.writeStartObject()
        .writeNull("b")
        .writeNull("a");
  }


  @Before
  public void reset() {
    writer = new StringWriter();
    generator = new TrustedGenerator(new NoOpFormatter(writer));
  }


  @Test
  public void sample() {
    // The JavaDoc example
    generator
        .writeStartObject()
        .writeStartObject("address")
        .write("city", "New York")
        .write("postalCode", "10021")
        .write("state", "NY")
        .write("streetAddress", "21 2nd Street")
        .writeEnd()
        .write("age", 25)
        .write("firstName", "John")
        .write("lastName", "Smith")
        .writeStartArray("phoneNumber")
        .writeStartObject()
        .write("number", "212 555-1234")
        .write("type", "home")
        .writeEnd()
        .writeStartObject()
        .write("number", "646 555-4567")
        .write("type", "fax")
        .writeEnd()
        .writeEnd()
        .writeEnd();
    generator.close();
    assertEquals("{\"address\":{\"city\":\"New York\",\"postalCode\":\"10021\",\"state\":\"NY\",\"streetAddress\":\"21 2nd Street\"},"
        + "\"age\":25,\"firstName\":\"John\",\"lastName\":\"Smith\",\"phoneNumber\":[{\"number\":\"212 555-1234\",\"type\":\"home\"},"
        + "{\"number\":\"646 555-4567\",\"type\":\"fax\"}]}", writer.toString());
  }


  @Test
  public void testWriteArray1() {
    generator.writeStartArray()
        .write(1)
        .write(2)
        .write(3)
        .writeEnd()
        .close();
    assertEquals("[1,2,3]", writer.toString());
  }


  @Test
  public void testWriteArray2() {
    generator.writeStartArray()
        .writeStartObject()
        .writeEnd()
        .write(2)
        .write(3)
        .writeEnd()
        .close();
    assertEquals("[{},2,3]", writer.toString());
  }


  @Test
  public void testWriteArray3() {
    generator.writeStartArray()
        .write(1)
        .writeStartObject()
        .writeEnd()
        .write(3)
        .writeEnd()
        .close();
    assertEquals("[1,{},3]", writer.toString());
  }


  @Test
  public void testWriteObject1() {
    generator.writeStartObject()
        .write("a", 1)
        .write("b", 2)
        .write("c", 3)
        .writeEnd()
        .close();
    assertEquals("{\"a\":1,\"b\":2,\"c\":3}", writer.toString());
  }


  @Test
  public void testWriteObject2() {
    generator.writeStartObject()
        .writeStartObject("a")
        .writeEnd()
        .write("b", 2)
        .write("c", 3)
        .writeEnd()
        .close();
    assertEquals("{\"a\":{},\"b\":2,\"c\":3}", writer.toString());
  }


  @Test
  public void testWriteObject3() {
    generator.writeStartObject()
        .write("a", 1)
        .writeStartObject("b")
        .writeEnd()
        .write("c", 3)
        .writeEnd()
        .close();
    assertEquals("{\"a\":1,\"b\":{},\"c\":3}", writer.toString());
  }


  @Test
  public void testWriteObject4() {
    generator.writeStartObject()
        .write("a", 1)
        .writeStartArray("b")
        .writeEnd()
        .write("c", 3)
        .writeEnd()
        .close();
    assertEquals("{\"a\":1,\"b\":[],\"c\":3}", writer.toString());
  }


  @Test
  public void writeBigDec() {
    BigDecimal bd = new BigDecimal("12.34");
    generator.write(bd);
    generator.close();
    assertEquals("1.234E1", writer.toString());

    reset();
    generator.writeStartArray();
    generator.write(BigDecimal.TEN);
    generator.writeEnd();
    generator.close();
    assertEquals("[10]", writer.toString());

    reset();
    generator.writeStartObject();
    generator.writeKey("a");
    generator.write(BigDecimal.TEN);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":10}", writer.toString());
  }


  @Test
  public void writeBigInt() {
    generator.write(BigInteger.TEN);
    generator.close();
    assertEquals("10", writer.toString());

    reset();
    generator.writeStartArray();
    generator.write(BigInteger.TEN);
    generator.writeEnd();
    generator.close();
    assertEquals("[10]", writer.toString());

    reset();
    generator.writeStartObject();
    generator.writeKey("a");
    generator.write(BigInteger.TEN);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":10}", writer.toString());
  }


  @Test
  public void writeBoolean() {
    generator.write(true);
    generator.close();
    assertEquals("true", writer.toString());

    reset();
    generator.writeStartArray();
    generator.write(true);
    generator.writeEnd();
    generator.close();
    assertEquals("[true]", writer.toString());

    reset();
    generator.writeStartObject();
    generator.writeKey("a");
    generator.write(false);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":false}", writer.toString());
  }


  @Test
  public void writeDouble() {
    generator.write(12.34d);
    generator.close();
    assertEquals("1.234E1", writer.toString());

    reset();
    generator.writeStartArray();
    generator.write(4.321d);
    generator.writeEnd();
    generator.close();
    assertEquals("[4.321E0]", writer.toString());

    reset();
    generator.writeStartObject();
    generator.writeKey("a");
    generator.write(0.4312d);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":4.312E-1}", writer.toString());
  }


  @Test(expected = JsonGenerationException.class)
  public void writeEnd() {
    generator.writeEnd();
  }


  @Test(expected = JsonIOException.class)
  public void writeIOFailure() throws IOException {
    Writer writer = Mockito.mock(Writer.class);
    Mockito.doThrow(new IOException()).when(writer).append(anyChar());
    generator = new TrustedGenerator(new NoOpFormatter(writer));
    generator.write("fail");
  }


  @Test(expected = JsonIOException.class)
  public void writeIOFailure2() throws IOException {
    Writer writer = Mockito.mock(Writer.class);
    Mockito.doThrow(new IOException()).when(writer).close();
    generator = new TrustedGenerator(new NoOpFormatter(writer));
    generator.close();
  }


  @Test(expected = JsonIOException.class)
  public void writeIOFailure3() throws IOException {
    Writer writer = Mockito.mock(Writer.class);
    Mockito.doThrow(new IOException()).when(writer).flush();
    generator = new TrustedGenerator(new NoOpFormatter(writer));
    generator.flush();
  }


  @Test
  public void writeInt() {
    generator.write(12);
    generator.close();
    assertEquals("12", writer.toString());

    reset();
    generator.writeStartArray();
    generator.write(43);
    generator.writeEnd();
    generator.close();
    assertEquals("[43]", writer.toString());

    reset();
    generator.writeStartObject();
    generator.writeKey("a");
    generator.write(56);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":56}", writer.toString());
  }


  @Test(expected = JsonGenerationException.class)
  public void writeKey() {
    generator.writeKey("fail");
  }


  @Test(expected = JsonGenerationException.class)
  public void writeKeyInArray() {
    generator.writeStartArray();
    generator.writeKey("a");
  }


  @Test
  public void writeLong() {
    generator.write(100000000012L);
    generator.close();
    assertEquals("100000000012", writer.toString());

    reset();
    generator.writeStartArray();
    generator.write(100000000043L);
    generator.writeEnd();
    generator.close();
    assertEquals("[100000000043]", writer.toString());

    reset();
    generator.writeStartObject();
    generator.writeKey("a");
    generator.write(100000000056L);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":100000000056}", writer.toString());
  }


  @Test
  public void writeNull() {
    generator.writeNull();
    generator.close();
    assertEquals("null", writer.toString());

    reset();
    generator.writeStartArray();
    generator.writeNull();
    generator.writeEnd();
    generator.close();
    assertEquals("[null]", writer.toString());

    reset();
    generator.writeStartObject();
    generator.writeKey("a");
    generator.writeNull();
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":null}", writer.toString());
  }


  @Test
  public void writeNullString() {
    generator.writeStartObject();
    generator.writeNull("a");
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":null}", writer.toString());
  }


  @Test
  public void writeStartArray() {
    generator.writeStartArray();
    generator.writeEnd();
    generator.close();
    assertEquals("[]", writer.toString());

    reset();
    generator.writeStartArray();
    generator.writeStartArray();
    generator.writeEnd();
    generator.writeEnd();
    generator.close();
    assertEquals("[[]]", writer.toString());

    reset();
    generator.writeStartObject();
    generator.writeKey("a");
    generator.writeStartArray();
    generator.writeEnd();
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":[]}", writer.toString());
  }


  @Test
  public void writeStartArrayString() {
    generator.writeStartObject();
    generator.writeStartArray("a");
    generator.writeEnd();
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":[]}", writer.toString());
  }


  @Test
  public void writeStartObjectString() {
    generator.writeStartObject();
    generator.writeStartObject("a");
    generator.writeEnd();
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":{}}", writer.toString());
  }


  @Test
  public void writeString() {
    generator.write("xyz");
    generator.close();
    assertEquals("\"xyz\"", writer.toString());

    reset();
    generator.writeStartArray();
    generator.write("xyz");
    generator.writeEnd();
    generator.close();
    assertEquals("[\"xyz\"]", writer.toString());

    reset();
    generator.writeStartObject();
    generator.writeKey("a");
    generator.write("xyz");
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":\"xyz\"}", writer.toString());
  }


  @Test
  public void writeStringBigDec() {
    generator.writeStartObject();
    generator.write("a", BigDecimal.TEN);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":10}", writer.toString());
  }


  @Test
  public void writeStringBigInt() {
    generator.writeStartObject();
    generator.write("a", BigInteger.TWO);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":2}", writer.toString());
  }


  @Test
  public void writeStringBoolean() {
    generator.writeStartObject();
    generator.write("a", false);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":false}", writer.toString());
  }


  @Test
  public void writeStringDouble() {
    generator.writeStartObject();
    generator.write("a", 9.8);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":9.8E0}", writer.toString());
  }


  @Test
  public void writeStringInt() {
    generator.writeStartObject();
    generator.write("a", 765);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":765}", writer.toString());

  }


  @Test
  public void writeStringLong() {
    generator.writeStartObject();
    generator.write("a", 1000000000765L);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":1000000000765}", writer.toString());

  }


  @Test
  public void writeStringString() {
    generator.writeStartObject();
    generator.write("a", "wibble");
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":\"wibble\"}", writer.toString());
  }


  @Test
  public void writeStringValue() {
    generator.writeStartObject();
    generator.write("a", JsonValue.EMPTY_JSON_ARRAY);
    generator.writeEnd();
    generator.close();
    assertEquals("{\"a\":[]}", writer.toString());
  }


  @Test(expected = JsonGenerationException.class)
  public void writeTwice() {
    generator.write("fail");
    generator.write("fail");
  }


  @Test(expected = JsonGenerationException.class)
  public void writeTwoInRoot() {
    generator.write(1).write(2);
  }


  @Test(expected = JsonGenerationException.class)
  public void writeTwoInRoot2() {
    generator.writeStartObject().writeEnd().write(2);
  }


  @Test(expected = JsonGenerationException.class)
  public void writeTwoInRoot3() {
    generator.writeStartArray().writeEnd().write(2);
  }


  @Test(expected = JsonGenerationException.class)
  public void writeTwoInRoot4() {
    generator.write(1).writeStartObject();
  }


  @Test(expected = JsonGenerationException.class)
  public void writeTwoInRoot5() {
    generator.write(1).writeStartArray();
  }


  @Test(expected = JsonGenerationException.class)
  public void writeTwoKeysInObject() {
    generator.writeStartObject();
    generator.writeKey("a");
    generator.writeKey("b");
  }


  @Test
  public void writeValue() {
    generator.write(JsonValue.EMPTY_JSON_ARRAY);
    generator.close();
    assertEquals("[]", writer.toString());
  }


  @Test(expected = JsonGenerationException.class)
  public void writeValueWithoutKey() {
    generator.writeStartObject();
    generator.write(true);
  }

}
