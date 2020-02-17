package io.setl.json.pointer.tree;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
    PointerTree tree = builder.add("/a/b").add("/a/c/-/a").add("/z").add("/a/a").build();
    assertTrue(tree instanceof PointerTreeImpl);
  }

}