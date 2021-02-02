package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPointer;

import org.junit.Test;

import io.setl.json.Canonical;
import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ObjectTerminalTest {

  @Test
  public void add() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/4");
    pointer.add(jsonObject, Canonical.create(1));
    assertEquals("{\"bim\":{\"4\":1,\"baz\":\"boo\"}}", jsonObject.toString());
  }


  @Test
  public void containsPath() {
    JsonExtendedPointer pointer = PointerFactory.create("/a");
    assertTrue(pointer.isParentOf(PointerFactory.create("/a/b")));
    assertFalse(pointer.isParentOf(PointerFactory.create("/b/b")));
    assertFalse(pointer.isParentOf(PointerFactory.create("")));
    assertFalse(pointer.isParentOf(PointerFactory.create("/b")));
  }


  @Test
  public void containsValue() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    assertFalse(pointer.containsValue(jsonObject));
    pointer = PointerFactory.create("/bim/baz");
    assertTrue(pointer.containsValue(jsonObject));
  }


  @Test
  public void copyArray() {
    JsonExtendedPointer pointer = PointerFactory.create("/a");
    JsonArray array = new ArrayBuilder().add(1).add(2).build();
    JsonArray out = pointer.copy(array, null);
    assertEquals("[]", out.toString());
  }


  @Test
  public void getValue() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/baz");
    assertEquals(Canonical.create("boo"), pointer.getValue(jsonObject));
  }


  @Test
  public void remove() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/baz");
    pointer.remove(jsonObject);
    assertEquals("{\"bim\":{}}", jsonObject.toString());
  }


  @Test
  public void replace() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/baz");
    pointer.replace(jsonObject, Canonical.create(1));
    assertEquals("{\"bim\":{\"baz\":1}}", jsonObject.toString());
  }


  @Test(expected = PointerMismatchException.class)
  public void testAdd() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    pointer.add(jsonObject, Canonical.create(1));
  }


  @Test(expected = PointerMismatchException.class)
  public void testContainsValue() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    pointer.containsValue(jsonObject);
  }


  @Test(expected = NoSuchValueException.class)
  public void testGetValue() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    pointer.getValue(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void testGetValue2() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/baz");
    pointer.getValue(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void testRemove() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/baz");
    pointer.remove(jsonObject);
  }


  @Test(expected = NoSuchValueException.class)
  public void testRemove2() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    pointer.remove(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void testReplace() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/baz");
    pointer.replace(jsonObject, Canonical.create(1));
  }


  @Test(expected = NoSuchValueException.class)
  public void testReplace2() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    pointer.replace(jsonObject, Canonical.create(1));
  }

}
