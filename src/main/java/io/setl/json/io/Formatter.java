package io.setl.json.io;

import io.setl.json.primitive.CJBase;

/**
 * Formatter for JSON.
 *
 * @author Simon Greatrix on 20/11/2020.
 */
public interface Formatter {

  /** Finish the output and close the stream. */
  void close();

  /** Flush pending writes to the stream. */
  void flush();

  /**
   * Write a JSON value.
   *
   * @param value the value to write
   */
  void write(CJBase value);

  /** Write the end-of-array marker. */
  void writeArrayEnd();

  /** Write the start-of-array marker. */
  void writeArrayStart();

  /** Write a colon to separate a key from a value in an object. */
  void writeColon();

  /** Write a comma. */
  void writeComma();

  /**
   * Write an object key.
   *
   * @param key the key
   */
  void writeKey(String key);

  /** Write the end-of-object marker. */
  void writeObjectEnd();

  /** Write the start-of-object marker. */
  void writeObjectStart();

}
