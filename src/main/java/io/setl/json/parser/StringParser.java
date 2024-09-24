package io.setl.json.parser;

import static io.setl.json.parser.Parser.safe;

import javax.json.stream.JsonParsingException;

import io.setl.json.io.Input;

/**
 * Parse a JSON String.
 *
 * @author Simon Greatrix on 13/01/2020.
 */
class StringParser {

  /** Working buffer. */
  final StringBuilder buf = new StringBuilder();

  /** The input. */
  final Input input;


  StringParser(Input input) {
    this.input = input;
  }


  String parse() {
    while (true) {
      int r = input.read();
      if (r == '"') {
        // seen closing quote, we are done.
        return buf.toString();
      }
      if (r == -1) {
        // seen EOF before closing quote
        throw new JsonParsingException("Unterminated string", input.getLocation());
      }
      if (r < 32) {
        throw new JsonParsingException(String.format("JSON strings must not contain C0 control codes, including 0x%04x", r), input.getLocation());
      }

      if (r == '\\') {
        parseEscape();
      } else {
        buf.append((char) r);
      }
    }
  }


  /** Read an escape sequence. */
  private void parseEscape() {
    int r = input.read();
    switch (r) {
      case '"':
        buf.append('\"');
        break;
      case '\\':
        buf.append('\\');
        break;
      case '/':
        buf.append('/');
        break;
      case 'b':
        buf.append('\b');
        break;
      case 'f':
        buf.append('\f');
        break;
      case 'n':
        buf.append('\n');
        break;
      case 'r':
        buf.append('\r');
        break;
      case 't':
        buf.append('\t');
        break;
      case 'u':
        parseUnicode();
        break;
      default:
        throw new JsonParsingException(String.format("Invalid escape sequence '\\' followed by %s", safe(r)), input.getLocation());
    }
  }


  /** Read a Unicode escape, which must be four hexadecimal digits. */
  private void parseUnicode() {
    int u = 0;
    for (int i = 0; i < 4; i++) {
      int r = input.read();
      u = u * 16;
      if ('0' <= r && r <= '9') {
        u += r - '0';
      } else if ('a' <= r && r <= 'f') {
        u += r - 'a' + 10;
      } else if ('A' <= r && r <= 'F') {
        u += r - 'A' + 10;
      } else {
        throw new JsonParsingException(String.format("Invalid hex character in \\u escape. Saw %s", safe(r)), input.getLocation());
      }
    }
    buf.append((char) u);
  }

}
