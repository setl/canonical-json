package io.setl.json.patch.key;

/**
 * Parts of a pointer for a patch.
 */
public abstract class Key {

  protected final Key parent;


  protected Key(Key parent) {
    this.parent = parent;
  }


  protected abstract String getEscapedKey();


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
