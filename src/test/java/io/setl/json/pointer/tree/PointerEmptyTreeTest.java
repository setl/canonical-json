package io.setl.json.pointer.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;

import org.junit.jupiter.api.Test;

import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.pointer.PointerFactory;

/**
 * import static org.junit.jupiter.api.Assertions.*;
 * import org.junit.jupiter.api.Test;
 *
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
