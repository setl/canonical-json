package io.setl.json.pointer.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonValue;

import org.junit.Test;

import io.setl.json.builder.JArrayBuilder;
import io.setl.json.pointer.EmptyPointer;
import io.setl.json.pointer.JPointerFactory;
import io.setl.json.pointer.JsonExtendedPointer;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerRootTreeTest {

  @Test
  public void containsAll() {
    JsonArray array = new JArrayBuilder().add("a").add(1).build();
    assertTrue(PointerRootTree.INSTANCE.containsAll(array));
  }


  @Test
  public void copy() {
    JsonArray array = new JArrayBuilder().add("a").add(1).build();
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
    assertTrue(PointerRootTree.INSTANCE.isParentOf(JPointerFactory.create("/a")));
  }


  @Test
  public void remove() {
    assertNull(PointerRootTree.INSTANCE.remove(JsonValue.EMPTY_JSON_ARRAY));
  }

}