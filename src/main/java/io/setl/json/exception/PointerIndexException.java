package io.setl.json.exception;

import jakarta.json.JsonException;

/**
 * A pointer into an array structure specifies an unusable index.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class PointerIndexException extends JsonException {

  /** The path through the array. */
  private final String path;

  /** The array size. */
  private final int size;


  /**
   * New instance.
   *
   * @param message the error message
   * @param path    the path which referred to the array structure
   * @param size    the actual size of the array
   */
  public PointerIndexException(String message, String path, int size) {
    super(message + " Path is " + path + ", but array size is " + size + ".");
    this.path = path;
    this.size = size;
  }


  /**
   * Get the JSON path through the array.
   *
   * @return the path to the array
   */
  public String getPath() {
    return path;
  }


  /**
   * Get the size of the array.
   *
   * @return the array size
   */
  public int getSize() {
    return size;
  }

}
