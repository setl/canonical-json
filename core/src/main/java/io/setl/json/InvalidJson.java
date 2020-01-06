package io.setl.json;

import java.io.IOException;

/**
 * Indicate that JSON cannot be parsed. The error message normally indicates the cause of the parsing failure.
 * 
 * @author Simon Greatrix
 *
 */
public class InvalidJson extends IOException {
  private static final long serialVersionUID = 1L;

  public InvalidJson(String message) {
    super(message);
  }
}
