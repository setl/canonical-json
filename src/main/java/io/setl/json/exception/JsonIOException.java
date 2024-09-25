package io.setl.json.exception;

import java.io.IOException;
import jakarta.json.JsonException;

/**
 * A JSON Exception which wraps the checked IOException.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class JsonIOException extends JsonException {

  private static final long serialVersionUID = 1L;


  /**
   * New instance with cause.
   *
   * @param e the cause
   */
  public JsonIOException(IOException e) {
    super("I/O failure", e);
  }


  /**
   * Get the cause.
   *
   * @return the cause
   */
  public IOException cause() {
    return (IOException) getCause();
  }

}
