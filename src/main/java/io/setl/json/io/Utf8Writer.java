package io.setl.json.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.annotation.Nonnull;

/**
 * @author Simon Greatrix on 03/01/2020.
 */
public class Utf8Writer extends Writer implements Appendable {

  private final Utf8Appendable appendable;


  public Utf8Writer(OutputStream output) {
    appendable = new Utf8Appendable(output);
  }


  @Override
  public void close() throws IOException {
    appendable.close();
  }


  @Override
  public void flush() throws IOException {
    appendable.flush();
  }


  @Override
  public void write(@Nonnull char[] cbuf, int off, int len) throws IOException {
    appendable.append(cbuf, off, len);
  }
}
