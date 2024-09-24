package io.setl.json.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.annotation.Nonnull;

/**
 * An OutputStream writer which uses UTF-8 encoding.
 *
 * @author Simon Greatrix on 03/01/2020.
 */
public class Utf8Writer extends Writer implements Appendable {

  /** The appendable. */
  private final Utf8Appendable appendable;


  /**
   * New instance writing to the output stream.
   *
   * @param output the output stream
   */
  public Utf8Writer(OutputStream output) {
    appendable = new Utf8Appendable(output);
  }


  @Override
  public void close() throws IOException {
    appendable.close();
  }


  /**
   * Close this writer, but DO NOT close the contained output stream.
   *
   * @throws IOException if the final character was an isolated high surrogate
   */
  public void finish() throws IOException {
    appendable.finish();
  }


  @Override
  public void flush() throws IOException {
    appendable.flush();
  }


  @Override
  public void write(@Nonnull char[] buffer, int offset, int length) throws IOException {
    appendable.append(buffer, offset, length);
  }

}
