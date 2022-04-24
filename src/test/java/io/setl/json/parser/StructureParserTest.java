package io.setl.json.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.junit.jupiter.api.Test;

import io.setl.json.CJObject;
import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.io.Location;

/**
 * @author Simon Greatrix on 17/01/2020.
 */
public class StructureParserTest {


  @Test
  public void close() {
    JsonParser parser = new ParserFactory(null).createParser(
        new ObjectBuilder().add("a", "b").build()
    );
    parser.close();
    assertFalse(parser.hasNext());
  }


  @Test
  public void getArray() {
    JsonArray array = new ArrayBuilder()
        .add(new ArrayBuilder().add(1).add(2).add(3))
        .add(true)
        .add(new ArrayBuilder().add(4).add(5).add(6))
        .add(true)
        .build();
    JsonParser parser = new ParserFactory(null).createParser(array);
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


  @Test
  public void getArrayStream1() {
    JsonArray array = new ArrayBuilder()
        .add(new ArrayBuilder().add(1).add(2).add(3))
        .add(true)
        .add(new ArrayBuilder().add(4).add(5).add(6))
        .add(false)
        .build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    assertEquals(Event.START_ARRAY, parser.next());
    assertEquals(Event.START_ARRAY, parser.next());

    StringBuilder buf = new StringBuilder();
    parser.getArrayStream().forEach(o -> buf.append(o.getValueType()).append(':').append(o.toString()).append(" "));
    assertEquals("NUMBER:1 NUMBER:2 NUMBER:3 ", buf.toString());
    assertEquals(Event.VALUE_TRUE, parser.next());
  }


  @Test
  public void getArrayStream2() {
    JsonArray array = new ArrayBuilder()
        .add(new ObjectBuilder().add("a", 1))
        .add(true)
        .build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    assertEquals(Event.START_ARRAY, parser.next());
    assertEquals(Event.START_OBJECT, parser.next());

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> parser.getArrayStream());
    assertEquals("State must be START_ARRAY, not START_OBJECT", e.getMessage());
  }


  @Test
  public void getArrayStream3() {
    JsonArray array = new ArrayBuilder()
        .add(new ArrayBuilder().add(1).add("a").add(false).add(4))
        .add(true)
        .build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    assertEquals(Event.START_ARRAY, parser.next());
    assertEquals(Event.START_ARRAY, parser.next());

    Optional<JsonValue> jv = parser.getArrayStream().filter(j -> j.getValueType() == ValueType.STRING).findFirst();
    assertTrue(jv.isPresent());
    assertEquals("\"a\"", jv.get().toString());
    assertEquals(Event.VALUE_FALSE, parser.next());
    parser.skipArray();
    assertEquals(Event.VALUE_TRUE, parser.next());
  }


  @Test
  public void getBigDecimal1() {
    JsonArray array = new ArrayBuilder().add(4.3).build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    parser.next();
    assertEquals(Event.VALUE_NUMBER, parser.next());
    BigDecimal bd = parser.getBigDecimal();
    assertEquals("4.3", bd.toPlainString());
  }


  @Test
  public void getBigDecimal2() {
    JsonObject object = new ObjectBuilder().add("a", 4.3).build();
    JsonParser parser = new ParserFactory(null).createParser(object);
    parser.next();
    parser.next();
    assertEquals(Event.VALUE_NUMBER, parser.next());
    BigDecimal bd = parser.getBigDecimal();
    assertEquals("4.3", bd.toPlainString());
  }


  @Test
  public void getInt() {
    JsonObject object = new ObjectBuilder().add("a", 4.3).build();
    JsonParser parser = new ParserFactory(null).createParser(object);
    parser.next();
    parser.next();
    assertEquals(Event.VALUE_NUMBER, parser.next());
    int i = parser.getInt();
    assertEquals(4, i);
  }


  @Test
  public void getInt1() {
    JsonArray array = new ArrayBuilder().add(4.3).build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    parser.next();
    assertEquals(Event.VALUE_NUMBER, parser.next());
    int i = parser.getInt();
    assertEquals(4, i);
  }


  @Test
  public void getLocation() {
    JsonArray array = new ArrayBuilder().add(4.3).build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    parser.next();
    assertEquals(Location.UNSET, parser.getLocation());
  }


  @Test
  public void getLong() {
    JsonObject object = new ObjectBuilder().add("a", 4.3).build();
    JsonParser parser = new ParserFactory(null).createParser(object);
    parser.next();
    parser.next();
    assertEquals(Event.VALUE_NUMBER, parser.next());
    long i = parser.getLong();
    assertEquals(4, i);
  }


  @Test
  public void getLong1() {
    JsonArray array = new ArrayBuilder().add(4.3).build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    parser.next();
    assertEquals(Event.VALUE_NUMBER, parser.next());
    long i = parser.getLong();
    assertEquals(4, i);
  }


  @Test
  public void getObject() {
    JsonObject value = new ObjectBuilder().add("b", 4.3).build();
    JsonObject object = new ObjectBuilder().add("a", value).add("c", true).build();
    JsonParser parser = new ParserFactory(null).createParser(object);
    parser.next();
    parser.next();
    assertEquals(Event.START_OBJECT, parser.next());
    JsonObject o = parser.getObject();
    assertEquals(value.toString(), o.toString());
    assertEquals(Event.KEY_NAME, parser.next());
  }


  @Test
  public void getObject1() {
    JsonObject value = new ObjectBuilder().add("b", 4.3).build();
    JsonArray array = new ArrayBuilder().add(value).add(true).build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    parser.next();
    assertEquals(Event.START_OBJECT, parser.next());
    JsonObject o = parser.getObject();
    assertEquals(value.toString(), o.toString());
    assertEquals(Event.VALUE_TRUE, parser.next());
  }


  @Test
  public void getObject2() {
    JsonObject value = new ObjectBuilder().add("b", 4.3).build();
    JsonObject object = new ObjectBuilder().add("a", value).add("c", true).build();
    JsonParser parser = new ParserFactory(null).createParser(object);
    parser.next();
    parser.next();
    assertEquals(Event.START_OBJECT, parser.next());
    assertEquals(Event.KEY_NAME, parser.next());
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> parser.getObject());
    assertEquals("State must be START_OBJECT, not KEY_NAME", e.getMessage());
  }


  @Test
  public void getObjectStream1() {
    JsonArray array = new ArrayBuilder()
        .add(new ObjectBuilder().add("a", 1).add("b", 2).add("c", 3))
        .add(true)
        .build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    assertEquals(Event.START_ARRAY, parser.next());
    assertEquals(Event.START_OBJECT, parser.next());

    StringBuilder buf = new StringBuilder();
    parser.getObjectStream().forEach(o -> buf.append(o.getKey()).append("->").append(o.getValue()).append(" "));
    assertEquals("a->1 b->2 c->3 ", buf.toString());
    assertEquals(Event.VALUE_TRUE, parser.next());
  }


  @Test
  public void getObjectStream2() {
    JsonArray array = new ArrayBuilder()
        .add(new ObjectBuilder().add("a", 1).add("b", "b").add("c", false).add("d", 4))
        .add(true)
        .build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    assertEquals(Event.START_ARRAY, parser.next());
    assertEquals(Event.START_OBJECT, parser.next());

    Optional<Entry<String, JsonValue>> jv = parser.getObjectStream().filter(j -> j.getValue().getValueType() == ValueType.STRING).findFirst();
    assertTrue(jv.isPresent());
    assertEquals("\"b\"", jv.get().getValue().toString());
    assertEquals(Event.KEY_NAME, parser.next());
    assertEquals(Event.VALUE_FALSE, parser.next());
    parser.skipObject();
    assertEquals(Event.VALUE_TRUE, parser.next());
  }


  @Test
  public void getValueStream() {
    JsonObject object = new ObjectBuilder().add("a", "b").build();
    JsonParser parser = new ParserFactory(null).createParser(object);
    parser.next();

    // not in root
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> parser.getValueStream());
    assertEquals("Not in root context", e.getMessage());
  }


  @Test
  public void getValueStream2() {
    JsonParser parser = new ParserFactory(null).createParser(
        new ObjectBuilder().add("a", "b").build()
    );
    Stream<JsonValue> stream = parser.getValueStream();
    List<JsonValue> jvs = stream.collect(Collectors.toList());
    assertEquals(1, jvs.size());
    assertTrue(jvs.get(0) instanceof CJObject);
  }


  @Test
  public void testIntegral() {
    JsonArray array = new ArrayBuilder().add(1).add(2.0).add("3").add(2.3).build();
    JsonParser parser = new ParserFactory(null).createParser(array);
    parser.next();
    parser.next();
    assertTrue(parser.isIntegralNumber());
    parser.next();
    assertTrue(parser.isIntegralNumber());
    parser.next();
    try {
      parser.isIntegralNumber();
      fail();
    } catch (IllegalStateException e) {
      // correct
    }
    parser.next();
    assertFalse(parser.isIntegralNumber());
  }


  @Test
  public void testRootArray() {
    ArrayBuilder builder = new ArrayBuilder();
    builder.addNull().add(12).add("wibble").add(
        new ObjectBuilder().add("a", 1).addNull("b").add("c", (String) null)
    ).add(true).add(
        new ArrayBuilder().add(false).add(2).add(
            new ArrayBuilder().build()
        )
    ).add("wobble");
    JsonArray array = builder.build();
    JsonParser parser = new ParserFactory(null).createParser(array);

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
