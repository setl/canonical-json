package io.setl.json.patch.key;

import io.setl.json.pointer.Pointer;

/**
 * An object property name as part of a pointer.
 */
public class ObjectKey extends Key {

  private final String escaped;


  public ObjectKey(Key parent, String key) {
    super(parent);
    escaped = Pointer.escapeKey(key);
  }


  @Override
  protected String getEscapedKey() {
    return escaped;
  }

}
