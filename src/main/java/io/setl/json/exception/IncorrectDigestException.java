package io.setl.json.exception;

import javax.json.JsonException;

/**
 * Exception thrown when a digest test in a patch fails.
 *
 * @author Simon Greatrix on 27/02/2020.
 */
public class IncorrectDigestException extends JsonException {

  private static final long serialVersionUID = 1L;


  public IncorrectDigestException(String message) {
    super(message);
  }

}
