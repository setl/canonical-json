package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;

import javax.json.JsonObject;

import org.junit.Test;

import io.setl.json.Primitive;
import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class JPointerTest {

  @Test(expected = IllegalArgumentException.class)
  public void badPath() {
    JPointerFactory.create("bad-path-without-leading-slash");
  }


  @Test
  public void escape() {
    assertEquals("ab~0~1c", JPointer.escapeKey("ab~/c"));
    assertEquals("ab~1c", JPointer.escapeKey("ab/c"));
    assertEquals("ab~0c", JPointer.escapeKey("ab~c"));
    assertEquals("ab|c", JPointer.escapeKey("ab|c"));
  }


  @Test
  public void getPath() {
    JsonExtendedPointer pointer = JPointerFactory.create("/a/b");
    assertEquals("/a/b", pointer.getPath());
  }


  @Test
  public void rfcExamples() {
    JsonObject exampleObject = new JObjectBuilder()
        .add("foo", new JArrayBuilder().add("bar").add("baz"))
        .add("", 0)
        .add("a/b", 1)
        .add("c%d", 2)
        .add("e^f", 3)
        .add("g|h", 4)
        .add("i\\j", 5)
        .add("k\"l", 6)
        .add(" ", 7)
        .add("m~n", 8)
        .build();

    assertEquals(exampleObject, JPointerFactory.create("").getValue(exampleObject));
    assertEquals(exampleObject.get("foo"), JPointerFactory.create("/foo").getValue(exampleObject));
    assertEquals(Primitive.create("bar"), JPointerFactory.create("/foo/0").getValue(exampleObject));
    assertEquals(Primitive.create(0), JPointerFactory.create("/").getValue(exampleObject));
    assertEquals(Primitive.create(1), JPointerFactory.create("/a~1b").getValue(exampleObject));
    assertEquals(Primitive.create(2), JPointerFactory.create("/c%d").getValue(exampleObject));
    assertEquals(Primitive.create(3), JPointerFactory.create("/e^f").getValue(exampleObject));
    assertEquals(Primitive.create(4), JPointerFactory.create("/g|h").getValue(exampleObject));
    assertEquals(Primitive.create(5), JPointerFactory.create("/i\\j").getValue(exampleObject));
    assertEquals(Primitive.create(6), JPointerFactory.create("/k\"l").getValue(exampleObject));
    assertEquals(Primitive.create(7), JPointerFactory.create("/ ").getValue(exampleObject));
    assertEquals(Primitive.create(8), JPointerFactory.create("/m~0n").getValue(exampleObject));
  }

}