package io.setl.json.primitive;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.JsonString;

import io.setl.json.primitive.cache.CacheManager;
import io.setl.json.primitive.cache.ICache;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class CJString extends CJBase implements JsonString {

  private static final byte[] ESCAPES;

  /** Canonical form uses upper-case hexadecimal. */
  private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


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
   * Get an instance that encapsulates the specified value.
   *
   * @param value the value
   *
   * @return the instance
   */
  public static CJString create(@Nonnull String value) {
    ICache<String, CJString> cache = CacheManager.stringCache();
    return cache.get(value, CJString::new);
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


  /**
   * Format text in its canonical JSON representation and append to the buffer. The rules for the canonical representation are:
   * <ol>
   *   <li>avoiding escape sequences for characters except those otherwise inexpressible in JSON (U+0022 QUOTATION MARK, U+005C REVERSE SOLIDUS, and ASCII
   *   control characters U+0000 through U+001F) or UTF-8 (U+D800 through U+DFFF), and</li>
   *   <li>avoiding escape sequences for combining characters, variation selectors, and other code points that affect preceding characters, and</li>
   *   <li>using two-character escape sequences where possible for characters that require escaping:
   *     <ul>
   *       <li><code>&#92;b</code> U+0008 BACKSPACE</li>
   *       <li><code>&#92;t</code> U+0009 CHARACTER TABULATION (“tab”)</li>
   *       <li><code>&#92;n</code> U+000A LINE FEED (“newline”)</li>
   *       <li><code>&#92;f</code> U+000C FORM FEED</li>
   *       <li><code>&#92;r</code> U+000D CARRIAGE RETURN</li>
   *       <li><code>&#92;"</code> U+0022 QUOTATION MARK</li>
   *       <li><code>&#92;&#92;</code> U+005C REVERSE SOLIDUS (“backslash”), and</li>
   *     </ul>
   *   </li>
   *   <li>using six-character <code>&#92;u00xx</code> uppercase hexadecimal escape sequences for control characters that require escaping but lack a
   *   two-character sequence, and</li>
   *   <li>using six-character <code>&#92;uDxxx</code> uppercase hexadecimal escape sequences for lone surrogates</li>
   * </ol>
   *
   * @param buf   the output buffer
   * @param input the text to format
   */
  public static void format(Appendable buf, String input) throws IOException {
    // opening quote
    buf.append('"');

    int i = 0;
    int l = input.length();
    while (i < l) {
      char ch = input.charAt(i);

      if (ch < 128) {
        // Handle the ASCII characters, and C0 block
        i = formatAscii(buf, input, i);
      } else if (ch < Character.MIN_HIGH_SURROGATE || Character.MAX_LOW_SURROGATE < ch) {
        // Normal character
        buf.append(ch);
        i++;
      } else {
        // it's a surrogate
        i = formatSurrogate(buf, input, i);
      }
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


  private static int formatAscii(Appendable buf, String input, int i) throws IOException {
    final int l = input.length();
    while (i < l) {
      char ch = input.charAt(i);
      if (ch >= 128) {
        return i;
      }

      switch (ESCAPES[ch]) {
        case 0:
          // normal character
          buf.append(ch);
          break;
        case 1:
          // unicode escape
          buf.append("\\u00").append(HEX[ch >>> 4]).append(HEX[ch & 0xf]);
          break;
        default:
          // special escape
          buf.append('\\').append((char) ESCAPES[ch]);
          break;
      }

      i++;
    }
    return i;
  }


  private static int formatSurrogate(Appendable buf, String input, int i) throws IOException {
    final int l = input.length();
    char ch0 = input.charAt(i);
    i++;
    if (Character.isLowSurrogate(ch0) || i == l) {
      // Trailing surrogate without a leading surrogate, must escape
      appendUnicode(buf, ch0);
      return i;
    }

    // we have a high surrogate with a following character - is it a low surrogate?
    char ch1 = input.charAt(i);
    if (Character.isLowSurrogate(ch1)) {
      // high and then low surrogate, so all good!
      buf.append(ch0).append(ch1);
      return i + 1;
    }

    // high surrogate not followed by low surrogate
    appendUnicode(buf, ch0);
    return i;
  }


  static {
    byte[] escaped = new byte[128];

    // 0 to 31 must be escaped
    for (int i = 0; i < 32; i++) {
      escaped[i] = 1;
    }

    // These have special escapes
    escaped[8] = 'b';
    escaped[9] = 't';
    escaped[10] = 'n';
    escaped[12] = 'f';
    escaped[13] = 'r';
    escaped['\\'] = '\\';
    escaped['"'] = '"';

    ESCAPES = escaped;
  }

  private final String value;


  private CJString(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof JsonString)) {
      return false;
    }

    JsonString jsonString = (JsonString) o;
    return Objects.equals(getString(), jsonString.getString());
  }


  @Override
  public CharSequence getChars() {
    return getString();
  }


  @Override
  public String getString() {
    return value;
  }


  @Override
  public Object getValue() {
    return getString();
  }


  @Override
  public ValueType getValueType() {
    return ValueType.STRING;
  }


  @Override
  public int hashCode() {
    return super.hashCode();
  }


  @Override
  public String toString() {
    return format(value);
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    format(writer, value);
  }

}
