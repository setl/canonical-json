package io.setl.json.io;

import io.setl.json.primitive.CJBase;

/**
 * @author Simon Greatrix on 20/11/2020.
 */
public interface Formatter {

  void close();

  void flush();

  void write(CJBase value);

  void writeArrayEnd();

  void writeArrayStart();

  void writeColon();

  void writeComma();

  void writeKey(String key);

  void writeObjectEnd();

  void writeObjectStart();

}
