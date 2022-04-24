package io.setl.json.exception;

import javax.json.JsonException;

/**
 * A pointer into an array structure specifies an unusable index.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class PointerIndexException extends JsonException {

  private final String path;

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


  public String getPath() {
    return path;
  }


  public int getSize() {
    return size;
  }

}
