package io.setl.json;

import javax.json.JsonStructure;

/**
 * A JSON Container. Either an array or an object.
 *
 * @author Simon Greatrix on 2020-01-07.
 */
public interface JContainer extends Primitive, JsonStructure {

  boolean isArray();
}
