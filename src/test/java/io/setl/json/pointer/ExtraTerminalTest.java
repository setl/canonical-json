package io.setl.json.pointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPointer;

import org.junit.jupiter.api.Test;

import io.setl.json.Canonical;
import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.exception.PointerIndexException;
import io.setl.json.primitive.CJNull;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ExtraTerminalTest {

  JsonArray array = new ArrayBuilder().add(0).add(new ArrayBuilder().add(0).add(1)).build();


  @Test
  public void add() {
    JsonPointer pointer = PointerFactory.create("/1/-");
    pointer.add(array, Canonical.create(true));
    assertEquals("[0,[0,1,true]]", array.toString());
  }


  @Test
  public void containsPath() {
    JsonExtendedPointer pointer = PointerFactory.create("/1/-");
    assertTrue(pointer.isParentOf(PointerFactory.create("/1/4")));
    assertTrue(pointer.isParentOf(PointerFactory.create("/1/3/a")));
    assertTrue(pointer.isParentOf(PointerFactory.create("/1/3/1")));
    assertTrue(pointer.isParentOf(PointerFactory.create("/1/-/a")));
    assertTrue(pointer.isParentOf(PointerFactory.create("/1/-/1")));
    assertTrue(pointer.isParentOf(PointerFactory.create("/1/-")));
    assertTrue(pointer.isParentOf(PointerFactory.create("/1/-/-")));
    assertFalse(pointer.isParentOf(PointerFactory.create("/1/x/a")));
    assertFalse(pointer.isParentOf(PointerFactory.create("/1/x")));
  }


  @Test
  public void containsValue() {
    JsonPointer pointer = PointerFactory.create("/1/-");
    assertFalse(pointer.containsValue(array));
  }


  @Test
  public void copyArray() {
    JsonExtendedPointer pointer = PointerFactory.create("/1/-");
    JsonArray object = new ArrayBuilder()
        .add(0)
        .add(new ArrayBuilder()
            .add(1)
            .add(2)
            .add(new ObjectBuilder()
                .add("a", "b"))).build();
    JsonArray o = pointer.copy(object, null);
    assertEquals("[null,[1,2,{\"a\":\"b\"}]]", o.toString());
  }


  @Test
  public void copyObject() {
    JsonExtendedPointer pointer = PointerFactory.create("/1/-");
    JsonObject object = new ObjectBuilder()
        .add("1", new ObjectBuilder()
            .add("-", new ObjectBuilder()
                .add("a", "b"))).build();
    JsonObject o = pointer.copy(object, null);
    assertEquals("{\"1\":{\"-\":{\"a\":\"b\"}}}", o.toString());
  }


  @Test
  public void getValue() {
    JsonPointer pointer = PointerFactory.create("/1/-");
    PointerIndexException e = assertThrows(PointerIndexException.class, () -> pointer.getValue(array));
    assertEquals("/1/-", e.getPath());
    assertEquals(2, e.getSize());
  }


  @Test
  public void remove() {
    JsonPointer pointer = PointerFactory.create("/1/-");
    PointerIndexException e = assertThrows(PointerIndexException.class, () -> pointer.remove(array));
    assertEquals("/1/-", e.getPath());
    assertEquals(2, e.getSize());
  }


  @Test
  public void replace() {
    JsonPointer pointer = PointerFactory.create("/1/-");
    PointerIndexException e = assertThrows(PointerIndexException.class, () -> pointer.replace(array, CJNull.NULL));
    assertEquals("/1/-", e.getPath());
    assertEquals(2, e.getSize());
  }

}
