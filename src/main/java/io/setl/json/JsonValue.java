package io.setl.json;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public interface JsonValue {

  Type getType();

  void writeTo(Writer writer) throws IOException;
}
