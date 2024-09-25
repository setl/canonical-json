package io.setl.json.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringWriter;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.Test;

import io.setl.json.primitive.CJTrue;

/**
 * @author Simon Greatrix on 21/11/2020.
 */
public class PrettyFormatterTest {

  StringWriter writer = new StringWriter();

  TrustedGenerator generator = new TrustedGenerator(new PrettyFormatter(writer, 15));


  @Test
  public void flush() {
    generator.writeStartArray()
        .writeStartArray()
        .writeStartArray()
        .writeStartArray()
        .writeStartArray()
        .writeStartArray()
        .writeEnd()
        .flush();
    generator.writeEnd()
        .writeEnd()
        .writeEnd()
        .writeEnd()
        .writeEnd()
        .close();
    assertEquals("[\n"
        + "  [\n"
        + "    [\n"
        + "      [\n"
        + "        [\n"
        + "          []\n"
        + "        ]\n"
        + "      ]\n"
        + "    ]\n"
        + "  ]\n"
        + "]", writer.toString());
  }


  @Test
  public void writeEmptyArray() {
    generator.write(JsonValue.EMPTY_JSON_ARRAY);
    generator.close();
    assertEquals("[]", writer.toString());
  }


  @Test
  public void writeEmptyObject() {
    generator.write(JsonValue.EMPTY_JSON_OBJECT);
    generator.close();
    assertEquals("{}", writer.toString());
  }


  @Test
  public void writeLiteral() {
    generator.write(CJTrue.TRUE);
    generator.close();
    assertEquals("true", writer.toString());
  }


  @Test
  public void writeSmallArray1() {
    generator.writeStartArray().write(true).writeEnd().close();
    assertEquals("[ true ]", writer.toString());
  }


  @Test
  public void writeSmallArray2() {
    generator.writeStartArray().write(true).writeEnd().close();
    assertEquals("[ true ]", writer.toString());
  }


  @Test
  public void writeSmallArray3() {
    generator.writeStartArray().write(1).write("a").writeStartObject().writeEnd().writeEnd().close();
    assertEquals("[ 1, \"a\", {} ]", writer.toString());
  }


  @Test
  public void writeSmallArray4() {
    generator.writeStartArray().write(1).write("a").writeStartArray().write(2).write(20).write(200).writeEnd().writeEnd().close();
    assertEquals("[\n"
        + "  1,\n"
        + "  \"a\",\n"
        + "  [ 2, 20, 200 ]\n"
        + "]", writer.toString());
  }


  @Test
  public void writeSmallArray5() {
    generator.writeStartArray()
        .writeStartArray()
        .writeStartArray()
        .writeStartArray()
        .writeStartArray()
        .writeStartArray()
        .writeEnd()
        .writeEnd()
        .writeEnd()
        .writeEnd()
        .writeEnd()
        .writeEnd()
        .close();
    assertEquals("[\n"
        + "  [\n"
        + "    [ [ [ [] ] ] ]\n"
        + "  ]\n"
        + "]", writer.toString());
  }


  @Test
  public void writeSmallObject1() {
    generator.writeStartObject().writeStartObject("a").write("b", 1).writeEnd().writeStartArray("c").write(2).writeEnd().writeEnd().close();
    assertEquals("{\n"
        + "  \"a\": { \"b\": 1 },\n"
        + "  \"c\": [ 2 ]\n"
        + "}", writer.toString());
  }


  @Test
  public void writeWithOverflow() {
    // the second number overflows the buffer
    generator.writeStartArray().write(12345).write(1234567890).writeEnd().close();
    assertEquals("[\n"
        + "  12345,\n"
        + "  1234567890\n"
        + "]", writer.toString());
  }


  @Test
  public void writeWithOverflow2() {
    // the comma overflows the buffer
    generator.writeStartArray().write(12345678901L).write(1).writeEnd().close();
    assertEquals("[\n"
        + "  12345678901,\n"
        + "  1\n"
        + "]", writer.toString());
  }


  @Test
  public void writeWithOverflow3() {
    // the comma overflows the buffer
    generator.writeStartArray()
        .write(1)
        .writeStartArray()
        .write(1)
        .writeStartArray()
        .write(1)
        .write(12345678901L)
        .write(1)
        .writeEnd()
        .writeEnd()
        .writeEnd()
        .close();
    assertEquals("[\n"
        + "  1,\n"
        + "  [\n"
        + "    1,\n"
        + "    [\n"
        + "      1,\n"
        + "      12345678901,\n"
        + "      1\n"
        + "    ]\n"
        + "  ]\n"
        + "]", writer.toString());
  }

}
