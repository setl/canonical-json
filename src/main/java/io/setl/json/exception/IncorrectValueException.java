package io.setl.json.exception;

import javax.json.JsonException;

/**
 * @author Simon Greatrix on 27/02/2020.
 */
public class IncorrectValueException extends JsonException {

  private static final long serialVersionUID = 1L;


  public IncorrectValueException(String message) {
    super(message);
  }

}
