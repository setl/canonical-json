package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import org.junit.Test;

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


  @Test(expected = NoSuchValueException.class)
  public void add() {
    JsonPointer pointer = PointerFactory.create("/waldo/1/-");
    pointer.add(jsonObject, CJNull.NULL);
  }


  @Test(expected = PointerMismatchException.class)
  public void add2() {
    JsonPointer pointer = PointerFactory.create("/waldo/1/-");
    pointer.add(jsonArray, CJNull.NULL);
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

  private void testNotContains(String path, JsonStructure structure) {
      JsonPointer pointer = PointerFactory.create(path);
      assertFalse(pointer.containsValue(structure));
  }

  @Test
  public void containsValue() {
    testNotContains("/waldo/1/-",jsonObject);
  }


  @Test
  public void containsValue2() {
    testNotContains("/waldo/1/-",jsonArray);
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


  @Test(expected = PointerMismatchException.class)
  public void doAdd() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");
    pointer.add(jsonObject, CJNull.NULL);
  }


  @Test
  public void doContains() {
    testNotContains("/boo/1/-",jsonObject);
  }


  @Test
  public void doContains2() {
    testNotContains("/waldo/1/a",jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void doGetValue() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");
    pointer.getValue(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void doRemove() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");
    pointer.remove(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void doReplace() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");
    pointer.replace(jsonObject, CJNull.NULL);
  }


  @Test(expected = PointerMismatchException.class)
  public void getValue() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");
    pointer.getValue(jsonArray);
  }


  @Test(expected = PointerMismatchException.class)
  public void remove() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");
    pointer.remove(jsonArray);
  }


  @Test(expected = PointerMismatchException.class)
  public void replace() {
    JsonPointer pointer = PointerFactory.create("/boo/1/-");
    pointer.replace(jsonArray, CJNull.NULL);
  }

}