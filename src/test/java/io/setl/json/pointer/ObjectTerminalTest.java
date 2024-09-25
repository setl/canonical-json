package io.setl.json.pointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPointer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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


  private void nsve(Executable e, String m) {
    NoSuchValueException ex = assertThrows(NoSuchValueException.class, e);
    assertEquals(m, ex.getMessage());
  }


  private void pme(Executable e, String m) {
    PointerMismatchException ex = assertThrows(PointerMismatchException.class, e);
    assertEquals(m, ex.getMessage());
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


  @Test
  public void testAdd() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    pme(() -> pointer.add(jsonObject, Canonical.create(1)), "JSON object required [path=/bim/waldo, expected=OBJECT, actual=ARRAY]");
  }


  @Test
  public void testContainsValue() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    pme(() -> pointer.containsValue(jsonObject), "JSON object required [path=/bim/waldo, expected=OBJECT, actual=ARRAY]");
  }


  @Test
  public void testGetValue() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    nsve(() -> pointer.getValue(jsonObject), "JSON Structure did not contain item at: /bim/waldo");
  }


  @Test
  public void testGetValue2() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/baz");
    pme(() -> pointer.getValue(jsonObject), "JSON object required [path=/bim/baz, expected=OBJECT, actual=ARRAY]");
  }


  @Test
  public void testRemove() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/baz");
    pme(() -> pointer.remove(jsonObject), "JSON object required [path=/bim/baz, expected=OBJECT, actual=ARRAY]");
  }


  @Test
  public void testRemove2() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    nsve(() -> pointer.remove(jsonObject), "JSON Structure did not contain item at: /bim/waldo");
  }


  @Test
  public void testReplace() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/baz");
    pme(() -> pointer.replace(jsonObject, Canonical.create(1)), "JSON object required [path=/bim/baz, expected=OBJECT, actual=ARRAY]");
  }


  @Test
  public void testReplace2() {
    JsonObject jsonObject = new ObjectBuilder()
        .add("bim", new ObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = PointerFactory.create("/bim/waldo");
    nsve(() -> pointer.replace(jsonObject, Canonical.create(1)), "JSON Structure did not contain item at: /bim/waldo");
  }


}
