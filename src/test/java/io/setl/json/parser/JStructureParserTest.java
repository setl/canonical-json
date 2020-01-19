package io.setl.json.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.setl.json.JObject;
import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import org.junit.Test;

/**
 * @author Simon Greatrix on 17/01/2020.
 */
public class JStructureParserTest {


  @Test
  public void close() {
    JsonParser parser = new JParserFactory(null).createParser(
        new JObjectBuilder().add("a", "b").build()
    );
    parser.close();
    assertFalse(parser.hasNext());
  }


  @Test
  public void getArray() {
    JsonArray array = new JArrayBuilder()
        .add(new JArrayBuilder().add(1).add(2).add(3))
        .add(true)
        .add(new JArrayBuilder().add(4).add(5).add(6))
        .add(true)
        .build();
    JsonParser parser = new JParserFactory(null).createParser(array);
    assertEquals(Event.START_ARRAY, parser.next());
    assertEquals(Event.START_ARRAY, parser.next());
    JsonArray array2 = parser.getArray();
    assertEquals(3, array2.size());
    assertEquals(1, array2.getInt(0));
    assertEquals(Event.VALUE_TRUE, parser.next());
    assertEquals(Event.START_ARRAY, parser.next());
    array2 = parser.getArray();
    assertEquals(3, array2.size());
    assertEquals(5, array2.getInt(1));
    assertEquals(Event.VALUE_TRUE, parser.next());
  }


  @Test(expected = IllegalStateException.class)
  public void getValueStream() {
    JsonParser parser = new JParserFactory(null).createParser(
        new JObjectBuilder().add("a", "b").build()
    );
    parser.next();
    parser.getValueStream();
  }

  @Test
  public void getValueStream2() {
    JsonParser parser = new JParserFactory(null).createParser(
        new JObjectBuilder().add("a", "b").build()
    );
    Stream<JsonValue> stream = parser.getValueStream();
    List<JsonValue> jvs = stream.collect(Collectors.toList());
    assertEquals(1,jvs.size());
    assertTrue(jvs.get(0) instanceof JObject);
  }


  @Test
  public void testRootArray() {
    JArrayBuilder builder = new JArrayBuilder();
    builder.addNull().add(12).add("wibble").add(
        new JObjectBuilder().add("a", 1).addNull("b").add("c", (String) null)
    ).add(true).add(
        new JArrayBuilder().add(false).add(2).add(
            new JArrayBuilder().build()
        )
    ).add("wobble");
    JsonArray array = builder.build();
    JsonParser parser = new JParserFactory(null).createParser(array);

    StringBuilder buf = new StringBuilder();
    while (parser.hasNext()) {
      buf.append(" ").append(parser.next());
    }
    assertEquals(
        " START_ARRAY"
            + " VALUE_NULL VALUE_NUMBER VALUE_STRING"
            + " START_OBJECT KEY_NAME VALUE_NUMBER KEY_NAME VALUE_NULL KEY_NAME VALUE_NULL END_OBJECT"
            + " VALUE_TRUE"
            + " START_ARRAY VALUE_FALSE VALUE_NUMBER START_ARRAY END_ARRAY END_ARRAY"
            + " VALUE_STRING"
            + " END_ARRAY",
        buf.toString()
    );
  }
}