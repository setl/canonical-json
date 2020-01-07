package io.setl.json.exception;

/**
 * Base exception used when the "safe" accessors of JObject and JArray encounter a problem.
 *
 * @author Simon Greatrix
 */
public class RequiredItemException extends RuntimeException {

  private static final long serialVersionUID = 1L;


  protected RequiredItemException(String msg) {
    super(msg);
  }

}
