package io.setl.json.exception;

import java.io.IOException;
import javax.json.stream.JsonLocation;

/**
 * Indicate that JSON cannot be parsed. The error message normally indicates the cause of the parsing failure.
 *
 * @author Simon Greatrix
 */
public class InvalidJsonException extends IOException {

  private static final long serialVersionUID = 1L;

  private final JsonLocation location;


  public InvalidJsonException(String message, JsonLocation location) {
    super(message);
    this.location = location;
  }


  public JsonLocation getLocation() {
    return location;
  }
}
