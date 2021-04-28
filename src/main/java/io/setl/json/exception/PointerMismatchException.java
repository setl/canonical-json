package io.setl.json.exception;

import javax.json.JsonException;
import javax.json.JsonValue.ValueType;

/**
 * A pointer cannot be used on the specified JSON structure.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class PointerMismatchException extends JsonException {

  private static final long serialVersionUID = 1L;


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
  }

}
