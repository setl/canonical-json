package io.setl.json;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Simon Greatrix on 03/01/2020.
 */
public interface Writable {
  void writeTo(Writer writer) throws IOException;
}
