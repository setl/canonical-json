package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.setl.json.Primitive;
import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.exception.PointerIndexException;
import io.setl.json.primitive.PNull;
import javax.json.JsonArray;
import javax.json.JsonObject;
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

  @Test
  public void containsPath() {
    JsonExtendedPointer pointer = JPointerFactory.create("/1/-");
    assertTrue(pointer.isParentOf(JPointerFactory.create("/1/4")));
    assertTrue(pointer.isParentOf(JPointerFactory.create("/1/3/a")));
    assertTrue(pointer.isParentOf(JPointerFactory.create("/1/3/1")));
    assertTrue(pointer.isParentOf(JPointerFactory.create("/1/-/a")));
    assertTrue(pointer.isParentOf(JPointerFactory.create("/1/-/1")));
    assertTrue(pointer.isParentOf(JPointerFactory.create("/1/-")));
    assertTrue(pointer.isParentOf(JPointerFactory.create("/1/-/-")));
    assertFalse(pointer.isParentOf(JPointerFactory.create("/1/x/a")));
    assertFalse(pointer.isParentOf(JPointerFactory.create("/1/x")));
  }

  @Test
  public void copyObject() {
    JsonExtendedPointer pointer = JPointerFactory.create("/1/-");
    JsonObject object = new JObjectBuilder()
        .add("1", new JObjectBuilder()
            .add("-", new JObjectBuilder()
            .add("a","b"))).build();
    JsonObject o = pointer.copy(object,null);
    assertEquals("{\"1\":{\"-\":{\"a\":\"b\"}}}",o.toString());
  }


  @Test
  public void copyArray() {
    JsonExtendedPointer pointer = JPointerFactory.create("/1/-");
    JsonArray object = new JArrayBuilder()
        .add(0)
        .add(new JArrayBuilder()
            .add(1)
            .add(2)
            .add(new JObjectBuilder()
                .add("a","b"))).build();
    JsonArray o = pointer.copy(object,null);
    assertEquals("[null,[1,2,{\"a\":\"b\"}]]",o.toString());
  }
}