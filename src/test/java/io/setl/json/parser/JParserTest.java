package io.setl.json.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.primitive.PTrue;
import io.setl.json.primitive.numbers.PInt;
import io.setl.json.primitive.numbers.PNumber;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.json.JsonValue;
import javax.json.stream.JsonParser.Event;
import org.junit.Test;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
public class JParserTest {

  private JParser create(String data) {
    return (JParser) new JParserFactory(null).createParser(new StringReader(data));
  }


  @Test(expected = IllegalStateException.class)
  public void testBadState() {
    JParser parser = create("[[12");
    parser.next();
    parser.getLong();
  }


  @Test
  public void testGetArray() {
    JParser parser = create("[0,1,2,3]");
    parser.next();
    JArray array = parser.getArray();
    assertEquals(4, array.size());
    for (int i = 0; i < 4; i++) {
      assertEquals(i, array.getInt(i));
    }
  }


  @Test
  public void testGetArrayStream() {
    JParser parser = create("[0,\"\",null,3]");
    parser.next();
    Stream<JsonValue> stream = parser.getArrayStream();
    assertEquals(4, stream.count());
  }


  @Test
  public void testGetBigDecimal() {
    JParser parser = create("123.45678900");
    parser.next();
    BigDecimal bd = parser.getBigDecimal();
    BigDecimal expected = new BigDecimal("123.456789");
    assertEquals(0, expected.compareTo(bd));
  }


  @Test
  public void testGetInt() {
    JParser parser = create("123");
    parser.next();
    assertEquals(123, parser.getInt());
  }


  @Test
  public void testGetLong() {
    JParser parser = create("-1234567890000.2");
    parser.next();
    assertEquals(-1234567890000L, parser.getLong());
  }


  @Test
  public void testObject() {
    JParser parser = create("{\"a\":1,\"b\":2}");
    parser.next();
    JObject object = parser.getObject();
    assertEquals(2, object.size());
    assertEquals(2, object.getInt("b"));
  }


  @Test
  public void testObjectStream() {
    JParser parser = create("{\"a\":1,\"b\":true}");
    parser.next();
    Stream<Entry<String, JsonValue>> stream = parser.getObjectStream();
    Object[] a = stream.toArray();
    assertEquals(2, a.length);
    assertEquals("a", ((Entry<?, ?>) a[0]).getKey());
    assertEquals("b", ((Entry<?, ?>) a[1]).getKey());
    assertEquals(PNumber.create(1), ((Entry<?, ?>) a[0]).getValue());
    assertEquals(PTrue.TRUE, ((Entry<?, ?>) a[1]).getValue());
  }


  @Test
  public void testSkipArray() {
    JParser parser = create("[1,true,3,4] 12");
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
    JParser parser = create("[{\"a\":true,\"b\":2}]");
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
    JParser parser = create("{}1 2 false[true]");
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