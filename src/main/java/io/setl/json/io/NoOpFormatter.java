package io.setl.json.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

import io.setl.json.exception.JsonIOException;
import io.setl.json.primitive.CJBase;
import io.setl.json.primitive.CJString;

/**
 * Outputs the JSON with no optional white-space.
 *
 * @author Simon Greatrix on 18/11/2020.
 */
public class NoOpFormatter implements Formatter {

  /** The appendable to output to. */
  protected final Appendable appendable;


  /**
   * New instance using the specified appendable.
   *
   * @param appendable the appendable
   */
  public NoOpFormatter(Appendable appendable) {
    this.appendable = appendable;
  }


  @Override
  public void close() {
    if (appendable instanceof Closeable) {
      try {
        ((Closeable) appendable).close();
      } catch (IOException exception) {
        throw new JsonIOException(exception);
      }
    }
  }


  @Override
  public void flush() {
    if (appendable instanceof Flushable) {
      try {
        ((Flushable) appendable).flush();
      } catch (IOException exception) {
        throw new JsonIOException(exception);
      }
    }
  }


  @Override
  public void write(CJBase value) {
    try {
      value.writeTo(appendable);
    } catch (IOException exception) {
      throw new JsonIOException(exception);
    }
  }


  private void write(char ch) {
    try {
      appendable.append(ch);
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  @Override
  public void writeArrayEnd() {
    write(']');
  }


  @Override
  public void writeArrayStart() {
    write('[');
  }


  @Override
  public void writeColon() {
    write(':');
  }


  @Override
  public void writeComma() {
    write(',');
  }


  @Override
  public void writeKey(String key) {
    try {
      CJString.format(appendable, key);
    } catch (IOException exception) {
      throw new JsonIOException(exception);
    }
  }


  @Override
  public void writeObjectEnd() {
    write('}');
  }


  @Override
  public void writeObjectStart() {
    write('{');
  }

}
