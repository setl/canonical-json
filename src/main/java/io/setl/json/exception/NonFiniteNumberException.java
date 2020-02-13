package io.setl.json.exception;

import io.setl.json.primitive.PString;

/**
 * @author Simon Greatrix on 24/01/2020.
 */
public class NonFiniteNumberException extends ArithmeticException {

  private static final PString REP_INFINITY = PString.create("Infinity");

  private static final PString REP_NAN = PString.create("NaN");

  private static final PString REP_NEG_INFINITY = PString.create("-Infinity");

  private static final long serialVersionUID = 1L;



  /**
   * Possible types of non-finite number.
   */
  public enum Type {
    NAN,
    NEGATIVE_INFINITY,
    POSITIVE_INFINITY
  }



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
  public PString getRepresentation() {
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


  public Type getType() {
    return type;
  }

}
