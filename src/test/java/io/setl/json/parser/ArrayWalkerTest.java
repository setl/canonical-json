package io.setl.json.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.jupiter.api.Test;

import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;

/**
 * @author Simon Greatrix on 17/01/2020.
 */
public class ArrayWalkerTest {

  ArrayWalker walker = new ArrayWalker(null, new ArrayBuilder()
      .add(new ArrayBuilder().add(1).add("b"))
      .add(BigDecimal.TEN)
      .add(56)
      .add(10_000_000_000L)
      .add(5.68)
      .add(new ObjectBuilder().add("a", 1).addNull("b"))
      .add("wibble")
      .build());


  @Test
  public void getArray() {
    walker.next();
    JsonArray a = walker.getArray();
    assertEquals(2, a.size());
    assertEquals(1, a.getInt(0));
    assertEquals("b", a.getString(1));
  }


  @Test
  public void getBigDecimal() {
    walker.next();
    walker.next();
    assertEquals(BigDecimal.TEN, walker.getBigDecimal());
    walker.next();
    assertEquals(BigDecimal.valueOf(56), walker.getBigDecimal());
  }


  @Test
  public void getInt() {
    walker.next();
    walker.next();
    assertEquals(10, walker.getInt());
    walker.next();
    assertEquals(56, walker.getInt());
    walker.next();
    assertEquals((int) (10_000_000_000L), walker.getInt());
  }


  @Test
  public void getLong() {
    walker.next();
    walker.next();
    assertEquals(10L, walker.getLong());
    walker.next();
    assertEquals(56L, walker.getLong());
    walker.next();
    assertEquals(10_000_000_000L, walker.getLong());
  }


  @Test
  public void getObject() {
    walker.next();
    walker.next();
    walker.next();
    walker.next();
    walker.next();
    walker.next();
    JsonObject object = walker.getObject();
    assertNotNull(object);
    assertEquals(1, object.getInt("a"));
  }


  @Test
  public void getString() {
    walker.next();
    walker.next();
    assertEquals("10", walker.getString());
    walker.next();
    walker.next();
    walker.next();
    assertEquals("5.68E0", walker.getString());
    walker.next();
    walker.next();
    assertEquals("wibble", walker.getString());
  }


  @Test
  public void isIntegralNumber() {
    walker.next();
    walker.next();
    assertTrue(walker.isIntegralNumber());
    walker.next();
    walker.next();
    walker.next();
    assertFalse(walker.isIntegralNumber());
  }


  @Test
  public void isIntegralNumber2() {
    walker.next();
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> walker.isIntegralNumber());
    assertEquals("Value is of type ARRAY not NUMBER", e.getMessage());
  }


  @Test
  public void next() {
    StringBuilder buf = new StringBuilder();
    while (walker.hasNext()) {
      buf.append(" ").append(walker.next());
    }
    // NB: The initial START_ARRAY is correctly not returned by the walker
    assertEquals(" START_ARRAY VALUE_NUMBER VALUE_NUMBER VALUE_NUMBER VALUE_NUMBER START_OBJECT VALUE_STRING END_ARRAY", buf.toString());
  }

}
