package io.setl.json.pointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.json.JsonObject;

import org.junit.jupiter.api.Test;

import io.setl.json.Canonical;
import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class PointerTest {

  @Test
  public void badPath() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> PointerFactory.create("bad-path-without-leading-slash"));
    assertEquals("Pointer must start with '/'", e.getMessage());
  }


  @Test
  public void escape() {
    assertEquals("ab~0~1c", Pointer.escapeKey("ab~/c"));
    assertEquals("ab~1c", Pointer.escapeKey("ab/c"));
    assertEquals("ab~0c", Pointer.escapeKey("ab~c"));
    assertEquals("ab|c", Pointer.escapeKey("ab|c"));
  }


  @Test
  public void getPath() {
    JsonExtendedPointer pointer = PointerFactory.create("/a/b");
    assertEquals("/a/b", pointer.getPath());
  }


  @Test
  public void rfcExamples() {
    JsonObject exampleObject = new ObjectBuilder()
        .add("foo", new ArrayBuilder().add("bar").add("baz"))
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

    assertEquals(exampleObject, PointerFactory.create("").getValue(exampleObject));
    assertEquals(exampleObject.get("foo"), PointerFactory.create("/foo").getValue(exampleObject));
    assertEquals(Canonical.create("bar"), PointerFactory.create("/foo/0").getValue(exampleObject));
    assertEquals(Canonical.create(0), PointerFactory.create("/").getValue(exampleObject));
    assertEquals(Canonical.create(1), PointerFactory.create("/a~1b").getValue(exampleObject));
    assertEquals(Canonical.create(2), PointerFactory.create("/c%d").getValue(exampleObject));
    assertEquals(Canonical.create(3), PointerFactory.create("/e^f").getValue(exampleObject));
    assertEquals(Canonical.create(4), PointerFactory.create("/g|h").getValue(exampleObject));
    assertEquals(Canonical.create(5), PointerFactory.create("/i\\j").getValue(exampleObject));
    assertEquals(Canonical.create(6), PointerFactory.create("/k\"l").getValue(exampleObject));
    assertEquals(Canonical.create(7), PointerFactory.create("/ ").getValue(exampleObject));
    assertEquals(Canonical.create(8), PointerFactory.create("/m~0n").getValue(exampleObject));
  }

}
