package io.setl.json.exception;

import io.setl.json.primitive.CJString;

/**
 * Exception thrown when trying to represent positive infinity, negative infinity or Not-a-number values as JSON numbers.
 *
 * @author Simon Greatrix on 24/01/2020.
 */
public class NonFiniteNumberException extends ArithmeticException {

  private static final CJString REP_INFINITY = CJString.create("Infinity");

  private static final CJString REP_NAN = CJString.create("NaN");

  private static final CJString REP_NEG_INFINITY = CJString.create("-Infinity");

  private static final long serialVersionUID = 1L;



  /**
   * Possible types of non-finite number.
   */
  public enum Type {
    /** Not-a-number. */
    NAN,
    /** Negative infinity. */
    NEGATIVE_INFINITY,
    /** Positive infinity. */
    POSITIVE_INFINITY
  }



  /** The type of non-finite number. */
  private final Type type;


  /**
   * New exception for the non-finite value.
   *
   * @param d the value
   */
  public NonFiniteNumberException(double d) {
    super(Double.toString(d));
    if (Double.isNaN(d)) {
      type = Type.NAN;
    } else {
      type = d < 0 ? Type.NEGATIVE_INFINITY : Type.POSITIVE_INFINITY;
    }
  }


  /**
   * New exception for the non-finite value.
   *
   * @param f the value
   */
  public NonFiniteNumberException(float f) {
    super(Float.toString(f));
    if (Float.isNaN(f)) {
      type = Type.NAN;
    } else {
      type = f < 0 ? Type.NEGATIVE_INFINITY : Type.POSITIVE_INFINITY;
    }
  }


  /**
   * Get the standard representation of the non-finite number.
   *
   * @return the representation
   */
  public CJString getRepresentation() {
    switch (getType()) {
      case NAN:
        return REP_NAN;
      case NEGATIVE_INFINITY:
        return REP_NEG_INFINITY;
      case POSITIVE_INFINITY:
        return REP_INFINITY;
      default:
        // Can't happen
        throw new IllegalStateException("Unknown Non-finite type " + getType());
    }
  }


  /**
   * Get the type of non-finite number.
   *
   * @return the type
   */
  public Type getType() {
    return type;
  }

}
