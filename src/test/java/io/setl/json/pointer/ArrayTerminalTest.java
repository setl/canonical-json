package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonPointer;

import org.junit.Test;

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


  @Test(expected = PointerIndexException.class)
  public void add2() {
    JsonPointer pointer = PointerFactory.create("/1/3");
    pointer.add(array, Canonical.create(true));
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


  @Test(expected = PointerIndexException.class)
  public void getValue2() {
    JsonPointer pointer = PointerFactory.create("/1/3");
    pointer.getValue(array);
  }


  @Test
  public void remove() {
    JsonPointer pointer = PointerFactory.create("/1/0");
    pointer.remove(array);
    assertEquals("[1,[1]]", array.toString());
  }


  @Test(expected = PointerIndexException.class)
  public void remove2() {
    JsonPointer pointer = PointerFactory.create("/1/3");
    pointer.remove(array);
  }


  @Test
  public void replace() {
    JsonPointer pointer = PointerFactory.create("/1/0");
    pointer.replace(array, Canonical.create(true));
    assertEquals("[1,[true,1]]", array.toString());
  }


  @Test(expected = PointerIndexException.class)
  public void replace2() {
    JsonPointer pointer = PointerFactory.create("/1/3");
    pointer.replace(array, Canonical.create(false));
  }

}
