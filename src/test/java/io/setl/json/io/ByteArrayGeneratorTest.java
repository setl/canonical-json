package io.setl.json.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 24/04/2022.
 */
class ByteArrayGeneratorTest {

  @Test
  public void testConstructor1() {
    ByteArrayGenerator g = new ByteArrayGenerator();
    g.startArray().startObject().key("a").value(JsonValue.NULL).end().end().flush();
    g.close();
    assertEquals("[{\"a\":null}]", g.toString());
  }


  @Test
  public void testConstructor2() {
    ByteArrayGenerator g = new ByteArrayGenerator(Map.of(JsonGenerator.PRETTY_PRINTING, true));
    g.startArray().startObject().key("a").value(JsonValue.NULL).end().end().flush();
    g.close();
    assertEquals("[ { \"a\": null } ]", g.toString());
  }


  @Test
  public void testConstructor3() {
    ByteArrayGenerator g = new ByteArrayGenerator((Map<String, ?>) null);
    g.startArray().startObject().key("a").value(JsonValue.NULL).end().end().flush();
    g.close();
    assertEquals("[{\"a\":null}]", g.toString());
  }


  @Test
  public void testConstructor4() {
    GeneratorFactory gf = new GeneratorFactory(Map.of(JsonGenerator.PRETTY_PRINTING, true, GeneratorFactory.SMALL_STRUCTURE_LIMIT, 0));
    ByteArrayGenerator g = new ByteArrayGenerator(gf);
    g.startArray().startObject().key("a").value(JsonValue.NULL).end().end().flush();
    g.close();
    assertEquals("[\n"
        + "  {\n"
        + "    \"a\": null\n"
        + "  }\n"
        + "]", g.toString());
  }


  @Test
  public void testConstructor5() {
    ByteArrayGenerator g = new ByteArrayGenerator((GeneratorFactory) null);
    g.startArray().startObject().key("a").value(JsonValue.NULL).end().end().flush();
    g.close();
    assertEquals("[{\"a\":null}]", g.toString());
  }

}