package io.setl.json;

import io.setl.json.exception.InvalidJsonException;
import io.setl.json.io.Input;
import io.setl.json.primitive.PBase;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PNumber;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class Parser {

  /**
   * Letters for the "false" literal.
   */
  private static final char[] LITERAL_FALSE = new char[]{'a', 'l', 's', 'e'};

  /** Letters for the "null" literal. */
  private static final char[] LITERAL_NULL = new char[]{'u', 'l', 'l'};

  /** Letters for the "true" literal. */
  private static final char[] LITERAL_TRUE = new char[]{'r', 'u', 'e'};

  private static final int MAX_RECURSION_DEPTH = Integer.getInteger(Parser.class.getName() + ".maxRecursion", 1_000);


  private static InvalidJsonException badCharacter(int actual, char expected) {
    return new InvalidJsonException("Invalid character in literal 0x" + Integer.toHexString(actual) + " when expecting '" + expected + "'", null);
  }


  private static InvalidJsonException badNumber(StringBuilder buf, int r, Input input) {
    return new InvalidJsonException(
        "Invalid character in JSON number: \"" + buf.toString() + "\" was followed by 0x" + Integer.toHexString(r),
        input.getLocation()
    );
  }


  /**
   * Check if input represents whitespace.
   *
   * @param r the input
   *
   * @return true if whitespace
   */
  private static boolean isWhite(int r) {
    return (r == ' ' || r == '\n' || r == '\r' || r == '\t');
  }


  /**
   * Match a literal.
   *
   * @param literal the literal to match (excluding first character)
   * @param input   input
   *
   * @throws IOException if literal is not matched
   */
  private static void matchLiteral(char[] literal, Input input) throws IOException {
    for (char c : literal) {
      int r = input.read();
      if (r == -1) {
        throw new EOFException();
      }
      if (r != c) {
        throw badCharacter(r, c);
      }
    }
  }


  /**
   * Read the primitive from the input. There must be no non-whitespace characters left on the input after reading the primitive.
   *
   * @param reader the input
   *
   * @return the primitive
   *
   * @throws IOException if the input cannot be read, or there is invalid JSON data
   */
  public static Primitive parse(Reader reader) throws IOException {
    Input input = new Input(reader);
    Primitive primitive = parseAny(input, skipWhite(input), 0);

    while (true) {
      int ch = input.read();
      if (ch == -1) {
        return primitive;
      }
      if (!isWhite(ch)) {
        throw new InvalidJsonException("Additional characters found after JSON data. Saw 0x" + Integer.toHexString(ch), input.getLocation());
      }
    }
  }


  /**
   * Read a list of whitespace separated primitives from the input.
   *
   * @param reader the input
   *
   * @return the primitives
   *
   * @throws IOException if the input cannot be read, or there is invalid JSON data
   */
  public static List<Primitive> parseAll(Reader reader) throws IOException {
    Input input = new Input(reader);
    final LinkedList<Primitive> list = new LinkedList<>();
    while (true) {
      int r = input.read();
      if (r == -1) {
        return list;
      }
      if (isWhite(r)) {
        continue;
      }

      Primitive primitive = parseAny(input, r, 0);
      list.add(primitive);
    }
  }


  /**
   * Read the next primitive from the input.
   *
   * @param input the input
   * @param depth the recursion depth
   *
   * @return the next primitive
   *
   * @throws IOException if the input cannot be read, or there is invalid JSON data
   */
  private static Primitive parseAny(Input input, int r, int depth) throws IOException {
    if (depth >= MAX_RECURSION_DEPTH) {
      throw new InvalidJsonException("Json structure has exceeded the configured maximum nesting depth of " + MAX_RECURSION_DEPTH, input.getLocation());
    }
    try {
      if (r == '{') {
        return parseObject(input, depth + 1);
      }
      if (r == '[') {
        return parseArray(input, depth + 1);
      }
      if (r == '\"') {
        return parseString(input);
      }
      if (r == 't' || r == 'f') {
        return parseBoolean(input, r);
      }
      if (r == 'n') {
        return parseNull(input);
      }
      if (r == '-' || ('0' <= r && r <= '9')) {
        return parseNumber(input, r);
      }
      throw new InvalidJsonException("Invalid input byte 0x" + Integer.toHexString(r), input.getLocation());
    } catch (StackOverflowError e) {
      throw new InvalidJsonException(
          "Json structure was less than configured maximum nesting depth of " + MAX_RECURSION_DEPTH + " but failed at " + depth,
          input.getLocation()
      );
    }
  }


  /**
   * Parse an array from the input.
   *
   * @param input the input
   * @param depth the recursion depth
   *
   * @return the array
   */
  private static Primitive parseArray(Input input, int depth) throws IOException {
    JArray arr = new JArray();
    int r = skipWhite(input);
    if (r == ']') {
      // empty array
      return arr;
    }

    while (true) {
      Primitive val = parseAny(input, r, depth);
      arr.add(val);

      // skip on to closer or comma
      r = skipWhite(input);
      if (r == ']') {
        return arr;
      }
      if (r != ',') {
        throw new InvalidJsonException("Array continuation was not ',' but 0x" + Integer.toHexString(r), input.getLocation());
      }

      // skip whitespace post comma
      r = skipWhite(input);
    }

  }


  /**
   * Parse a boolean from the input.
   *
   * @param input the input
   * @param r     the initial character of the literal
   *
   * @return the boolean
   */
  private static Primitive parseBoolean(Input input, int r) throws IOException {
    if (r == 't') {
      matchLiteral(LITERAL_TRUE, input);
      return PTrue.TRUE;
    }
    matchLiteral(LITERAL_FALSE, input);
    return PFalse.FALSE;
  }


  /**
   * Read the next primitive from the input.
   *
   * @param reader the input
   *
   * @return the next primitive
   *
   * @throws IOException if the input cannot be read, or there is invalid JSON data
   */
  public static Primitive parseFirst(Reader reader) throws IOException {
    Input input = new Input(reader);
    return parseAny(input, skipWhite(input), 0);
  }


  /**
   * Parse a null from the input.
   *
   * @param input the input
   *
   * @return the null
   */
  private static PBase parseNull(Input input) throws IOException {
    matchLiteral(LITERAL_NULL, input);
    return PNull.NULL;
  }


  /**
   * Parse a number from the input.
   *
   * @param input the input
   * @param r     the initial character of the number
   *
   * @return the number
   */
  private static PBase parseNumber(Input input, int r) throws IOException {
    StringBuilder buf = new StringBuilder();
    // process first character
    int s;
    if (r == '-') {
      buf.append('-');
      s = 0;
    } else if (r == '0') {
      buf.append('0');
      s = 1;
    } else {
      buf.append((char) r);
      s = 2;
    }

    // read rest of number
    while (true) {
      r = input.read();
      if (r == -1 || isWhite(r) || r == ',' || r == ']' || r == '}') {
        // Check for an invalid number
        if (s == 0 || s == 10 || s == 20 || s == 21) {
          throw new InvalidJsonException("Incomplete JSON number: \"" + buf.toString() + "\"", input.getLocation());
        }
        break;
      }

      switch (s) {
        case 0:
          // seen leading minus sign, must be followed by digit
          if (r == '0') {
            buf.append('0');
            s = 1;
          } else if ('1' <= r && r <= '9') {
            buf.append((char) r);
            s = 2;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 1:
          // seen leading zero, must be followed by '.', 'e', or 'E'
          if (r == '.') {
            // start fractional part
            buf.append('.');
            s = 10;
          } else if (r == 'e' || r == 'E') {
            // start exponent part
            buf.append((char) r);
            s = 20;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 2:
          // seen leading 1 to 9, can be followed by any digit, '.', 'e', or 'E'
          if ('0' <= r && r <= '9') {
            // carry on with integer part
            buf.append((char) r);
          } else if (r == '.') {
            // start fractional part
            buf.append('.');
            s = 10;
          } else if (r == 'e' || r == 'E') {
            // start exponent part
            buf.append((char) r);
            s = 20;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 10: // falls through
          // seen '.', read fractional part
          if ('0' <= r && r <= '9') {
            // carry on with fractional part
            buf.append((char) r);
            s = 11;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 11:
          // seen '.' and a digit, read fractional part
          if ('0' <= r && r <= '9') {
            // carry on with fractional part
            buf.append((char) r);
          } else if (r == 'e' || r == 'E') {
            // start exponent part
            buf.append((char) r);
            s = 20;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 20:
          // seen 'e' or 'E', read exponent part. Can be '+', '-' or '0' to '9'
          if (r == '+' || r == '-') {
            buf.append((char) r);
            s = 21;
          } else if ('0' <= r && r <= '9') {
            buf.append('+').append((char) r);
            s = 22;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        case 21:
          // fall thru
          // seen 'e' or 'E' followed by '+' or '-'. Must have a digit next. Cannot end in this state.
        case 22:
          // reading exponent, must be all digits
          if ('0' <= r && r <= '9') {
            buf.append((char) r);
            s = 22;
          } else {
            throw badNumber(buf, r, input);
          }
          break;
        default:
          throw new InternalError("Impossible parser state");
      }
    }
    if (r != -1) {
      input.unread(r);
    }
    try {
      Number val = new BigDecimal(buf.toString());
      return new PNumber(val);
    } catch (NumberFormatException nfe) {
      throw new InvalidJsonException("Number in JSON is too extreme to process: \"" + buf.toString() + "\"", input.getLocation());
    }
  }


  /**
   * Parse an object from the input.
   *
   * @param input the input
   * @param depth the recursion depth
   *
   * @return the object
   */
  private static Primitive parseObject(Input input, int depth) throws IOException {
    JObject obj = new JObject();
    while (true) {
      int r = skipWhite(input);
      // name must start with a quote
      if (r != '\"') {
        if (r == '}' && obj.isEmpty()) {
          // Empty object is OK
          return obj;
        }
        throw new InvalidJsonException("Object pair's name did not start with '\"' but with 0x" + Integer.toHexString(r), input.getLocation());
      }
      Primitive name = parseString(input);

      // then comes the ':'
      r = skipWhite(input);
      if (r != ':') {
        throw new InvalidJsonException("Object pair-value separator was not start with ':' but 0x" + Integer.toHexString(r), input.getLocation());
      }

      // and then the value
      Primitive value = parseAny(input, skipWhite(input), depth);
      obj.put(name.getValueSafe(String.class), value);
      r = skipWhite(input);
      if (r == '}') {
        return obj;
      }
      if (r != ',') {
        throw new InvalidJsonException("Object continuation was not ',' but 0x" + Integer.toHexString(r), input.getLocation());
      }
    }
  }


  /**
   * Parse a string from the input.
   *
   * @param input the input
   *
   * @return the string
   */
  private static PBase parseString(Input input) throws IOException {
    int s = 0;
    int u = 0;
    StringBuilder buf = new StringBuilder();
    boolean notDone = true;
    while (notDone) {
      int r = input.read();
      switch (s) {
        case 0:
          // regular character probable
          if (r == '"') {
            notDone = false;
            break;
          }
          if (r == '\\') {
            s = 1;
            break;
          }
          if (r == -1) {
            throw new EOFException("Unterminated string");
          }
          if (r < 32) {
            throw new InvalidJsonException("JSON strings must not contain C0 control codes, including 0x" + Integer.toHexString(r), input.getLocation());
          }
          buf.append((char) r);
          break;

        case 1:
          // expecting an escape sequence
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
              u = 0;
              s = 2;
              break;
            default:
              throw new InvalidJsonException("Invalid escape sequence \"\\" + ((char) r) + "\"", input.getLocation());
          }
          if (s == 1) {
            s = 0;
          }
          break;

        case 2: // fall thru
        case 3: // fall thru
        case 4: // fall thru
        case 5: // fall thru
          // Unicode escape
          u = u * 16;
          if ('0' <= r && r <= '9') {
            u += r - '0';
          } else if ('a' <= r && r <= 'f') {
            u += r - 'a' + 10;
          } else if ('A' <= r && r <= 'F') {
            u += r - 'A' + 10;
          } else {
            throw new InvalidJsonException("Invalid hex character in \\u escape", input.getLocation());
          }
          s++;
          if (s == 6) {
            s = 0;
            buf.append((char) u);
          }
          break;
        default:
          throw new InternalError("Impossible parser state");
      }
    }

    // finally create the string
    return new PString(buf.toString());
  }


  /**
   * Skip whitespace and return the first non-white character. Whitespace is not allowed in the canonical form.
   *
   * @param input the input
   *
   * @return the first non-white character
   */
  private static int skipWhite(Input input) throws IOException {
    // whitespace allowed
    int r;
    do {
      r = input.read();
    }
    while (isWhite(r));
    if (r == -1) {
      throw new EOFException();
    }
    return r;
  }

}
