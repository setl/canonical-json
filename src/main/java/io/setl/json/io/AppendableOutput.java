package io.setl.json.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

import io.setl.json.exception.JsonIOException;

/**
 * An equivalent to a regular Appendable which throws JsonIOException when an IOException would occur.
 *
 * @author Simon Greatrix on 20/11/2020.
 */
public class AppendableOutput implements PrettyOutput {

  /** Output. */
  private final Appendable appendable;

  /** Maximum size of a small structure, not including the end marker. */
  private final int smallStructureLimit;

  /** Current level of indentation. */
  private int indent = 0;


  public AppendableOutput(Appendable appendable, int smallStructureLimit) {
    this.appendable = appendable;
    if (smallStructureLimit < 2) {
      this.smallStructureLimit = 0;
    } else {
      this.smallStructureLimit = smallStructureLimit-2;
    }
  }


  public PrettyOutput append(CharSequence csq) {
    try {
      appendable.append(csq);
    } catch (IOException exception) {
      throw new JsonIOException(exception);
    }
    return this;
  }


  @Override
  public PrettyOutput append(char[] csq, int start, int end) {
    for (int i = start; i < end; i++) {
      append(csq[i]);
    }
    return this;
  }


  public PrettyOutput append(CharSequence csq, int start, int end) {
    try {
      appendable.append(csq, start, end);
    } catch (IOException exception) {
      throw new JsonIOException(exception);
    }
    return this;
  }


  public PrettyOutput append(char c) {
    try {
      if (c < 3) {
        indent += (c - 1);
        appendable.append('\n');
        for (int i = 0; i < indent; i++) {
          appendable.append(' ').append(' ');
        }
      } else {
        appendable.append(c);
      }
      return this;
    } catch (IOException exception) {
      throw new JsonIOException(exception);
    }
  }


  @Override
  public PrettyOutput append(Special special) {
    PrettyBuffer buffer;
    switch (special) {
      case END_ARRAY:
        return append('\u0000').append(']');
      case END_OBJECT:
        return append('\u0000').append('}');
      case SEPARATOR:
        return append(',').append('\u0001');
      case START_ARRAY:
        if (smallStructureLimit > 0) {
          buffer = new PrettyBuffer(this, smallStructureLimit, Special.END_ARRAY);
          buffer.append('[').append('\u0002');
          return buffer;
        } else {
          return append('[').append('\u0002');
        }
      case START_OBJECT:
        if (smallStructureLimit > 0) {
          buffer = new PrettyBuffer(this, smallStructureLimit, Special.END_OBJECT);
          buffer.append('{').append('\u0002');
          return buffer;
        } else {
          return append('{').append('\u0002');
        }
      default:
        throw new InternalError("Unrecognised enumeration: " + special);
    }
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
  public PrettyOutput flush() {
    if (appendable instanceof Flushable) {
      try {
        ((Flushable) appendable).flush();
      } catch (IOException exception) {
        throw new JsonIOException(exception);
      }
    }
    return this;
  }

}
