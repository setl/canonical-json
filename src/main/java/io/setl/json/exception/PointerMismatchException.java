package io.setl.json.exception;

import javax.annotation.Nullable;
import javax.json.JsonException;
import javax.json.JsonValue.ValueType;

/**
 * A pointer cannot be used on the specified JSON structure.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class PointerMismatchException extends JsonException {

  private static final long serialVersionUID = 1L;

  /** The actual type. */
  private final ValueType actual;

  /** The expected type. */
  private final ValueType expected;

  /** The path that failed. */
  private final String path;


  /**
   * New exception.
   *
   * @param message  associated message
   * @param path     the pointer that failed
   * @param expected the expected value type in the structure
   * @param actual   the actual value type in the structure.
   */
  public PointerMismatchException(String message, String path, ValueType expected, ValueType actual) {
    super(String.format("%s [path=%s, expected=%s, actual=%s]", message, path, expected, actual));
    this.path = path;
    this.expected = expected;
    this.actual = actual;
  }


  /**
   * New exception for when the expected type could be an Object or an Array.
   *
   * @param message associated message
   * @param path    the pointer that failed
   * @param actual  the actual value type in the structure.
   */
  public PointerMismatchException(String message, String path, ValueType actual) {
    super(String.format("%s [path=%s, expected=STRUCTURE, actual=%s]", message, path, actual));
    this.path = path;
    expected = null;
    this.actual = actual;
  }


  /**
   * Get the actual type.
   *
   * @return the actual type
   */
  public ValueType getActual() {
    return actual;
  }


  /**
   * Get the expected type. If null, implies it could be either an OBJECT or an ARRAY.
   *
   * @return the expected type (can be null if the expected type was only known to be a structure)
   */
  @Nullable
  public ValueType getExpected() {
    return expected;
  }


  /**
   * Get the JSON path that failed.
   *
   * @return the path that failed
   */
  public String getPath() {
    return path;
  }

}
