package io.setl.json.patch.key;

/**
 * Parts of a pointer for a patch.
 */
public abstract class Key {

  /** The parent key. */
  protected final Key parent;


  /**
   * New instance with the specified parent.
   *
   * @param parent the parent key
   */
  protected Key(Key parent) {
    this.parent = parent;
  }


  /**
   * Get the key as a string appropriately escaped.
   *
   * @return the key in string form
   */
  protected abstract String getEscapedKey();


  /**
   * Append this key and its parents to a string builder.
   *
   * @param builder the builder to append to
   *
   * @return the builder
   */
  protected StringBuilder toString(StringBuilder builder) {
    if (parent != null) {
      parent.toString(builder);
    }
    return builder.append('/').append(getEscapedKey());
  }


  @Override
  public String toString() {
    return toString(new StringBuilder()).toString();
  }

}
