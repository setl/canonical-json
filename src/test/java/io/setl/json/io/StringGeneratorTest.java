package io.setl.json.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 24/04/2022.
 */
class StringGeneratorTest {

  @Test
  public void testConstructor1() {
    StringGenerator g = new StringGenerator();
    g.startArray().startObject().key("a").value(JsonValue.NULL).end().end().flush();
    g.close();
    assertEquals("[{\"a\":null}]", g.toString());
  }


  @Test
  public void testConstructor2() {
    StringGenerator g = new StringGenerator(Map.of(JsonGenerator.PRETTY_PRINTING, true));
    g.startArray().startObject().key("a").value(JsonValue.NULL).end().end().flush();
    g.close();
    assertEquals("[ { \"a\": null } ]", g.toString());
  }


  @Test
  public void testConstructor3() {
    StringGenerator g = new StringGenerator((Map<String, ?>) null);
    g.startArray().startObject().key("a").value(JsonValue.NULL).end().end().flush();
    g.close();
    assertEquals("[{\"a\":null}]", g.toString());
  }


  @Test
  public void testConstructor4() {
    GeneratorFactory gf = new GeneratorFactory(Map.of(JsonGenerator.PRETTY_PRINTING, true, GeneratorFactory.SMALL_STRUCTURE_LIMIT, 0));
    StringGenerator g = new StringGenerator(gf);
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
    StringGenerator g = new StringGenerator((GeneratorFactory) null);
    g.startArray().startObject().key("a").value(JsonValue.NULL).end().end().flush();
    g.close();
    assertEquals("[{\"a\":null}]", g.toString());
  }

}