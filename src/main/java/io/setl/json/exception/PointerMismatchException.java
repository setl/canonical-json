package io.setl.json.exception;

import javax.json.JsonException;
import javax.json.JsonValue.ValueType;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class PointerMismatchException extends JsonException {

  private static final long serialVersionUID = 1L;

  private final ValueType actual;

  private final ValueType expected;

  private final String pointer;


  /**
   * New exception.
   *
   * @param message  associated message
   * @param pointer  the pointer that failed
   * @param expected the expected value type in the structure
   * @param actual   the actual value type in the structure.
   */
  public PointerMismatchException(String message, String pointer, ValueType expected, ValueType actual) {
    super(String.format("%s [path=%s, expected=%s, actual=%s]", message, pointer, expected, actual));
    this.pointer = pointer;
    this.expected = expected;
    this.actual = actual;
  }


  /**
   * New exception for when the expected type could be an Object or an Array.
   *
   * @param message associated message
   * @param pointer the pointer that failed
   * @param actual  the actual value type in the structure.
   */
  public PointerMismatchException(String message, String pointer, ValueType actual) {
    super(String.format("%s [path=%s, expected=STRUCTURE, actual=%s]", message, pointer, actual));
    this.pointer = pointer;
    this.expected = null;
    this.actual = actual;
  }


  public ValueType getActual() {
    return actual;
  }


  public ValueType getExpected() {
    return expected;
  }


  public String getPointer() {
    return pointer;
  }
}
