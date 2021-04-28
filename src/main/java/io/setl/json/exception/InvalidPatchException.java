package io.setl.json.exception;

import javax.json.JsonException;

/**
 * The JSON specification of a patch was invalid.
 *
 * @author Simon Greatrix on 28/04/2021.
 */
public class InvalidPatchException extends JsonException {

  public InvalidPatchException(String message) {
    super(message);
  }

}
