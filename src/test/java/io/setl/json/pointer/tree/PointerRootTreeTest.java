package io.setl.json.pointer.tree;

import static org.junit.Assert.*;

import javax.json.JsonArray;

import org.junit.Test;

import io.setl.json.builder.JArrayBuilder;

/**
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerRootTreeTest {

  @Test
  public void containsAll() {
    JsonArray array = new JArrayBuilder().add("a").add(1).build();
    assertTrue(PointerRootTree.ROOT.containsAll(array));
  }


  @Test
  public void copy() {
    JsonArray array = new JArrayBuilder().add("a").add(1).build();
    assertTrue(PointerRootTree.ROOT.containsAll(array));
  }


  @Test
  public void isParentOf() {
  }


  @Test
  public void remove() {
  }

}