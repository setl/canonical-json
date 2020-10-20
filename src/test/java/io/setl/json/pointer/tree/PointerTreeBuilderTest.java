package io.setl.json.pointer.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.setl.json.pointer.JPointerFactory;

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
    assertTrue(impl.getPointers().contains(JPointerFactory.create("/a/b")));
    assertTrue(impl.getPointers().contains(JPointerFactory.create("/a/c/-/a")));
    assertTrue(impl.getPointers().contains(JPointerFactory.create("/z")));
    assertFalse(impl.getPointers().contains(JPointerFactory.create("/z/d")));
    assertTrue(impl.getPointers().contains(JPointerFactory.create("/a/a")));
  }

}
