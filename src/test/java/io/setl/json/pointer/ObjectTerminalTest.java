package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.setl.json.Primitive;
import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import org.junit.Test;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ObjectTerminalTest {

  @Test
  public void add() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/4");
    pointer.add(jsonObject, Primitive.create(1));
    assertEquals("{\"bim\":{\"4\":1,\"baz\":\"boo\"}}", jsonObject.toString());
  }


  @Test
  public void containsValue() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/waldo");
    assertFalse(pointer.containsValue(jsonObject));
    pointer = JPointerFactory.create("/bim/baz");
    assertTrue(pointer.containsValue(jsonObject));
  }


  @Test
  public void getValue() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/baz");
    assertEquals(Primitive.create("boo"), pointer.getValue(jsonObject));
  }


  @Test
  public void remove() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/baz");
    pointer.remove(jsonObject);
    assertEquals("{\"bim\":{}}", jsonObject.toString());
  }


  @Test
  public void replace() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/baz");
    pointer.replace(jsonObject, Primitive.create(1));
    assertEquals("{\"bim\":{\"baz\":1}}", jsonObject.toString());
  }


  @Test(expected = PointerMismatchException.class)
  public void testAdd() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/waldo");
    pointer.add(jsonObject, Primitive.create(1));
  }


  @Test(expected = PointerMismatchException.class)
  public void testContainsValue() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/waldo");
    pointer.containsValue(jsonObject);
  }


  @Test(expected = NoSuchValueException.class)
  public void testGetValue() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/waldo");
    pointer.getValue(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void testGetValue2() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/baz");
    pointer.getValue(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void testRemove() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/baz");
    pointer.remove(jsonObject);
  }


  @Test(expected = NoSuchValueException.class)
  public void testRemove2() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/waldo");
    pointer.remove(jsonObject);
  }


  @Test(expected = PointerMismatchException.class)
  public void testReplace() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JArrayBuilder()
            .add("baz")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/baz");
    pointer.replace(jsonObject, Primitive.create(1));
  }


  @Test(expected = NoSuchValueException.class)
  public void testReplace2() {
    JsonObject jsonObject = new JObjectBuilder()
        .add("bim", new JObjectBuilder()
            .add("baz", "boo")
        ).build();
    JsonPointer pointer = JPointerFactory.create("/bim/waldo");
    pointer.replace(jsonObject, Primitive.create(1));
  }
}