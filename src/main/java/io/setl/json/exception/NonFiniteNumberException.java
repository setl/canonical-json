package io.setl.json.exception;

/**
 * @author Simon Greatrix on 24/01/2020.
 */
public class NonFiniteNumberException extends ArithmeticException {

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


  public Type getType() {
    return type;
  }

}
