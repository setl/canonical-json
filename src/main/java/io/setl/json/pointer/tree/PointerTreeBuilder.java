package io.setl.json.pointer.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.setl.json.pointer.JsonExtendedPointer;
import io.setl.json.pointer.PointerFactory;

/**
 * Builder of pointer trees.
 *
 * @author Simon Greatrix on 17/02/2020.
 */
public class PointerTreeBuilder {

  /** All the effective pointers. There is no pointer in this list which also has a parent in this list. */
  private final List<JsonExtendedPointer> pointers = new ArrayList<>();

  /** Have we seen the "" pointer?. */
  private boolean seenRoot = false;


  public PointerTreeBuilder add(String path) {
    return add(PointerFactory.create(path));
  }


  /**
   * Add the provided pointer to this tree.
   *
   * @param newPointer the new pointer
   *
   * @return this
   */
  public PointerTreeBuilder add(JsonExtendedPointer newPointer) {
    if (newPointer.getPath().isEmpty()) {
      // the new pointer is the root
      pointers.clear();
      seenRoot = true;
    }
    // If we've seen the root, nothing else matters
    if (seenRoot) {
      return this;
    }

    for (JsonExtendedPointer pointer : pointers) {
      if (pointer.isParentOf(newPointer)) {
        // parent is present, so do not add
        return this;
      }
    }

    // Remove any children of the new pointer
    pointers.removeIf(newPointer::isParentOf);
    pointers.add(newPointer);
    return this;
  }


  /**
   * Build the pointer tree.
   *
   * @return the tree
   */
  public PointerTree build() {
    if (seenRoot) {
      return PointerRootTree.INSTANCE;
    }

    if (pointers.isEmpty()) {
      return PointerEmptyTree.INSTANCE;
    }

    pointers.sort((Comparator.comparing(JsonExtendedPointer::getPath)));

    return new PointerTreeImpl(Collections.unmodifiableList(pointers));
  }

}
