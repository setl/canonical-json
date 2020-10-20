package io.setl.json.parser;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.Map;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.json.stream.JsonParsingException;

import org.junit.Test;

/**
 * @author Simon Greatrix on 24/01/2020.
 */
public class JParserFactoryTest {

  @Test
  public void createParser() {
    JsonParser parser = new JParserFactory(null).createParser(new StringReader("[] {}"));
    assertTrue(parser.hasNext());
    assertEquals(Event.START_ARRAY, parser.next());
    assertTrue(parser.hasNext());
    assertEquals(Event.END_ARRAY, parser.next());

    try {
      parser.hasNext();
      fail();
    } catch (JsonParsingException e) {
      // correct
    }
  }


  @Test
  public void getConfigInUse1() {
    JParserFactory factory = new JParserFactory(null);
    Map<String, ?> map = factory.getConfigInUse();
    assertEquals(1, map.size());
    assertEquals(Boolean.TRUE, map.get(JParserFactory.REQUIRE_SINGLE_ROOT));
  }


  @Test
  public void getConfigInUse2() {
    JParserFactory factory = new JParserFactory(Map.of(JParserFactory.REQUIRE_SINGLE_ROOT, "false"));
    Map<String, ?> map = factory.getConfigInUse();
    assertEquals(1, map.size());
    assertEquals(Boolean.FALSE, map.get(JParserFactory.REQUIRE_SINGLE_ROOT));
  }


  @Test
  public void getConfigInUse3() {
    JParserFactory factory = new JParserFactory(Map.of(JParserFactory.REQUIRE_SINGLE_ROOT, false));
    Map<String, ?> map = factory.getConfigInUse();
    assertEquals(1, map.size());
    assertEquals(Boolean.FALSE, map.get(JParserFactory.REQUIRE_SINGLE_ROOT));
  }


  @Test
  public void testCreateParser() {
    JsonParser parser = new JParserFactory(Map.of(JParserFactory.REQUIRE_SINGLE_ROOT, false)).createParser(
        new ByteArrayInputStream("{} []".getBytes(UTF_8)));
    assertEquals(Event.START_OBJECT, parser.next());
    assertEquals(Event.END_OBJECT, parser.next());
    assertEquals(Event.START_ARRAY, parser.next());
    assertEquals(Event.END_ARRAY, parser.next());
  }

}
