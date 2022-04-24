package io.setl.json.pointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import org.junit.jupiter.api.Test;

import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;
import io.setl.json.primitive.CJNull;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ObjectPathTest {

  JsonArray jsonArray = new ArrayBuilder().add(1).add(2).build();

  JsonObject jsonObject = new ObjectBuilder()
      .add("bim", new ArrayBuilder().add("baz"))
      .add("boo", "bat").build();


  @Test
  public void add() {
    JsonPointer pointer = PointerFactory.create("/waldo/1/-");
    NoSuchValueException e = assertThrows(NoSuchValueException.class, () -> pointer.add(jsonObject, CJNull.NULL));
    assertEquals("JSON Structure did not contain item at: /waldo", e.getMessage());
    assertEquals("/waldo", e.getPath());
  }


  @Test
  public void add2() {
    JsonPointer pointer = PointerFactory.create("/waldo/1/-");
    PointerMismatchException e = assertThrows(PointerMismatchException.class, () -> pointer.add(jsonArray, CJNull.NULL));
    assertEquals("/waldo", e.getPath());
    assertEquals(ValueType.OBJECT, e.getExpected());
    assertEquals(ValueType.ARRAY, e.getActual());
  }


  @Test
  public void containsPath() {
    JsonExtendedPointer pointer = PointerFactory.create("/a/1");
    assertTrue(pointer.isParentOf(PointerFactory.create("/a/1/b")));
    assertFalse(pointer.isParentOf(PointerFactory.create("/a")));

    pointer = PointerFactory.create("/-/1");
    assertTrue(pointer.isParentOf(PointerFactory.create("/2/1")));
    assertTrue(pointer.isParentOf(PointerFactory.create("/1/1/a")));
    assertTrue(pointer.isParentOf(PointerFactory.create("/-/1/a")));
  }


  @Test
  public void containsValue() {
    testNotContains("/waldo/1/-", jsonObject);
  }


  @Test
  public void containsValue2() {
    testNotContains("/waldo/1/-", jsonArray);
  }


  @Test
  public void copyArray() {
    JsonExtendedPointer pointer = PointerFactory.create("/-/1");
    JsonArray array = new ArrayBuilder()
        .add(1)
        .add(2)
        .add(new ArrayBuilder().add(4).add(5))
        .add(JsonValue.EMPTY_JSON_ARRAY)
        .add(JsonValue.EMPTY_JSON_OBJECT)
        .add(new ObjectBuilder().add("1", "b").add("2", "c"))
        .build();
    JsonArray out = pointer.copy(array, null);
    assertEquals("[null,null,[null,5],[null,null],{},{\"1\":\"b\"}]", out.toString());

    pointer = PointerFactory.create("/a/1");
    out = pointer.copy(JsonValue.EMPTY_JSON_ARRAY, null);
    assertEquals("[]", out.toString());
  }


  @Test
  public void copyObject() {
    JsonExtendedPointer pointer = PointerFactory.create("/a/1");
    JsonObject object = new ObjectBuilder()
        .add("x", JsonValue.EMPTY_JSON_ARRAY)
        .add("a", JsonValue.EMPTY_JSON_ARRAY)
        .build();
    JsonObject out = pointer.copy(object, null);
    assertEquals("{\"a\":[null,null]}", out.toString());

    object = new ObjectBuilder()
        .add("x", JsonValue.EMPTY_JSON_ARRAY)
        .build();
    out = pointer.copy(object, null);
    assertEquals("{}", out.toString());
  }


  @Test
  public void doAdd() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");

    PointerMismatchException e = assertThrows(PointerMismatchException.class, () -> pointer.add(jsonObject, CJNull.NULL));
    assertEquals("Path does not exist [path=/boo, expected=STRUCTURE, actual=STRING]", e.getMessage());
  }


  @Test
  public void doContains() {
    testNotContains("/boo/1/-", jsonObject);
  }


  @Test
  public void doContains2() {
    testNotContains("/waldo/1/a", jsonObject);
  }


  @Test
  public void doGetValue() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");

    PointerMismatchException e = assertThrows(PointerMismatchException.class, () -> pointer.getValue(jsonObject));
    assertEquals("Path does not exist [path=/boo, expected=STRUCTURE, actual=STRING]", e.getMessage());
  }


  @Test
  public void doRemove() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");

    PointerMismatchException e = assertThrows(PointerMismatchException.class, () -> pointer.remove(jsonObject));
    assertEquals("Path does not exist [path=/boo, expected=STRUCTURE, actual=STRING]", e.getMessage());
  }


  @Test
  public void doReplace() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");

    PointerMismatchException e = assertThrows(PointerMismatchException.class, () -> pointer.replace(jsonObject, CJNull.NULL));
    assertEquals("Path does not exist [path=/boo, expected=STRUCTURE, actual=STRING]", e.getMessage());
  }


  @Test
  public void getValue() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");

    PointerMismatchException e = assertThrows(PointerMismatchException.class, () -> pointer.getValue(jsonArray));
    assertEquals("JSON object required [path=/boo, expected=OBJECT, actual=ARRAY]", e.getMessage());
  }


  @Test
  public void remove() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");

    PointerMismatchException e = assertThrows(PointerMismatchException.class, () -> pointer.remove(jsonArray));
    assertEquals("JSON object required [path=/boo, expected=OBJECT, actual=ARRAY]", e.getMessage());
  }


  @Test
  public void replace() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");

    PointerMismatchException e = assertThrows(PointerMismatchException.class, () -> pointer.replace(jsonArray, CJNull.NULL));
    assertEquals("JSON object required [path=/boo, expected=OBJECT, actual=ARRAY]", e.getMessage());
  }


  private void testNotContains(String path, JsonStructure structure) {
    JsonPointer pointer = PointerFactory.create(path);
    assertFalse(pointer.containsValue(structure));
  }

}