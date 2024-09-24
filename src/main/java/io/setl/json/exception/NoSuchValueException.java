package io.setl.json.exception;

import javax.json.JsonException;

/**
 * A JSON Structure did not contain the item required by a pointer.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class NoSuchValueException extends JsonException {

  /** The path that was not found. */
  private final String path;


  /**
   * Create a new exception.
   *
   * @param path the path that was not found
   */
  public NoSuchValueException(String path) {
    super("JSON Structure did not contain item at: " + path);
    this.path = path;
  }


  /**
   * Get the path that was not found.
   *
   * @return the path
   */
  public String getPath() {
    return path;
  }

}
