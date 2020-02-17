package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonValue;

import org.junit.Test;

import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;
import io.setl.json.primitive.PNull;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ObjectPathTest {

  JsonArray jsonArray = new JArrayBuilder().add(1).add(2).build();

  JsonObject jsonObject = new JObjectBuilder()
      .add("bim", new JArrayBuilder().add("baz"))
      .add("boo", "bat").build();


  @Test(expected = NoSuchValueException.class)
  public void add() {
    JsonPointer pointer = JPointerFactory.create("/waldo/1/-");
    pointer.add(jsonObject, PNull.NULL);
  }


  @Test(expected = PointerMismatchException.class)
  public void add2() {
    JsonPointer pointer = JPointerFactory.create("/waldo/1/-");
    pointer.add(jsonArray, PNull.NULL);
  }


  @Test
  public void containsPath() {
    JsonExtendedPointer pointer = JPointerFactory.create("/a/1");
    assertTrue(pointer.isParentOf(JPointerFactory.create("/a/1/b")));
    assertFalse(pointer.isParentOf(JPointerFactory.create("/a")));

    pointer = JPointerFactory.create("/-/1");
    assertTrue(pointer.isParentOf(JPointerFactory.create("/2/1")));
    assertTrue(pointer.isParentOf(JPointerFactory.create("/1/1/a")));
    assertTrue(pointer.isParentOf(JPointerFactory.create("/-/1/a")));
  }


  @Test
  public void containsValue() {
    JsonPointer pointer = JPointerFactory.create("/waldo/1/-");
    assertFalse(pointer.containsValue(jsonObject));
  }


  @Test
  public void containsValue2() {
    JsonPointer pointer = JPointerFactory.create("/waldo/1/-");
    assertFalse(pointer.containsValue(jsonArray));
  }


  @Test
  public void copyArray() {
    JsonExtendedPointer pointer = JPointerFactory.create("/-/1");
    JsonArray array = new JArrayBuilder()
        .add(1)
        .add(2)
        .add(new JArrayBuilder().add(4).add(5))
        .add(JsonValue.EMPTY_JSON_ARRAY)
        .add(JsonValue.EMPTY_JSON_OBJECT)
        .add(new JObjectBuilder().add("1", "b").add("2", "c"))
        .build();
    JsonArray out = pointer.copy(array, null);
    assertEquals("[null,null,[null,5],[null,null],{},{\"1\":\"b\"}]", out.toString());

    pointer = JPointerFactory.create("/a/1");
    out = pointer.copy(JsonValue.EMPTY_JSON_ARRAY, null);
    assertEquals("[]", out.toString());
  }


  @Test
  public void copyObject() {
    JsonExtendedPointer pointer = JPointerFactory.create("/a/1");
    JsonObject object = new JObjectBuilder()
        .add("x", JsonValue.EMPTY_JSON_ARRAY)
        .add("a", JsonValue.EMPTY_JSON_ARRAY)
        .build();
    JsonObject out = pointer.copy(object, null);
    assertEquals("{\"a\":[null,null]}", out.toString());

    object = new JObjectBuilder()
        .add("x", JsonValue.EMPTY_JSON_ARRAY)
        .build();
    out = pointer.copy(object, null);
    assertEquals("{}", out.toString());
  }


  @Test(expected = PointerMismatchException.class)
  public void doAdd() {
    JsonPointer pointer = JPointerFactory.create("/boo/1/-");
    pointer.add(jsonObject, PNull.NULL);
  }


  @Test
  public void doContains() {
    JsonPointer pointer = JPointerFactory.create("/boo/1/-");
    assertFalse(pointer.containsValue(jsonObject));
  }


  @Test
  public void doContains2() {
    JsonPointer pointer = JPointerFactory.create("/waldo/1/a");
    assertFalse(pointer.containsValue(jsonObject));
  }


  @Test(expected = PointerMismatchException.class)
  public void doGetValue() {
    JsonPointer pointer = JPointerFactory.create("/boo/1/-");
    pointer.getValue(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void doRemove() {
    JsonPointer pointer = JPointerFactory.create("/boo/1/-");
    pointer.remove(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void doReplace() {
    JsonPointer pointer = JPointerFactory.create("/boo/1/-");
    pointer.replace(jsonObject, PNull.NULL);
  }


  @Test(expected = PointerMismatchException.class)
  public void getValue() {
    JsonPointer pointer = JPointerFactory.create("/boo/1/-");
    pointer.getValue(jsonArray);
  }


  @Test(expected = PointerMismatchException.class)
  public void remove() {
    JsonPointer pointer = JPointerFactory.create("/boo/1/-");
    pointer.remove(jsonArray);
  }


  @Test(expected = PointerMismatchException.class)
  public void replace() {
    JsonPointer pointer = JPointerFactory.create("/boo/1/-");
    pointer.replace(jsonArray, PNull.NULL);
  }

}