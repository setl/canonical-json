package io.setl.json.exception;

import javax.json.JsonException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class PointerIndexException extends JsonException {

  public PointerIndexException(String message, String path, int size) {
    super(message + " [path=" + path + " size=" + size + "]");
  }
}
