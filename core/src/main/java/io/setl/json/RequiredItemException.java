package io.setl.json;

/**
 * Base exception used when the "safe" accessors of JsonObject and JsonArray encounter a problem.
 *
 * @author Simon Greatrix
 */
public class RequiredItemException extends RuntimeException {

  private static final long serialVersionUID = 1L;


  protected RequiredItemException(String msg) {
    super(msg);
  }

}
