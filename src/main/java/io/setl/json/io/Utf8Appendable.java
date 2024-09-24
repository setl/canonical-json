package io.setl.json.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An appendable that writes UTF-8 encoded characters to a stream.
 *
 * @author Simon Greatrix on 03/01/2020.
 */
public class Utf8Appendable implements Appendable {

  /** The output stream. */
  private final OutputStream output;

  /** The high surrogate if the last character written was such. Used to detect isolated surrogates. */
  private char highSurrogate = 0;


  /**
   * New instance.
   *
   * @param output the output stream
   */
  public Utf8Appendable(OutputStream output) {
    this.output = output;
  }


  @Override
  public Appendable append(CharSequence csq) throws IOException {
    return append(csq, 0, csq.length());
  }


  @Override
  public Appendable append(CharSequence csq, int start, int end) throws IOException {
    for (int i = start; i < end; i++) {
      write(csq.charAt(i));
    }
    return this;
  }


  @Override
  public Appendable append(char c) throws IOException {
    write(c);
    return this;
  }


  /**
   * Append part of a character array.
   *
   * @param buffer the array
   * @param offset the offset into the array
   * @param length the number of characters to write
   *
   * @throws IOException if the wrapped output stream fails
   */
  public void append(char[] buffer, int offset, int length) throws IOException {
    int e = offset + length;
    for (int i = offset; i < e; i++) {
      write(buffer[i]);
    }
  }


  /**
   * Close the output.
   *
   * @throws IOException if the wrapped output stream fails to close
   */
  public void close() throws IOException {
    finish();
    output.close();
  }


  /**
   * Close this writer, but DO NOT close the contained output stream.
   *
   * @throws IOException if the final character was an isolated high surrogate
   */
  public void finish() throws IOException {
    if (highSurrogate != 0) {
      throw new IOException("Isolated high surrogate");
    }
  }


  /**
   * Flush the output.
   *
   * @throws IOException if the attempt to flush the output fails
   */
  public void flush() throws IOException {
    output.flush();
  }


  private void write(int cp) throws IOException {
    if (cp < 0x80) {
      output.write(cp);
      return;
    }

    if (cp < 0x800) {
      output.write(0b1100_0000 | (cp >>> 6));
      output.write(0b1000_0000 | (cp & 0x3f));
      return;
    }

    if (cp < 0x1_0000) {
      output.write(0b1110_0000 | (cp >>> 12));
      output.write(0b1000_0000 | ((cp >>> 6) & 0x3f));
      output.write(0b1000_0000 | (cp & 0x3f));
      return;
    }

    output.write(0b1111_0000 | (cp >>> 18));
    output.write(0b1000_0000 | ((cp >>> 12) & 0x3f));
    output.write(0b1000_0000 | ((cp >>> 6) & 0x3f));
    output.write(0b1000_0000 | (cp & 0x3f));
  }


  private void write(char ch) throws IOException {
    if (Character.isHighSurrogate(ch)) {
      if (highSurrogate != 0) {
        throw new IOException("Repeated high surrogate");
      }
      highSurrogate = ch;
      return;
    }
    if (Character.isLowSurrogate(ch)) {
      if (highSurrogate == 0) {
        throw new IOException("Isolated low surrogate");
      }
      write(Character.toCodePoint(highSurrogate, ch));
      highSurrogate = 0;
      return;
    }
    if (highSurrogate != 0) {
      throw new IOException("Isolated high surrogate");
    }

    write((int) ch);
  }

}
