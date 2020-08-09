package io.setl.json.pointer.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.Before;
import org.junit.Test;

import io.setl.json.JArray;
import io.setl.json.JCanonicalObject;
import io.setl.json.JObject;
import io.setl.json.pointer.JPointerFactory;
import io.setl.json.primitive.PString;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerTreeImplTest {

  PointerTree tree;


  @Before
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
    JArray root = new JArray();
    // if we don't ask for anything it is all OK
    assertTrue(tree.containsAll(root));

    root.add(new JCanonicalObject(Map.of("a", 1)));
    assertTrue(tree.containsAll(root));
  }


  @Test
  public void containsAllObject() {
    JObject root = new JCanonicalObject();
    // if we don't ask for anything it is all OK
    assertTrue(tree.containsAll(root));

    JObject objectA = new JCanonicalObject();
    root.put("a", objectA);
    objectA.put("b", 1);
    assertTrue(tree.containsAll(root));

    objectA.put("b", new JCanonicalObject(Map.of("x", "y")));
    assertTrue(tree.containsAll(root));

    objectA.put("b", new JArray(List.of(1, 2, 3)));
    assertTrue(tree.containsAll(root));

    // Add something that is not OK
    objectA.put("z", JsonValue.TRUE);
    assertFalse(tree.containsAll(root));
    objectA.remove("z");

    JArray arrayB = new JArray();
    root.put("b", arrayB);
    arrayB.add(new JCanonicalObject(Map.of("a", true)));
    arrayB.add(new JCanonicalObject(Map.of("b", true)));
    assertTrue(tree.containsAll(root));

    arrayB.add(false);
    assertFalse(tree.containsAll(root));
    arrayB.remove(arrayB.size() - 1);

    arrayB.add(new JArray());
    assertFalse(tree.containsAll(root));
    arrayB.remove(arrayB.size() - 1);

    root.put("a", "hello");
    assertFalse(tree.containsAll(root));
  }


  @Test
  public void containsAllString() {
    assertFalse(tree.containsAll(PString.create("hello")));
  }


  @Test
  public void copyArray() {
    JArray root = new JArray();
    root.add(new JCanonicalObject(Map.of("a", true, "b", false, "c", 1)));
    root.add(new JCanonicalObject(Map.of("c", 2)));
    root.add(new JCanonicalObject(Map.of("a", 2)));

    JsonArray copy = tree.copy(root);
    assertEquals("[{\"a\":true,\"b\":false},{},{\"a\":2}]", copy.toString());
  }


  @Test
  public void copyObject() {
    JObject root = new JCanonicalObject();
    root.put("a", new JCanonicalObject(Map.of("a", 1, "b", 2)));
    root.put("b", new JArray(List.of(
        Map.of("a", 1, "b", 2, "c", 3),
        Map.of("a", 1, "b", 2, "c", 3),
        Map.of("a", 1, "b", 2, "c", 3)
    )));

    JsonObject copy = tree.copy(root);
    assertEquals("{\"a\":{\"b\":2},\"b\":[{\"a\":1},{\"b\":2}]}", copy.toString());
  }


  @Test
  public void isParentOf() {
    assertTrue(tree.isParentOf(JPointerFactory.create("/a/b/c/d")));
    assertTrue(tree.isParentOf(JPointerFactory.create("/1/a/b")));
    assertFalse(tree.isParentOf(JPointerFactory.create("/z")));
  }


  @Test
  public void remove() {
    JObject root = new JCanonicalObject();
    root.put("a", new JCanonicalObject(Map.of("a", 1, "b", 2)));
    root.put("b", new JArray(List.of(
        Map.of("a", 1, "b", 2, "c", 3),
        Map.of("a", 1, "b", 2, "c", 3),
        Map.of("a", 1, "b", 2, "c", 3)
    )));

    JsonObject copy = tree.remove(root);
    assertEquals("{\"a\":{\"a\":1},\"b\":[{\"b\":2,\"c\":3},{\"a\":1,\"c\":3},{\"a\":1,\"b\":2,\"c\":3}]}", copy.toString());
  }

}