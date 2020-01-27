package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.setl.json.Primitive;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.exception.PointerMismatchException;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import org.junit.Test;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class EmptyPointerTest {

  JsonObject object1 = new JObjectBuilder().add("a", "b").build();

  JsonObject object2 = new JObjectBuilder().add("c", "d").build();

  JsonPointer pointer = JPointerFactory.create("");


  @Test
  public void add() {
    JsonObject jo = pointer.add(object1, object2);
    assertEquals(object2, jo);
  }


  @Test(expected = PointerMismatchException.class)
  public void add2() {
    JsonObject jo = pointer.add(object1, Primitive.EMPTY_JSON_ARRAY);
    assertEquals(object2, jo);
  }


  @Test
  public void containsValue() {
    assertTrue(pointer.containsValue(object1));
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