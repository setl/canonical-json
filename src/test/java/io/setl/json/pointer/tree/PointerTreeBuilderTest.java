package io.setl.json.pointer.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.setl.json.pointer.PointerFactory;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerTreeBuilderTest {

  @Test
  public void buildEmpty() {
    PointerTreeBuilder builder = new PointerTreeBuilder();
    PointerTree tree = builder.build();
    assertTrue(tree instanceof PointerEmptyTree);
  }


  @Test
  public void buildRoot() {
    PointerTreeBuilder builder = new PointerTreeBuilder();
    PointerTree tree = builder.add("/a/b").add("/a/c/-/a").add("").add("/a/a").build();
    assertTrue(tree instanceof PointerRootTree);
  }


  @Test
  public void buildTree() {
    PointerTreeBuilder builder = new PointerTreeBuilder();
    PointerTree tree = builder.add("/a/b").add("/a/c/-/a").add("/z").add("/z/d").add("/a/a").build();
    assertTrue(tree instanceof PointerTreeImpl);

    PointerTreeImpl impl = (PointerTreeImpl) tree;
    assertEquals(4, impl.getPointers().size());
    assertTrue(impl.getPointers().contains(PointerFactory.create("/a/b")));
    assertTrue(impl.getPointers().contains(PointerFactory.create("/a/c/-/a")));
    assertTrue(impl.getPointers().contains(PointerFactory.create("/z")));
    assertFalse(impl.getPointers().contains(PointerFactory.create("/z/d")));
    assertTrue(impl.getPointers().contains(PointerFactory.create("/a/a")));
  }

}
