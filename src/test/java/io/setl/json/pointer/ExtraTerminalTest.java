package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import io.setl.json.Primitive;
import io.setl.json.builder.JArrayBuilder;
import io.setl.json.exception.PointerIndexException;
import io.setl.json.primitive.PNull;
import javax.json.JsonArray;
import javax.json.JsonPointer;
import org.junit.Test;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ExtraTerminalTest {

  JsonArray array = new JArrayBuilder().add(0).add(new JArrayBuilder().add(0).add(1)).build();


  @Test
  public void add() {
    JsonPointer pointer = JPointerFactory.create("/1/-");
    pointer.add(array, Primitive.create(true));
    assertEquals("[0,[0,1,true]]", array.toString());
  }


  @Test
  public void containsValue() {
    JsonPointer pointer = JPointerFactory.create("/1/-");
    assertFalse(pointer.containsValue(array));
  }


  @Test(expected = PointerIndexException.class)
  public void getValue() {
    JsonPointer pointer = JPointerFactory.create("/1/-");
    pointer.getValue(array);
  }


  @Test(expected = PointerIndexException.class)
  public void remove() {
    JsonPointer pointer = JPointerFactory.create("/1/-");
    pointer.remove(array);
  }


  @Test(expected = PointerIndexException.class)
  public void replace() {
    JsonPointer pointer = JPointerFactory.create("/1/-");
    pointer.replace(array, PNull.NULL);
  }
}