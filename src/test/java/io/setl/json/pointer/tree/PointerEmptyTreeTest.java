package io.setl.json.pointer.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;

import org.junit.Test;

import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.pointer.PointerFactory;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerEmptyTreeTest {

  @Test
  public void containsAll() {
    JsonObject object = new ObjectBuilder().add("a", "b").add("b", 1).build();
    assertFalse(PointerEmptyTree.INSTANCE.containsAll(object));
  }


  @Test
  public void copyArray() {
    JsonArray array = new ArrayBuilder().add("a").add(1).build();
    JsonArray s = PointerEmptyTree.INSTANCE.copy(array);
    assertNull(s);
  }


  @Test
  public void copyObject() {
    JsonObject object = new ObjectBuilder().add("a", "b").add("b", 1).build();
    JsonStructure s = PointerEmptyTree.INSTANCE.copy(object);
    assertNull(s);
  }


  @Test
  public void getPointers() {
    assertTrue(PointerEmptyTree.INSTANCE.getPointers().isEmpty());
  }


  @Test
  public void isParentOf() {
    assertFalse(PointerEmptyTree.INSTANCE.isParentOf(PointerFactory.create("/foo")));
  }


  @Test
  public void removeArray() {
    JsonArray array = new ArrayBuilder().add("a").add(1).build();
    JsonArray s = PointerEmptyTree.INSTANCE.remove(array);
    assertEquals(s, array);
  }


  @Test
  public void removeObject() {
    JsonObject object = new ObjectBuilder().add("a", "b").add("b", 1).build();
    JsonObject s = PointerEmptyTree.INSTANCE.remove(object);
    assertEquals(object, s);
  }

}