package io.setl.json.patch.key;

import java.util.Objects;

import io.setl.json.pointer.ArrayPath;
import io.setl.json.pointer.ArrayTerminal;
import io.setl.json.pointer.PathElement;

public class ArrayKey extends Key {

  private final int index;


  public ArrayKey(Key parent, int index) {
    super(parent);
    this.index = index;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ArrayKey)) {
      return false;
    }
    // By design, the index does not participate in equality.
    ArrayKey that = (ArrayKey) o;
    return Objects.equals(parent, that.parent);
  }


  @Override
  public int hashCode() {
    // By design, the index does not participate in equality.
    return ((parent != null) ? parent.hashCode() : 0) * 31 + 1031;
  }


  protected PathElement makeElement(boolean isTerminal) {
    return isTerminal ? new ArrayTerminal(index) : new ArrayPath(index);
  }


  @Override
  public String toString() {
    return (parent != null ? parent.toString() : "") + "/" + index;
  }

}
