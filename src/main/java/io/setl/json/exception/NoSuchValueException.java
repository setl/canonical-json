package io.setl.json.exception;

import javax.json.JsonException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class NoSuchValueException extends JsonException {

  public NoSuchValueException(String path) {
    super("JSON Structure did not contain item at: " + path);
  }

  public NoSuchValueException(String path, Throwable cause) {
    super("JSON Structure did not contain item at: " + path, cause);
  }
}
