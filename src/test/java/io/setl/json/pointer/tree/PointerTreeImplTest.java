package io.setl.json.pointer.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.pointer.PointerFactory;
import io.setl.json.primitive.CJString;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerTreeImplTest {

  PointerTree tree;


  @BeforeEach
  public void before() {
    tree = new PointerTreeBuilder()
        .add("/a/b")
        .add("/b/0/a")
        .add("/b/1/b")
        .add("/-/a")
        .add("/-/b")
        .build();
  }


  @Test
  public void containsAllArray() {
    CJArray root = new CJArray();
    // if we don't ask for anything it is all OK
    assertTrue(tree.containsAll(root));

    root.add(new CJObject(Map.of("a", 1)));
    assertTrue(tree.containsAll(root));
  }


  @Test
  public void containsAllObject() {
    CJObject root = new CJObject();
    // if we don't ask for anything it is all OK
    assertTrue(tree.containsAll(root));

    CJObject objectA = new CJObject();
    root.put("a", objectA);
    objectA.put("b", 1);
    assertTrue(tree.containsAll(root));

    objectA.put("b", new CJObject(Map.of("x", "y")));
    assertTrue(tree.containsAll(root));

    objectA.put("b", new CJArray(List.of(1, 2, 3)));
    assertTrue(tree.containsAll(root));

    // Add something that is not OK
    objectA.put("z", JsonValue.TRUE);
    assertFalse(tree.containsAll(root));
    objectA.remove("z");

    CJArray arrayB = new CJArray();
    root.put("b", arrayB);
    arrayB.add(new CJObject(Map.of("a", true)));
    arrayB.add(new CJObject(Map.of("b", true)));
    assertTrue(tree.containsAll(root));

    arrayB.add(false);
    assertFalse(tree.containsAll(root));
    arrayB.remove(arrayB.size() - 1);

    arrayB.add(new CJArray());
    assertFalse(tree.containsAll(root));
    arrayB.remove(arrayB.size() - 1);

    root.put("a", "hello");
    assertFalse(tree.containsAll(root));
  }


  @Test
  public void containsAllString() {
    assertFalse(tree.containsAll(CJString.create("hello")));
  }


  @Test
  public void copyArray() {
    CJArray root = new CJArray();
    root.add(new CJObject(Map.of("a", true, "b", false, "c", 1)));
    root.add(new CJObject(Map.of("c", 2)));
    root.add(new CJObject(Map.of("a", 2)));

    JsonArray copy = tree.copy(root);
    assertEquals("[{\"a\":true,\"b\":false},{},{\"a\":2}]", copy.toString());
  }


  @Test
  public void copyObject() {
    CJObject root = new CJObject();
    root.put("a", new CJObject(Map.of("a", 1, "b", 2)));
    root.put("b", new CJArray(List.of(
        Map.of("a", 1, "b", 2, "c", 3),
        Map.of("a", 1, "b", 2, "c", 3),
        Map.of("a", 1, "b", 2, "c", 3)
    )));

    JsonObject copy = tree.copy(root);
    assertEquals("{\"a\":{\"b\":2},\"b\":[{\"a\":1},{\"b\":2}]}", copy.toString());
  }


  @Test
  public void isParentOf() {
    assertTrue(tree.isParentOf(PointerFactory.create("/a/b/c/d")));
    assertTrue(tree.isParentOf(PointerFactory.create("/1/a/b")));
    assertFalse(tree.isParentOf(PointerFactory.create("/z")));
  }


  @Test
  public void remove() {
    CJObject root = new CJObject();
    root.put("a", new CJObject(Map.of("a", 1, "b", 2)));
    root.put("b", new CJArray(List.of(
        Map.of("a", 1, "b", 2, "c", 3),
        Map.of("a", 1, "b", 2, "c", 3),
        Map.of("a", 1, "b", 2, "c", 3)
    )));

    JsonObject copy = tree.remove(root);
    assertEquals("{\"a\":{\"a\":1},\"b\":[{\"b\":2,\"c\":3},{\"a\":1,\"c\":3},{\"a\":1,\"b\":2,\"c\":3}]}", copy.toString());
  }

}
