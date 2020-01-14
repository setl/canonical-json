package io.setl.json.parser;

import static io.setl.json.parser.JParser.isWhite;
import static io.setl.json.parser.JParser.safe;

import io.setl.json.io.Input;
import java.math.BigDecimal;
import javax.json.stream.JsonParsingException;

/**
 * @author Simon Greatrix on 13/01/2020.
 */
class NumberParser {

  private enum Step {
    /** Processing the starting character. */
    START(false) {
      Step apply(int r) {
        if (r == '-') {
          return LEADING_MINUS;
        }
        if (r == '0') {
          return LEADING_ZERO;
        }
        return INTEGER_PART;
      }
    },
    /** Seen leading minus sign, must be followed by digit. */
    LEADING_MINUS(false) {
      Step apply(int r) {
        if (r == '0') {
          return LEADING_ZERO;
        }
        if (isDigit(r)) {
          return INTEGER_PART;
        }
        return ERROR;
      }
    },
    /** Parsing step failed. */
    ERROR(true) {
      Step apply(int r) {
        throw new IllegalStateException("Step invoked after error");
      }
    },
    /** Seen leading zero, must be followed by '.', 'e', or 'E'. */
    LEADING_ZERO(true) {
      Step apply(int r) {
        if (r == '.') {
          // start fractional part
          return START_FRACTION;
        } else if (r == 'e' || r == 'E') {
          // start exponent part
          return START_EXPONENT;
        }
        return ERROR;
      }
    },
    /** Seen leading 1 to 9, can be followed by any digit, '.', 'e', or 'E'. */
    INTEGER_PART(true) {
      Step apply(int r) {
        if (isDigit(r)) {
          return INTEGER_PART;
        }
        if (r == '.') {
          return START_FRACTION;
        }
        if (r == 'e' || r == 'E') {
          return START_EXPONENT;
        }
        return ERROR;
      }
    },
    /** Seen a '.', so read fractional part. */
    START_FRACTION(false) {
      Step apply(int r) {
        if (isDigit(r)) {
          return FRACTION_PART;
        }
        return ERROR;
      }
    },
    /** Seen a '.' and a digit, now reading fractional part. */
    FRACTION_PART(true) {
      Step apply(int r) {
        if (isDigit(r)) {
          return FRACTION_PART;
        }
        if (r == 'e' || r == 'E') {
          return START_EXPONENT;
        }
        return ERROR;
      }
    },
    /** seen 'e' or 'E', read exponent part. Can be '+', '-' or '0' to '9' */
    START_EXPONENT(false) {
      Step apply(int r) {
        if (r == '+' || r == '-') {
          return SIGNED_EXPONENT;
        }
        if (isDigit(r)) {
          return EXPONENT_PART;
        }
        return ERROR;
      }
    },
    /** Seen "E+" or "E-". A digit is required. */
    SIGNED_EXPONENT(false) {
      Step apply(int r) {
        return isDigit(r) ? EXPONENT_PART : ERROR;
      }
    },
    EXPONENT_PART(true) {
      Step apply(int r) {
        return isDigit(r) ? EXPONENT_PART : ERROR;
      }
    };

    private final boolean terminal;


    Step(boolean terminal) {
      this.terminal = terminal;
    }


    /**
     * Apply the character and advance to the next step in parsing.
     *
     * @param r the character
     *
     * @return the next step
     */
    abstract Step apply(int r);


    /** Can parsing legally stop in this state?. */
    boolean isFinal() {
      return terminal;
    }
  }


  private static JsonParsingException badNumber(StringBuilder buf, int r, Input input) {
    return new JsonParsingException(
        "Invalid character in JSON number: \"" + buf.toString() + "\" was followed by " + safe(r),
        input.getLocation()
    );
  }


  private static boolean isDigit(int r) {
    return '0' <= r && r <= '9';
  }


  final Input input;


  NumberParser(Input input) {
    this.input = input;
  }


  /**
   * Does the character end the number?. The actual character is the first one that is not part of the number and will need to be unread for later processing.
   *
   * @param r the character
   *
   * @return true if no longer in the number
   */
  private boolean isEnd(int r, Step step) {
    if (r == -1 || isWhite(r) || r == ',' || r == ']' || r == '}') {
      input.unread(r);
      // Check for an invalid final state
      if (step.isFinal()) {
        return true;
      }

      // invalid final state
      throw new JsonParsingException("Incomplete JSON number", input.getLocation());
    }

    // not an ending character
    return false;
  }


  /**
   * Parse a number from the input.
   *
   * @param r the initial character of the number
   *
   * @return the number
   */
  public BigDecimal parse(int r) {
    StringBuilder buf = new StringBuilder();
    buf.append((char) r);

    // read rest of number
    Step step = Step.START.apply(r);

    while (true) {
      r = input.read();
      if (isEnd(r, step)) {
        break;
      }
      buf.append((char) r);
      step = step.apply(r);
      if (step == Step.ERROR) {
        throw badNumber(buf, r, input);
      }
    }

    try {
      return new BigDecimal(buf.toString());
    } catch (NumberFormatException nfe) {
      throw new JsonParsingException("Number in JSON is too extreme to process: \"" + buf.toString() + "\"", input.getLocation());
    }
  }
}
