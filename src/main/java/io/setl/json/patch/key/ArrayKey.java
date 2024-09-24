package io.setl.json.patch.key;

/**
 * An array index which makes part of a pointer for a patch.
 */
public class ArrayKey extends Key {

  private final int index;


  /**
   * New instance.
   *
   * @param parent the parent key
   * @param index  the index in an array
   */
  public ArrayKey(Key parent, int index) {
    super(parent);
    this.index = index;
  }


  @Override
  protected String getEscapedKey() {
    return Integer.toString(index);
  }

}
