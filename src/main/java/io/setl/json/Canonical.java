package io.setl.json;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * Correctly present text and numeric data in the canonical form.
 *
 * @author Simon Greatrix
 */
public class Canonical {

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
   * Produce the canonical representation of a String, including the wrapping quotes.
   *
   * @param input the string to represent
   *
   * @return the canonical representation.
   */
  public static String format(String input) {
    StringBuilder buf = new StringBuilder();
    try {
      format(buf, input);
    } catch (IOException e) {
      throw new InternalError("IO Exception without I/O", e);
    }
    return buf.toString();
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
   * Convert a number to its canonical JSON representation.
   *
   * @param number the number
   *
   * @return the representation
   */
  public static String format(Number number) {
    StringBuilder buf = new StringBuilder();
    try {
      format(buf, number);
    } catch (IOException e) {
      throw new InternalError("IO Exception without I/O", e);
    }
    return buf.toString();
  }


  /**
   * Append the canonically formatter number to the buffer.
   *
   * @param buf    the output buffer
   * @param number the number
   */
  public static void format(Appendable buf, Number number) throws IOException {
    // Standard Java integers all provide the canonical representation naturally.
    if (number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte) {
      buf.append(number.toString());
      return;
    }

    // Should be a Double, Float, or BigDecimal. We want BigDecimal.
    BigDecimal bigDecimal;
    if (number instanceof BigDecimal) {
      bigDecimal = (BigDecimal) number;
    } else {
      // Special handling for NaN and infinity.
      if ((number instanceof Double || number instanceof Float)
          && (Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue()))
      ) {
        buf.append('\"').append(number.toString()).append('\"');
        return;
      }
      // Going via the string representation is recommended practice
      bigDecimal = new BigDecimal(number.toString());
    }

    // Handle zero
    if (bigDecimal.signum() == 0) {
      buf.append('0');
      return;
    }

    // Strip trailing zeros and see if we have an integer
    bigDecimal = bigDecimal.stripTrailingZeros();
    if (bigDecimal.scale() <= 0) {
      // this is an integer
      buf.append(bigDecimal.toBigIntegerExact().toString(10));
      return;
    }

    // It's a floating point number. First deal with a leading minus sign.
    String sign;
    if (bigDecimal.signum() == 1) {
      sign = "";
    } else {
      sign = "-";
      bigDecimal = bigDecimal.negate();
    }

    // Get the digits and we will insert the decimal separator and append the exponent.
    String unscaled = bigDecimal.unscaledValue().toString(10);
    if (unscaled.length() == 1) {
      // A value like "0.03" has an unscaled value of "3" but the canonical representation requires a non-empty fractional part, so we have to add it.
      unscaled = unscaled + ".0";
    } else {
      // insert the decimal separator just after the first digit
      unscaled = unscaled.substring(0, 1) + "." + unscaled.substring(1);
    }

    // use the scale and precision to calculate the correct exponent
    int scale = bigDecimal.scale();
    int precision = bigDecimal.precision();
    buf.append(sign).append(unscaled).append('E').append(Integer.toString(precision - scale - 1));
  }


  /**
   * Output the JSON using UTF-8 handling isolated surrogates.
   *
   * @param output    the output stream
   * @param primitive the primitive to output
   */
  public static void stream(OutputStream output, Primitive primitive) throws IOException {
    String text = primitive.toString();
    output.write(text.getBytes(StandardCharsets.UTF_8));
  }
}
