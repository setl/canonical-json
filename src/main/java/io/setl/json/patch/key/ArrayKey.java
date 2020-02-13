package io.setl.json.patch.key;

public class ArrayKey extends Key {

  private final int index;


  public ArrayKey(Key parent, int index) {
    super(parent);
    this.index = index;
  }


  @Override
  protected String getEscapedKey() {
    return Integer.toString(index);
  }

}
