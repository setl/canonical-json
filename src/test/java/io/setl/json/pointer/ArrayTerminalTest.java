package io.setl.json.pointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonPointer;

import org.junit.jupiter.api.Test;

import io.setl.json.CJArray;
import io.setl.json.Canonical;
import io.setl.json.builder.ArrayBuilder;
import io.setl.json.exception.PointerIndexException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ArrayTerminalTest {

  JsonArray array = new ArrayBuilder().add(1).add(new ArrayBuilder().add(0).add(1)).build();


  @Test
  public void add() {
    JsonPointer pointer = PointerFactory.create("/1/0");
    pointer.add(array, Canonical.create(true));
    assertEquals("[1,[true,0,1]]", array.toString());
  }


  @Test
  public void add2() {
    JsonPointer pointer = PointerFactory.create("/1/3");
    PointerIndexException e = assertThrows(PointerIndexException.class, () -> pointer.add(array, Canonical.create(true)));
    assertEquals("Array index too large. Path is /1/3, but array size is 2.", e.getMessage());
  }


  @Test
  public void add3() {
    JsonPointer pointer = PointerFactory.create("/1/2");
    pointer.add(array, Canonical.create(true));
    assertEquals("[1,[0,1,true]]", array.toString());
  }


  @Test
  public void containsPointer() {
    JsonExtendedPointer pointer = PointerFactory.create("/a/0");
    assertFalse(pointer.isParentOf(PointerFactory.create("/a/-")));
    assertTrue(pointer.isParentOf(PointerFactory.create("/a/0/a")));
  }


  @Test
  public void containsValue() {
    JsonPointer pointer = PointerFactory.create("/1/0");
    assertTrue(pointer.containsValue(array));

    pointer = PointerFactory.create("/1/2");
    assertFalse(pointer.containsValue(array));
  }


  @Test
  public void containsValue2() {
    JsonPointer pointer = PointerFactory.create("/1/3");
    assertFalse(pointer.containsValue(array));
  }


  @Test
  public void copy() {
    JsonArray output = new CJArray();
    JsonExtendedPointer pointer = PointerFactory.create("/1/1");
    pointer.copy(array, output);
    assertEquals("[null,[null,1]]", output.toString());
    output = new CJArray();
    pointer = PointerFactory.create("/1/3");
    pointer.copy(array, output);
    assertEquals("[null,[null,null,null,null]]", output.toString());
  }


  @Test
  public void getValue() {
    JsonPointer pointer = PointerFactory.create("/1/0");
    assertEquals(Canonical.create(0), pointer.getValue(array));
  }


  @Test
  public void getValue2() {
    JsonPointer pointer = PointerFactory.create("/1/3");
    PointerIndexException e = assertThrows(PointerIndexException.class, () -> pointer.getValue(array));
    assertEquals("Array index too large. Path is /1/3, but array size is 2.", e.getMessage());
  }


  @Test
  public void remove() {
    JsonPointer pointer = PointerFactory.create("/1/0");
    pointer.remove(array);
    assertEquals("[1,[1]]", array.toString());
  }


  @Test
  public void remove2() {
    JsonPointer pointer = PointerFactory.create("/1/3");
    PointerIndexException e = assertThrows(PointerIndexException.class, () -> pointer.remove(array));
    assertEquals("Array index too large. Path is /1/3, but array size is 2.", e.getMessage());
  }


  @Test
  public void replace() {
    JsonPointer pointer = PointerFactory.create("/1/0");
    pointer.replace(array, Canonical.create(true));
    assertEquals("[1,[true,1]]", array.toString());
  }


  @Test
  public void replace2() {
    JsonPointer pointer = PointerFactory.create("/1/3");
    PointerIndexException e = assertThrows(PointerIndexException.class, () -> pointer.replace(array, Canonical.create(false)));
    assertEquals("Array index too large. Path is /1/3, but array size is 2.", e.getMessage());
  }

}
