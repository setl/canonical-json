package io.setl.json.pointer;

import static org.junit.Assert.assertFalse;

import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.exception.NoSuchValueException;
import io.setl.json.exception.PointerMismatchException;
import io.setl.json.primitive.PNull;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import org.junit.Test;

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
  public void containsValue() {
    JsonPointer pointer = JPointerFactory.create("/waldo/1/-");
    assertFalse(pointer.containsValue(jsonObject));
  }


  @Test
  public void containsValue2() {
    JsonPointer pointer = JPointerFactory.create("/waldo/1/-");
    assertFalse(pointer.containsValue(jsonArray));
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
    pointer.replace(jsonObject,PNull.NULL);
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