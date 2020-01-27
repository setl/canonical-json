package io.setl.json.exception;

import java.io.IOException;
import javax.json.JsonException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class JsonIOException extends JsonException {

  private static final long serialVersionUID = 1L;


  public JsonIOException(IOException e) {
    super("I/O failure", e);
  }

}
