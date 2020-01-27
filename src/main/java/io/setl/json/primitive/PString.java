package io.setl.json.primitive;

import io.setl.json.JType;
import java.io.IOException;
import java.io.Writer;
import javax.json.JsonString;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PString extends PBase implements JsonString {

  /** Canonical form uses upper-case hexadecimal. */
  private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


  /**
   * Append a Unicode escape to a buffer.
   *
   * @param buf the buffer
   * @param ch  the character to escape
   */
  private static void appendUnicode(Appendable buf, char ch) throws IOException {
    buf.append("\\u").append(HEX[(ch >>> 12) & 15]).append(HEX[(ch >>> 8) & 15]).append(HEX[(ch >>> 4) & 15]).append(HEX[ch & 15]);
  }


  /**
   * Format text in its canonical JSON representation and append to the buffer.
   *
   * @param buf   the output buffer
   * @param input the text to format
   */
  public static void format(Appendable buf, String input) throws IOException {
    // opening quote
    buf.append('"');

    // track high surrogates for when they are followed by low surrogates
    char highSurrogate = '\0';

    // Loop through every character
    int l = input.length();
    for (int i = 0; i < l; i++) {
      char ch = input.charAt(i);
      if (Character.isHighSurrogate(ch)) {
        if (highSurrogate != '\0') {
          // previous character was an isolated high surrogate
          appendUnicode(buf, highSurrogate);
        }
        // got a high surrogate that might combine with the next character
        highSurrogate = ch;
      } else if (Character.isLowSurrogate(ch)) {
        if (highSurrogate != '\0') {
          // a high surrogate and a low surrogate so we have a valid pair.
          buf.append(highSurrogate).append(ch);
          highSurrogate = '\0';
        } else {
          // isolated low surrogate
          appendUnicode(buf, ch);
        }
      } else {
        // new character is not a surrogate
        if (highSurrogate != '\0') {
          // previous character was an isolated high surrogate
          appendUnicode(buf, highSurrogate);
          highSurrogate = '\0';
        }
        // Handle C0 escape codes
        if (ch < 32) {
          switch (ch) {
            case 8:
              buf.append("\\b");
              break;
            case 9:
              buf.append("\\t");
              break;
            case 10:
              buf.append("\\n");
              break;
            case 12:
              buf.append("\\f");
              break;
            case 13:
              buf.append("\\r");
              break;
            default:
              buf.append("\\u00").append(HEX[ch >>> 4]).append(HEX[ch & 0xf]);
              break;
          }
        } else if (ch == '\\') {
          // Reverse solidus must be escaped
          buf.append("\\\\");
        } else if (ch == '\"') {
          // Double quote must be escaped
          buf.append("\\\"");
        } else {
          // regular character
          buf.append(ch);
        }
      }
    }

    // Could have a trailing high surrogate
    if (highSurrogate != '\0') {
      appendUnicode(buf, highSurrogate);
    }

    // closing quote
    buf.append('"');
  }


  /**
   * Append a properly escaped canonical string to the provided buffer.
   *
   * @param buf   the buffer
   * @param value the value
   *
   * @return the passed in buffer
   */
  public static StringBuilder format(StringBuilder buf, String value) {
    try {
      format((Appendable) buf, value);
    } catch (IOException e) {
      throw new InternalError("IO Exception without I/O", e);
    }
    return buf;
  }


  /**
   * Create a properly escaped canonical representation of the provided value.
   *
   * @param value the value
   *
   * @return the canonical representation
   */
  public static String format(String value) {
    return format(new StringBuilder(), value).toString();
  }


  private final String value;


  public PString(String value) {
    this.value = value;
  }


  @Override
  public CharSequence getChars() {
    return value;
  }


  @Override
  public String getString() {
    return value;
  }


  @Override
  public JType getType() {
    return JType.STRING;
  }


  @Override
  public Object getValue() {
    return value;
  }


  @Override
  public ValueType getValueType() {
    return ValueType.STRING;
  }


  @Override
  public String toString() {
    return format(value);
  }


  @Override
  public void writeTo(Writer writer) throws IOException {
    format(writer, value);
  }
}
