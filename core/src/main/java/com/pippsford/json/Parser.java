package com.pippsford.json;

import java.io.*;
import java.math.BigDecimal;

public class Parser {

  private static final int MAX_RECURSION_DEPTH = Integer.getInteger("com.pippsford.Parser.maxRecursion",1_000);
  
  /**
   * Letters for the "false" literal
   */
  private static final char[] LITERAL_FALSE = new char[] { 'a', 'l', 's', 'e' };

  /** Letters for the "null" literal */
  private static final char[] LITERAL_NULL = new char[] { 'u', 'l', 'l' };

  /** Letters for the "true" literal */
  private static final char[] LITERAL_TRUE = new char[] { 'r', 'u', 'e' };


  /**
   * Check if input represents whitespace
   * 
   * @param r
   *          the input
   * @return true if whitespace
   */
  private static boolean isWhite(int r) {
    return (r == ' ' || r == '\n' || r == '\r' || r == '\t');
  }


  /**
   * Test reading a file.
   * 
   * @param args
   *          the file name
   * @throws IOException
   *           if the file cannot be read
   */
  public static void main(String[] args) throws IOException {
    Primitive prim;
    try (FileReader reader = new FileReader(args[0])) {
      prim = parseFirst(reader);
    }
    System.out.println("Type = " + prim.getType());
    System.out.println("Value = " + prim.getValue());
  }


  /**
   * Match a literal.
   * 
   * @param literal
   *          the literal to match (excluding first character)
   * @param input
   *          input
   * @throws IOException
   *           if literal is not matched
   */
  private static void matchLiteral(char[] literal, Reader input) throws IOException {
    for(int i = 0;i < literal.length;i++) {
      int r = input.read();
      if( r == -1 ) throw new EOFException();
      if( r != literal[i] ) {
        throw new InvalidJson("Invalid character in literal 0x" + Integer.toHexString(r) + " when expecting '" + literal[i] + "'");
      }
    }
  }


  /**
   * Read the primitive from the input. There must be no non-whitespace characters left on the input after reading the primitive.
   * 
   * @param input
   *          the input
   * @return the primitive
   * @throws IOException
   *           if the input cannot be read, or there is invalid JSON data
   */
  public static Primitive parse(Reader input) throws IOException {
    if( !(input instanceof PushbackReader) ) {
      input = new PushbackReader(input);
    }
    Primitive primitive = parseAny((PushbackReader) input, skipWhite(input), 0);

    while( true ) {
      int ch = input.read();
      if( ch == -1 ) {
        return primitive;
      }
      if( !isWhite(ch) ) {
        throw new InvalidJson("Additional characters found after JSON data. Saw 0x" + Integer.toHexString(ch));
      }
    }
  }


  /**
   * Read the next primitive from the input
   * 
   * @param input
   *          the input
   * @param depth
   *          the recursion depth
   * @return the next primitive
   * @throws IOException
   *           if the input cannot be read, or there is invalid JSON data
   */
  private static Primitive parseAny(PushbackReader input, int r, int depth) throws IOException {
    if( depth >= MAX_RECURSION_DEPTH ) {
      throw new InvalidJson("Json structure has exceeded the configured maximum nesting depth of "+MAX_RECURSION_DEPTH);
    }
    if( r == '{' ) return parseObject(input, depth + 1);
    if( r == '[' ) return parseArray(input, depth + 1);
    if( r == '\"' ) return parseString(input);
    if( r == 't' || r == 'f' ) return parseBoolean(input, r);
    if( r == 'n' ) return parseNull(input);
    if( r == '-' || ('0' <= r && r <= '9') ) return parseNumber(input, r);
    throw new InvalidJson("Invalid input byte 0x" + Integer.toHexString(r));
  }


  /**
   * Parse an array from the input
   * 
   * @param input
   *          the input
   * @param depth
   *          the recursion depth
   * @return the array
   * @throws IOException
   */
  private static Primitive parseArray(PushbackReader input, int depth) throws IOException {
    JsonArray arr = new JsonArray();
    Primitive prim = new Primitive(Type.ARRAY, arr);
    int r = skipWhite(input);
    if( r==']' ) {
      // empty array
      return prim;
    }
    
    while( true ) {
      Primitive val = parseAny(input, r, depth);
      arr.add(val);
      
      // skip on to closer or comma
      r = skipWhite(input);
      if( r == ']' ) return prim;
      if( r != ',' ) throw new InvalidJson("Array continuation was not ',' but 0x" + Integer.toHexString(r));

      // skip whitespace post comma
      r = skipWhite(input);
    }

  }


  /**
   * Parse a boolean from the input
   * 
   * @param input
   *          the input
   * @param r
   *          the initial character of the literal
   * @return the boolean
   * @throws IOException
   */
  private static Primitive parseBoolean(Reader input, int r) throws IOException {
    Boolean val = Boolean.valueOf(r == 't');
    matchLiteral(val.booleanValue() ? LITERAL_TRUE : LITERAL_FALSE, input);
    return new Primitive(Type.BOOLEAN, val);
  }


  /**
   * Read the next primitive from the input.
   * 
   * @param input
   *          the input
   * @return the next primitive
   * @throws IOException
   *           if the input cannot be read, or there is invalid JSON data
   */
  public static Primitive parseFirst(Reader input) throws IOException {
    if( !(input instanceof PushbackReader) ) {
      input = new PushbackReader(input);
    }
    return parseAny((PushbackReader) input, skipWhite(input), 0);
  }


  /**
   * Parse a null from the input
   * 
   * @param input
   *          the input
   * @return the null
   * @throws IOException
   */
  private static Primitive parseNull(Reader input) throws IOException {
    matchLiteral(LITERAL_NULL, input);
    return new Primitive(Type.NULL, null);
  }


  /**
   * Parse a number from the input
   * 
   * @param input
   *          the input
   * @param r
   *          the initial character of the number
   * @param verify
   *          if true, verify the input is in canonical form
   * @return the number
   * @throws IOException
   */
  private static Primitive parseNumber(PushbackReader input, int r) throws IOException {
    StringBuilder buf = new StringBuilder();
    // process first character
    int s;
    if( r == '-' ) {
      buf.append('-');
      s = 0;
    } else if( r == '0' ) {
      buf.append('0');
      s = 1;
    } else {
      buf.append((char) r);
      s = 2;
    }

    // read rest of number
    while( true ) {
      r = input.read();
      if( r == -1 || isWhite(r) || r == ',' || r == ']' || r == '}' ) {
        // Check for an invalid number
        if( s == 0 || s == 10 || s == 20 || s == 21 ) {
          throw new InvalidJson("Incomplete JSON number: \"" + buf.toString() + "\"");
        }
        break;
      }

      switch (s) {
      case 0:
        // seen leading minus sign, must be followed by digit
        if( r == '0' ) {
          buf.append('0');
          s = 1;
        } else if( '1' <= r && r <= '9' ) {
          buf.append((char) r);
          s = 2;
        } else {
          throw new InvalidJson("Invalid character in JSON number: \"" + buf.toString() + "\" was followed by 0x" + Integer.toHexString(r));
        }
        break;
      case 1:
        // seen leading zero, must be followed by '.', 'e', or 'E'
        if( r == '.' ) {
          // start fractional part
          buf.append('.');
          s = 10;
        } else if( r == 'e' || r == 'E' ) {
          // start exponent part
          buf.append((char) r);
          s = 20;
        } else {
          throw new InvalidJson("Invalid character in JSON number: \"" + buf.toString() + "\" was followed by 0x" + Integer.toHexString(r));
        }
        break;
      case 2:
        // seen leading 1 to 9, can be followed by any digit, '.', 'e', or 'E'
        if( '0' <= r && r <= '9' ) {
          // carry on with integer part
          buf.append((char) r);
        } else if( r == '.' ) {
          // start fractional part
          buf.append('.');
          s = 10;
        } else if( r == 'e' || r == 'E' ) {
          // start exponent part
          buf.append((char) r);
          s = 20;
        } else {
          throw new InvalidJson("Invalid character in JSON number: \"" + buf.toString() + "\" was followed by 0x" + Integer.toHexString(r));
        }
        break;
      case 10: // falls through
        // seen '.', read fractional part
        if( '0' <= r && r <= '9' ) {
          // carry on with fractional part
          buf.append((char) r);
          s = 11;
        } else {
          throw new InvalidJson("Invalid character in JSON number: \"" + buf.toString() + "\" was followed by 0x" + Integer.toHexString(r));
        }
        break;
      case 11:
        // seen '.' and a digit, read fractional part
        if( '0' <= r && r <= '9' ) {
          // carry on with fractional part
          buf.append((char) r);
        } else if( r == 'e' || r == 'E' ) {
          // start exponent part
          buf.append((char) r);
          s = 20;
        } else {
          throw new InvalidJson("Invalid character in JSON number: \"" + buf.toString() + "\" was followed by 0x" + Integer.toHexString(r));
        }
        break;
      case 20:
        // seen 'e' or 'E', read exponent part. Can be '+', '-' or '0' to '9'
        if( r == '+' || r == '-' ) {
          buf.append((char) r);
          s = 21;
        } else if( '0' <= r && r <= '9' ) {
          buf.append('+').append((char) r);
          s = 22;
        } else {
          throw new InvalidJson("Invalid character in JSON number: \"" + buf.toString() + "\" was followed by 0x" + Integer.toHexString(r));
        }
        break;
      case 21:
        // seen 'e' or 'E' followed by '+' or '-'. Must have a digit
        if( '0' <= r && r <= '9' ) {
          buf.append((char) r);
          s = 22;
        } else {
          throw new InvalidJson("Invalid character in JSON number: \"" + buf.toString() + "\" was followed by 0x" + Integer.toHexString(r));
        }
        break;
      case 22:
        // read exponent, must be all digits
        if( '0' <= r && r <= '9' ) {
          buf.append((char) r);
          s = 22;
        } else {
          throw new InvalidJson("Invalid character in JSON number: \"" + buf.toString() + "\" was followed by 0x" + Integer.toHexString(r));
        }
        break;
      }
    }
    if(r!=-1) {
      input.unread(r);
    }
    try {
      Number val = new BigDecimal(buf.toString());
      return new Primitive(Type.NUMBER, val);
    } catch (NumberFormatException nfe) {
      throw new InvalidJson("Number in JSON is too extreme to process: \"" + buf.toString() + "\"");
    }
  }


  /**
   * Parse an object from the input
   * 
   * @param input
   *          the input
   * @param depth
   *          the recursion depth
   * @return the object
   * @throws IOException
   */
  private static Primitive parseObject(PushbackReader input, int depth) throws IOException {
    JsonObject obj = new JsonObject();
    Primitive prim = new Primitive(Type.OBJECT, obj);
    while( true ) {
      int r = skipWhite(input);
      // name must start with a quote
      if( r != '\"' ) {
        if( r == '}' && obj.isEmpty() ) {
          // Empty object is OK
          return prim;
        }
        throw new InvalidJson("Object pair's name did not start with '\"' but with 0x" + Integer.toHexString(r));
      }
      Primitive name = parseString(input);

      // then comes the ':'
      r = skipWhite(input);
      if( r != ':' ) {
        throw new InvalidJson("Object pair-value separator was not start with ':' but 0x" + Integer.toHexString(r));
      }

      // and then the value
      Primitive value = parseAny(input, skipWhite(input), depth);
      obj.put(name.getValueSafe(String.class), value);
      r = skipWhite(input);
      if( r == '}' ) return prim;
      if( r != ',' ) throw new InvalidJson("Object continuation was not ',' but 0x" + Integer.toHexString(r));
    }
  }


  /**
   * Parse an array from the input
   * 
   * @param input
   *          the input
   * @return the array
   * @throws IOException
   */
  private static Primitive parseString(Reader input) throws IOException {
    int s = 0;
    int u = 0;
    StringBuilder buf = new StringBuilder();
    boolean notDone = true;
    while( notDone ) {
      int r = input.read();
      switch (s) {
      case 0:
        // regular character probable
        if( r == '"' ) {
          notDone = false;
          break;
        }
        if( r == '\\' ) {
          s = 1;
          break;
        }
        if( r == -1 ) throw new EOFException("Unterminated string");
        if( r < 32 ) {
          throw new InvalidJson("JSON strings must not contain C0 control codes, including 0x" + Integer.toHexString(r));
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
          throw new InvalidJson("Invalid escape sequence \"\\" + ((char) r) + "\"");
        }
        if( s == 1 ) s = 0;
        break;

      case 2: // fall thru
      case 3: // fall thru
      case 4: // fall thru
      case 5: // fall thru
        // Unicode escape
        u = u * 16;
        if( '0' <= r && r <= '9' ) {
          u += r - '0';
        } else if( 'a' <= r && r <= 'f' ) {
          u += r - 'a' + 10;
        } else if( 'A' <= r && r <= 'F' ) {
          u += r - 'A' + 10;
        } else {
          throw new InvalidJson("Invalid hex character in \\u escape");
        }
        s++;
        if( s == 6 ) {
          s = 0;
          buf.append((char) u);
        }
        break;
      }
    }

    // finally create the string
    Primitive prim = new Primitive(Type.STRING, buf.toString());
    return prim;
  }


  /**
   * Skip whitespace and return the first non-white character. Whitespace is not allowed in the canonical form
   * 
   * @param input
   *          the input
   * @return the first non-white character
   * @throws IOException
   */
  private static int skipWhite(Reader input) throws IOException {
    // whitespace allowed
    int r;
    do {
      r = input.read();
    } while( isWhite(r) );
    if( r == -1 ) throw new EOFException();
    return r;
  }

}
