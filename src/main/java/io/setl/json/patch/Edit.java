package io.setl.json.patch;

import javax.json.JsonArray;
import javax.json.JsonValue;

/**
 * Representation of an edit that can be applied to produce the new value
 * from the old value.
 *
 * @author Simon Greatrix
 */
public class Edit {

  public enum Change {
    ADD, REMOVE, REPLACE
  }



  /** The array index associated with this edit. */
  private final int index;

  /** The operation performed by this edit. */
  private final Change op;

  /** The value associated with this edit. */
  private final JsonValue value;


  /**
   * Create new edit.
   *
   * @param op    the operation to be performed
   * @param index the line number affected
   * @param value the associated value
   */
  Edit(Change op, int index, JsonValue value) {
    this.op = op;
    this.index = index;
    this.value = value;
  }


  /**
   * Apply this edit to the supplied list. Edits must be applied in order.
   *
   * @param list the list to edit
   */
  public void apply(JsonArray list) {
    switch (op) {
      case ADD:
        list.add(index, value);
        break;
      case REMOVE:
        list.remove(index);
        break;
      case REPLACE:
        list.set(index, value);
        break;
      default:
        throw new UnsupportedOperationException("Invalid operation: " + op);
    }
  }


  /**
   * The line number associated with this edit.
   *
   * @return the line number
   */
  public int getIndex() {
    return index;
  }


  /**
   * The operation associated with this edit.
   *
   * @return the operation code
   */
  public Change getOp() {
    return op;
  }


  /**
   * The value to be inserted, set or deleted by this edit.
   *
   * @return the associated value
   */
  public JsonValue getValue() {
    return value;
  }


  @Override
  public String toString() {
    return String.valueOf(index) + ' ' + op + ' ' + value;
  }
}
