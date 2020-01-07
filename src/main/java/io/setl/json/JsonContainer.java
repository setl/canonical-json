package io.setl.json;

/**
 * A JSON Container. Either an array or an object.
 *
 * @author Simon Greatrix on 2020-01-07.
 */
public interface JsonContainer extends JsonValue {

  boolean isArray();
}
