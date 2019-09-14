package com.pippsford.json;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Generate canonical JSON as a binary stream. Note that simply writing out the JSON's textual representation as UTF-8 <b>DOES NOT</b> produce the correct
 * output for surrogates, as the Java CharsetEncoder for UTF-8 considers isolated surrogates to be malformed input.
 * 
 * @author Simon
 *
 */
public class Generator {

  /** Canonical form uses upper-case hexadecimal. */
  private static final char[] HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };


  /**
   * Append a Unicode escape to a buffer.
   * 
   * @param buf
   *          the buffer
   * @param ch
   *          the character to escape
   */
  private static void appendUnicode(StringBuilder buf, char ch) {
    buf.append("\\u").append(HEX[(ch >>> 12) & 15]).append(HEX[(ch >>> 8) & 15]).append(HEX[(ch >>> 4) & 15]).append(HEX[ch & 15]);
  }


  /**
   * Produce the canonical representation of a String, including the wrapping quotes.
   * 
   * @param input
   *          the string to represent
   * @return the canonical representation.
   */
  static String escapeString(String input) {
    StringBuilder buf = new StringBuilder();
    // opening quote
    buf.append('"');

    // track high surrogates for when they are followed by low surrogates
    char highSurrogate = '\0';

    // Loop through every character
    int l = input.length();
    for(int i = 0;i < l;i++) {
      char ch = input.charAt(i);
      if( Character.isHighSurrogate(ch) ) {
        if( highSurrogate != '\0' ) {
          // previous character was an isolated high surrogate
          appendUnicode(buf, highSurrogate);
        }
        // got a high surrogate that might combine with the next character
        highSurrogate = ch;
      } else if( Character.isLowSurrogate(ch) ) {
        if( highSurrogate != '\0' ) {
          // a high surrogate and a low surrogate so we have a valid pair.
          buf.append(highSurrogate).append(ch);
          highSurrogate = '\0';
        } else {
          // isolated low surrogate
          appendUnicode(buf, ch);
        }
      } else {
        // new character is not a surrogate
        if( highSurrogate != '\0' ) {
          // previous character was an isolated high surrogate
          appendUnicode(buf, highSurrogate);
          highSurrogate = '\0';
        }
        // Handle C0 escape codes
        if( ch < 32 ) {
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
        } else if( ch == '\\' ) {
          // Reverse solidus must be escaped
          buf.append("\\\\");
        } else if( ch == '\"' ) {
          // Double quote must be escaped
          buf.append("\\\"");
        } else {
          // regular character
          buf.append(ch);
        }
      }
    }

    // Could have a trailing high surrogate
    if( highSurrogate != '\0' ) {
      appendUnicode(buf, highSurrogate);
    }

    // closing quote
    buf.append('"');
    return buf.toString();
  }


  static String escapeNumber(Number number) {
    // Standard Java integers all provide the canonical representation naturally.
    if( number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte ) {
      return number.toString();
    }

    // Should be a Double, Float, or BigDecimal. We want BigDecimal
    BigDecimal bigDecimal;
    if( number instanceof BigDecimal ) {
      bigDecimal = (BigDecimal) number;
    } else {
      // Going via the string representation is recommended practice
      bigDecimal = new BigDecimal(number.toString());
    }

    // Handle zero
    if( bigDecimal.signum() == 0 ) {
      return "0";
    }

    // Strip trailing zeros and see if we have an integer
    bigDecimal = bigDecimal.stripTrailingZeros();
    if( bigDecimal.scale() <= 0 ) {
      // this is an integer
      return bigDecimal.toBigIntegerExact().toString(10);
    }

    // It's a floating point number. First deal with a leading minus sign.
    String sign;
    if( bigDecimal.signum() == 1 ) {
      sign = "";
    } else {
      sign = "-";
      bigDecimal = bigDecimal.negate();
    }

    // Get the digits and we will insert the decimal separator and append the exponent.
    String unscaled = bigDecimal.unscaledValue().toString(10);
    if( unscaled.length() == 1 ) {
      // A value like "0.03" has an unscaled value of "3" but the canonical representation requires a non-empty fractional part, so we have to add it.
      unscaled = unscaled + ".0";
    } else {
      // insert the decimal separator just after the first digit
      unscaled = unscaled.substring(0, 1) + "." + unscaled.substring(1);
    }

    // use the scale and precision to calculate the correct exponent
    int scale = bigDecimal.scale();
    int precision = bigDecimal.precision();
    return sign + unscaled + "E" + (precision - scale - 1);
  }


  /**
   * Output the JSON using UTF-8 handling isolated surrogates.
   * 
   * @param output
   *          the output stream
   * @param primitive
   *          the primitive to output
   * @throws IOException
   */
  public static void stream(OutputStream output, Primitive primitive) throws IOException {
    String text = primitive.toString();

    char highSurrogate = '\0';
    boolean sawHighSurrogate = false;

    int l = text.length();
    for(int i = 0;i < l;i++) {
      char ch = text.charAt(i);
      if( sawHighSurrogate ) {
        sawHighSurrogate = false;
        if( Character.isLowSurrogate(ch) ) {
          // Good surrogate pair.
          utf8(output, Character.toCodePoint(highSurrogate, ch));
          continue;
        }

        // previous high surrogate was an isolate
        utf8(output, highSurrogate);
      }

      // if we get a high surrogate, hold it to compare with the next character. NB. No primitive can create a String which has an isolated high surrogate as
      // its final character, so we don't have to worry about that edge case.
      if( Character.isHighSurrogate(ch) ) {
        sawHighSurrogate = true;
        highSurrogate = ch;
        continue;
      }

      // we have a normal character, or an isolated low surrogate
      utf8(output, ch);
    }
  }


  /**
   * Output a code point in UTF-8.
   * 
   * @param output
   *          the stream
   * @param codePoint
   *          the code point
   * @throws IOException
   */
  private static void utf8(OutputStream output, int codePoint) throws IOException {
    if( codePoint < 0x80 ) {
      output.write((byte) codePoint);
    } else if( codePoint < 0x800 ) {
      output.write((byte) (0b1100_0000 | ((codePoint >> 6) & 0x1f)));
      output.write((byte) (0b1000_0000 | (codePoint & 0x3f)));
    } else if( codePoint < 0x1_0000 ) {
      output.write((byte) (0b1110_0000 | ((codePoint >> 12) & 0x0f)));
      output.write((byte) (0b1000_0000 | ((codePoint >> 6) & 0x3f)));
      output.write((byte) (0b1000_0000 | (codePoint & 0x3f)));
    } else {
      output.write((byte) (0b1111_0000 | ((codePoint >> 18) & 0x07)));
      output.write((byte) (0b1000_0000 | ((codePoint >> 12) & 0x3f)));
      output.write((byte) (0b1000_0000 | ((codePoint >> 6) & 0x3f)));
      output.write((byte) (0b1000_0000 | (codePoint & 0x3f)));
    }
  }
}
