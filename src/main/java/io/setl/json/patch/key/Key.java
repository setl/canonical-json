package io.setl.json.patch.key;

import io.setl.json.pointer.JPointer;
import io.setl.json.pointer.PathElement;

public abstract class Key {

  protected final Key parent;


  protected Key(Key parent) {
    this.parent = parent;
  }


  protected PathElement[] asElement(boolean isTerminal) {
    PathElement[] result = new PathElement[2];
    PathElement me = makeElement(isTerminal);
    result[1] = me;
    if (getParent() != null) {
      PathElement[] parentElement = getParent().asElement(false);
      result[0] = parentElement[0];
      parentElement[1].setChild(me);
    } else {
      result[0] = me;
    }
    return result;
  }


  public JPointer asPointer() {
    return new JPointer(asElement(true)[0]);
  }


  Key getParent() {
    return parent;
  }


  protected abstract PathElement makeElement(boolean isTerminal);

}
