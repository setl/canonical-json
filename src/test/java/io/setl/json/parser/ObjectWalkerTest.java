package io.setl.json.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser.Event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.setl.json.CJArray;
import io.setl.json.CJObject;

/**
 * @author Simon Greatrix on 17/01/2020.
 */
public class ObjectWalkerTest {

  private CJObject value;

  private ObjectWalker walker;


  @Test
  public void getArray() {
    lookForKey("array");
    assertTrue(walker.hasNext());
    assertEquals(Event.START_ARRAY, walker.next());
    assertEquals(value.get("array"), walker.getArray());
  }


  @Test
  public void getBigDecimal() {
    lookForKey("bigDecimal");
    assertTrue(walker.hasNext());
    assertEquals(Event.VALUE_NUMBER, walker.next());
    assertEquals(value.getBigDecimal("bigDecimal"), walker.getBigDecimal());
  }


  @Test
  public void getInt() {
    lookForKey("int");
    assertTrue(walker.hasNext());
    assertEquals(Event.VALUE_NUMBER, walker.next());
    assertEquals(value.getInt("int"), walker.getInt());
  }


  @Test
  public void getLong() {
    lookForKey("long");
    assertTrue(walker.hasNext());
    assertEquals(Event.VALUE_NUMBER, walker.next());
    assertEquals(value.getLong("long"), walker.getLong());
  }


  @Test
  public void getNumber() {
    lookForKey("number");
    assertTrue(walker.hasNext());
    assertEquals(Event.VALUE_NUMBER, walker.next());
    BigDecimal bd1 = new BigDecimal(String.valueOf(value.getJsonNumber("number").numberValue()));
    BigDecimal bd2 = new BigDecimal(String.valueOf(walker.getNumber()));
    assertEquals(0, bd1.compareTo(bd2));
  }


  @Test
  public void getObject() {
    lookForKey("object");
    assertTrue(walker.hasNext());
    assertEquals(Event.START_OBJECT, walker.next());
    assertEquals(value.get("object"), walker.getObject());
  }


  @Test
  public void getString() {
    lookForKey("string");
    assertTrue(walker.hasNext());
    assertEquals(Event.VALUE_STRING, walker.next());
    assertEquals(value.getString("string"), walker.getString());
  }


  @Test
  public void getValue1() {
    lookForKey("null");
    assertTrue(walker.hasNext());
    assertEquals(Event.VALUE_NULL, walker.next());
    assertEquals(JsonValue.NULL, walker.getValue());
  }


  @Test
  public void getValue2() {
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> walker.getValue());
    assertEquals("Next has not been called", e.getMessage());
  }


  @Test
  public void getValue3() {
    while (walker.hasNext()) {
      walker.next();
    }

    assertThrows(NoSuchElementException.class, () -> walker.getValue());
  }


  @Test
  public void isIntegralNumber1() {
    lookForKey("int");
    assertTrue(walker.hasNext());
    assertTrue(walker.isIntegralNumber());

  }


  @Test
  public void isIntegralNumber2() {
    lookForKey("double");
    assertTrue(walker.hasNext());
    assertFalse(walker.isIntegralNumber());
  }


  private void lookForKey(String key) {
    while (walker.hasNext()) {
      Event e = walker.next();
      if (e.equals(Event.KEY_NAME)) {
        if (key.equals(walker.getString())) {
          return;
        }
      }
    }
    throw new IllegalStateException("Key not found: " + key);
  }


  @BeforeEach
  public void setUp() {
    CJArray array = new CJArray();
    array.add("x");
    array.add("y");
    array.add("z");

    CJObject object2 = new CJObject();
    object2.put("a", 1);
    object2.put("b", 2);

    CJObject object = new CJObject();
    object.put("array", array);
    object.put("bigDecimal", new BigDecimal(String.valueOf(Math.PI)));
    object.put("int", 1234);
    object.put("long", Long.MAX_VALUE - 1234);
    object.put("number", new BigInteger("1000000000000000000000000000000000000000000000000000000000000"));
    object.put("string", "Hello, World!");
    object.put("double", 1.2345);
    object.put("object", object2);
    object.put("null");

    value = object;

    walker = new ObjectWalker(null, object);
  }

}
