package io.setl.json.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Stream;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser.Event;

import org.junit.jupiter.api.Test;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.primitive.CJTrue;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
public class ParserTest {

  private Parser create(String data) {
    return (Parser) new ParserFactory(null).createParser(new StringReader(data));
  }


  @Test
  public void testBadState() {
    Parser parser = create("[[12");
    parser.next();
    IllegalStateException e = assertThrows(IllegalStateException.class, () ->parser.getLong());
    assertEquals("State must be VALUE_NUMBER, not START_ARRAY",e.getMessage());
  }


  @Test
  public void testGetArray() {
    Parser parser = create("[0,1,2,3]");
    parser.next();
    CJArray array = parser.getArray();
    assertEquals(4, array.size());
    for (int i = 0; i < 4; i++) {
      assertEquals(i, array.getInt(i));
    }
  }


  @Test
  public void testGetArrayStream() {
    Parser parser = create("[0,\"\",null,3]");
    parser.next();
    Stream<JsonValue> stream = parser.getArrayStream();
    assertEquals(4, stream.count());
  }


  @Test
  public void testGetBigDecimal() {
    Parser parser = create("123.45678900");
    parser.next();
    BigDecimal bd = parser.getBigDecimal();
    BigDecimal expected = new BigDecimal("123.456789");
    assertEquals(0, expected.compareTo(bd));
  }


  @Test
  public void testGetInt() {
    Parser parser = create("123");
    parser.next();
    assertEquals(123, parser.getInt());
  }


  @Test
  public void testGetLong() {
    Parser parser = create("-1234567890000.2");
    parser.next();
    assertEquals(-1234567890000L, parser.getLong());
  }


  @Test
  public void testObject() {
    Parser parser = create("{\"a\":1,\"b\":2}");
    parser.next();
    CJObject object = parser.getObject();
    assertEquals(2, object.size());
    assertEquals(2, object.getInt("b"));
  }


  @Test
  public void testObjectStream() {
    Parser parser = create("{\"a\":1,\"b\":true}");
    parser.next();
    Stream<Entry<String, JsonValue>> stream = parser.getObjectStream();
    Object[] a = stream.toArray();
    assertEquals(2, a.length);
    assertEquals("a", ((Entry<?, ?>) a[0]).getKey());
    assertEquals("b", ((Entry<?, ?>) a[1]).getKey());
    assertEquals(CJNumber.create(1), ((Entry<?, ?>) a[0]).getValue());
    assertEquals(CJTrue.TRUE, ((Entry<?, ?>) a[1]).getValue());
  }


  @Test
  public void testSkipArray() {
    Parser parser = create("[1,true,3,4] 12");
    parser.setRequireSingleRoot(false);
    parser.next();
    Stream<JsonValue> stream = parser.getArrayStream();
    Iterator<JsonValue> iterator = stream.iterator();
    iterator.next();
    iterator.next();
    assertEquals(Event.VALUE_TRUE, parser.getLastEvent());
    parser.skipArray();
    assertEquals(Event.END_ARRAY, parser.getLastEvent());
    assertFalse(iterator.hasNext());
  }


  @Test
  public void testSkipObject() {
    Parser parser = create("[{\"a\":true,\"b\":2}]");
    parser.setRequireSingleRoot(false);
    parser.next();
    parser.next();
    Stream<Entry<String, JsonValue>> stream = parser.getObjectStream();
    Iterator<Entry<String, JsonValue>> iterator = stream.iterator();
    iterator.next();
    assertEquals(Event.VALUE_TRUE, parser.getLastEvent());
    parser.skipObject();
    assertEquals(Event.END_OBJECT, parser.getLastEvent());
    assertFalse(iterator.hasNext());
  }


  @Test
  public void testValueStream() {
    Parser parser = create("{}1 2 false[true]");
    parser.setRequireSingleRoot(false);
    Stream<JsonValue> stream = parser.getValueStream();
    Iterator<JsonValue> iter = stream.iterator();
    StringBuilder buf = new StringBuilder();
    while (iter.hasNext()) {
      JsonValue jv = iter.next();
      buf.append(" ").append(jv.getValueType());
    }
    assertEquals(" OBJECT NUMBER NUMBER FALSE ARRAY", buf.toString());
  }

}
