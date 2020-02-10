package io.setl.json.patch.key;

import java.util.Objects;

import io.setl.json.pointer.ObjectPath;
import io.setl.json.pointer.ObjectTerminal;
import io.setl.json.pointer.PathElement;

public class ObjectKey extends Key {

  private final String key;


  public ObjectKey(Key parent, String key) {
    super(parent);
    this.key = key;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ObjectKey)) {
      return false;
    }
    ObjectKey that = (ObjectKey) o;
    return key.equals(that.key) && Objects.equals(parent, that.parent);
  }


  @Override
  public int hashCode() {
    return ((parent != null) ? parent.hashCode() : 0) * 31 + key.hashCode();
  }


  @Override
  protected PathElement makeElement(boolean isTerminal) {
    return isTerminal ? new ObjectTerminal(key) : new ObjectPath(key);
  }


  @Override
  public String toString() {
    return (parent != null ? parent.toString() : "") + "/" + key;
  }

}
