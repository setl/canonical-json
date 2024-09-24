package io.setl.json.exception;

/**
 * An exception thrown when an attempt is made to use an object that is not directly representable as JSON.
 *
 * @author Simon Greatrix on 10/08/2020.
 */
public class NotJsonException extends IllegalArgumentException {

  /**
   * New instance.
   *
   * @param value the value that cannot be represented as JSON
   */
  public NotJsonException(Object value) {
    super("Cannot represent instances of " + value.getClass() + " as JSON directly");
  }

}
