package io.setl.json.exception;

import javax.json.JsonException;

/**
 * A JSON Structure did not contain the item required by a pointer.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class NoSuchValueException extends JsonException {

  private final String path;


  public NoSuchValueException(String path) {
    super("JSON Structure did not contain item at: " + path);
    this.path = path;
  }


  public String getPath() {
    return path;
  }

}
