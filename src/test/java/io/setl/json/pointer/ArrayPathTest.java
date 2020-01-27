package io.setl.json.pointer;

import static org.junit.Assert.assertFalse;

import io.setl.json.builder.JArrayBuilder;
import io.setl.json.exception.PointerIndexException;
import javax.json.JsonArray;
import javax.json.JsonPointer;
import org.junit.Test;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ArrayPathTest {

  JsonArray array = new JArrayBuilder().add(1).add(new JArrayBuilder().add(0).add(1)).build();


  @Test
  public void containsValue() {
    JsonPointer pointer = JPointerFactory.create("/2/1");
    assertFalse(pointer.containsValue(array));
  }


  @Test(expected = PointerIndexException.class)
  public void getValue() {
    JsonPointer pointer = JPointerFactory.create("/2/foo");
    pointer.getValue(array);
  }
}