package io.setl.json.io;

import javax.json.stream.JsonGenerationException;

/**
 * A circular char buffer used by the PrettyFormatter to limit the expansion of small structures.
 *
 * @author Simon Greatrix on 19/11/2020.
 */
class PrettyBuffer implements PrettyOutput {

  private final char[] chars;

  private final Special endsWith;

  private final PrettyOutput parent;

  private final int size;

  private int pos;


  PrettyBuffer(PrettyOutput parent, int size, Special endsWith) {
    this.parent = parent;
    this.size = size;
    this.endsWith = endsWith;
    pos = 0;
    chars = new char[size];
  }


  @Override
  public PrettyOutput append(CharSequence csq) {
    return append(csq, 0, csq.length());
  }


  @Override
  public PrettyOutput append(char[] csq, int start, int end) {
    int length = end - start;
    if (pos + length <= size) {
      for (int i = start; i < end; i++) {
        chars[pos] = csq[i];
        pos++;
      }
      return this;
    }

    PrettyOutput ancestor = parent.append(chars, 0, pos);
    return ancestor.append(csq, start, end);
  }


  @Override
  public PrettyOutput append(CharSequence csq, int start, int end) {
    int length = end - start;
    if (pos + length <= size) {
      for (int i = start; i < end; i++) {
        chars[pos] = csq.charAt(i);
        pos++;
      }
      return this;
    }

    PrettyOutput ancestor = parent.append(chars, 0, pos);
    return ancestor.append(csq, start, end);
  }


  @Override
  public PrettyOutput append(char c) {
    if (pos + 1 <= size) {
      chars[pos] = c;
      pos++;
      return this;
    }

    PrettyOutput ancestor = parent.append(chars, 0, pos);
    return ancestor.append(c);
  }


  @Override
  public PrettyOutput append(Special special) {
    if (special == endsWith) {
      char endSymbol = endsWith == Special.END_ARRAY ? ']' : '}';

      if (pos == 2) {
        // special rule for empty structures
        return parent.append(chars[0]).append(endSymbol);
      }

      for (int i = 0; i < pos; i++) {
        if (chars[i] < 3) {
          chars[i] = ' ';
        }
      }

      return parent.append(chars, 0, pos).append(' ').append(endSymbol);
    }

    PrettyBuffer buffer;
    switch (special) {
      case END_ARRAY:
      case END_OBJECT:
        throw new IllegalStateException(special + " encountered without matching start. Expected: " + endsWith);
      case SEPARATOR:
        return append(',').append('\u0001');
      case START_ARRAY:
        buffer = new PrettyBuffer(this, size, Special.END_ARRAY);
        buffer.append('[').append('\u0002');
        return buffer;
      case START_OBJECT:
        buffer = new PrettyBuffer(this, size, Special.END_OBJECT);
        buffer.append('{').append('\u0002');
        return buffer;
      default:
        throw new InternalError("Unrecognised enumeration: " + special);
    }
  }


  @Override
  public void close() {
    throw new JsonGenerationException("Generation terminated within structure");
  }


  @Override
  public PrettyOutput flush() {
    PrettyOutput ancestor = parent.append(chars, 0, pos);
    return ancestor.flush();
  }

}
