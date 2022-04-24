package io.setl.json.pointer.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonValue;

import org.junit.jupiter.api.Test;

import io.setl.json.builder.ArrayBuilder;
import io.setl.json.pointer.EmptyPointer;
import io.setl.json.pointer.JsonExtendedPointer;
import io.setl.json.pointer.PointerFactory;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerRootTreeTest {

  @Test
  public void containsAll() {
    JsonArray array = new ArrayBuilder().add("a").add(1).build();
    assertTrue(PointerRootTree.INSTANCE.containsAll(array));
  }


  @Test
  public void copy() {
    JsonArray array = new ArrayBuilder().add("a").add(1).build();
    JsonArray s = PointerRootTree.INSTANCE.copy(array);
    assertEquals(s, array);
    assertNotSame(s, array);
  }


  @Test
  public void getPointers() {
    List<JsonExtendedPointer> pointerList = PointerRootTree.INSTANCE.getPointers();
    assertEquals(1, pointerList.size());
    assertEquals(EmptyPointer.INSTANCE, pointerList.get(0));
  }


  @Test
  public void isParentOf() {
    assertTrue(PointerRootTree.INSTANCE.isParentOf(PointerFactory.create("/a")));
  }


  @Test
  public void remove() {
    assertNull(PointerRootTree.INSTANCE.remove(JsonValue.EMPTY_JSON_ARRAY));
  }

}