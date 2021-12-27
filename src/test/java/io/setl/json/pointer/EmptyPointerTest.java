package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.Test;

import io.setl.json.Canonical;
import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.exception.PointerMismatchException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class EmptyPointerTest {

  JsonObject object1 = new ObjectBuilder().add("a", "b").build();

  JsonObject object2 = new ObjectBuilder().add("c", "d").build();

  JsonExtendedPointer pointer = PointerFactory.create("");


  @Test
  public void add() {
    JsonObject jo = pointer.add(object1, object2);
    assertEquals(object2, jo);
  }


  @Test(expected = PointerMismatchException.class)
  public void add2() {
    pointer.add(object1, Canonical.EMPTY_JSON_ARRAY);
  }


  @Test
  public void containsPointer() {
    assertTrue(pointer.isParentOf(pointer));
    JsonExtendedPointer p2 = PointerFactory.create("/");
    assertTrue(pointer.isParentOf(p2));
    assertFalse(p2.isParentOf(pointer));
  }


  @Test
  public void containsValue() {
    assertTrue(pointer.containsValue(object1));
  }


  @Test
  public void copyArray() {
    JsonArray array = new ArrayBuilder().add(1).add(2).add(JsonValue.EMPTY_JSON_OBJECT).build();
    JsonArray o = pointer.copy(array, null);
    assertEquals(array, o);
  }


  @Test
  public void copyObject() {
    JsonObject o = pointer.copy(object1, null);
    assertEquals(object1, o);
  }


  @Test
  public void getPath() {
    assertEquals("", pointer.getPath());
  }


  @Test
  public void getValue() {
    assertEquals(object1, pointer.getValue(object1));
  }


  @Test(expected = JsonException.class)
  public void remove() {
    pointer.remove(object1);
  }


  @Test(expected = JsonException.class)
  public void replace() {
    pointer.replace(object1, object2);
  }

}