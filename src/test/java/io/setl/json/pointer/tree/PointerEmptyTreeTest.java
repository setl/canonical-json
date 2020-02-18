package io.setl.json.pointer.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import org.junit.Test;

import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.pointer.JPointerFactory;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerEmptyTreeTest {

  @Test
  public void containsAll() {
    JsonObject object = new JObjectBuilder().add("a", "b").add("b", 1).build();
    assertFalse(PointerEmptyTree.EMPTY.containsAll(object));
  }


  @Test
  public void copyArray() {
    JsonArray array = new JArrayBuilder().add("a").add(1).build();
    JsonArray s = PointerEmptyTree.EMPTY.copy(array);
    assertTrue(s.isEmpty());
  }


  @Test
  public void copyObject() {
    JsonObject object = new JObjectBuilder().add("a", "b").add("b", 1).build();
    JsonStructure s = PointerEmptyTree.EMPTY.copy(object);
    assertTrue(((JsonObject) s).isEmpty());
  }


  @Test
  public void getPointers() {
    assertTrue(PointerEmptyTree.EMPTY.getPointers().isEmpty());
  }


  @Test
  public void isParentOf() {
    assertFalse(PointerEmptyTree.EMPTY.isParentOf(JPointerFactory.create("/foo")));
  }


  @Test
  public void removeArray() {
    JsonArray array = new JArrayBuilder().add("a").add(1).build();
    JsonArray s = PointerEmptyTree.EMPTY.remove(array);
    assertEquals(s, array);
  }


  @Test
  public void removeObject() {
    JsonObject object = new JObjectBuilder().add("a", "b").add("b", 1).build();
    JsonObject s = PointerEmptyTree.EMPTY.remove(object);
    assertEquals(object, s);
  }

}