package io.setl.json.primitive.numbers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.json.JsonNumber;
import javax.json.JsonValue;

import io.setl.json.Canonical;
import io.setl.json.exception.NonFiniteNumberException;
import io.setl.json.primitive.CJBase;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.cache.CacheManager;
import io.setl.json.primitive.cache.ICache;

/**
 * A number.
 *
 * @author Simon Greatrix on 08/01/2020.
 */
public abstract class CJNumber extends CJBase implements JsonNumber {

  public static final int TYPE_BIG_INT = 2;

  public static final int TYPE_DECIMAL = 3;

  public static final int TYPE_INT = 0;

  public static final int TYPE_LONG = 1;

  private static final Map<Class<? extends Number>, Function<Number, CJNumber>> CREATORS = Map.of(
      BigDecimal.class, n -> new CJBigDecimal((BigDecimal) n),
      BigInteger.class, n -> new CJBigInteger((BigInteger) n),
      Integer.class, n -> create(n.intValue()),
      Long.class, n -> new CJLong(n.longValue())
  );


  private static final Map<Class<? extends Number>, UnaryOperator<Number>> SIMPLIFIERS = Map.of(
      AtomicInteger.class, Number::intValue,
      AtomicLong.class, n -> simplify(n.longValue()),
      BigDecimal.class, n -> simplify((BigDecimal) n),
      BigInteger.class, n -> simplify((BigInteger) n, true),
      Byte.class, Number::intValue,
      Integer.class, Number::intValue,
      Long.class, n -> simplify(n.longValue()),
      Short.class, Number::intValue,

      Double.class, n -> simplify(n.doubleValue()),
      Float.class, n -> simplify(n.floatValue())
  );


  /**
   * Convert a number into a JsonValue. IEEE floating point numbers may specify "Not A Number", "Positive Infinity", or "Negative Infinity". These three special
   * cases cannot be represented as numbers in JSON and so will result in a NonFiniteNumberException.
   *
   * @param value the numeric value to convert to a canonical JSON value
   *
   * @return the canonical JSON representation
   */
  public static CJNumber cast(Number value) {
    ICache<Number, CJNumber> cache = CacheManager.valueCache();
    return cache.get(value, CJNumber::create);
  }


  /**
   * Cast a JsonNumber to a PNumber.
   *
   * @param jsonNumber the JsonNumber
   *
   * @return the equivalent (or same) PNumber.
   */
  public static CJNumber cast(JsonNumber jsonNumber) {
    if (jsonNumber instanceof CJNumber) {
      return (CJNumber) jsonNumber;
    }

    Number number = jsonNumber.numberValue();
    return cast(number);
  }


  /**
   * Convert a number into a JsonValue. IEEE floating point numbers may specify "Not A Number", "Positive Infinity", or "Negative Infinity". These three special
   * cases cannot be represented as numbers in JSON and so are rendered as Strings.
   *
   * @param value the numeric value to convert to a canonical JSON value
   *
   * @return the canonical JSON representation
   */
  public static Canonical castUnsafe(Number value) {
    ICache<Number, CJNumber> cache = CacheManager.valueCache();
    try {
      return cache.get(value, CJNumber::create);
    } catch (NonFiniteNumberException e) {
      return e.getRepresentation();
    }
  }


  /**
   * Create a JsonNumber from a Number value performing appropriate type simplifications.
   *
   * @param value the value
   *
   * @return the JsonNumber for that value
   */
  private static CJNumber create(Number value) {
    Class<? extends Number> cl = value.getClass();
    UnaryOperator<Number> operator = SIMPLIFIERS.get(cl);
    if (operator == null) {
      throw new IllegalArgumentException("Unknown number class: " + value.getClass());
    }

    Number simple = operator.apply(value);
    cl = simple.getClass();
    Function<Number, CJNumber> function = CREATORS.get(cl);
    if (function != null) {
      return function.apply(simple);
    }
    throw new IllegalArgumentException("Unknown number class: " + value.getClass());
  }


  /**
   * Create a PNumber for an int. The actual type will either be a PInt.
   *
   * @param i the int
   *
   * @return the PNumber
   */
  public static CJNumber create(int i) {
    return new CJInt(i);
  }


  /**
   * Create a PNumber for a long. The actual type will either be a PInt or a PLong depending on the scale of the long.
   *
   * @param l the long
   *
   * @return the PNumber
   */
  public static CJNumber create(long l) {
    if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE) {
      return new CJInt((int) l);
    }
    return new CJLong(l);
  }


  /**
   * Simplify a long into either a long or an int.
   *
   * @param l the long to simplify
   *
   * @return the new representation
   */
  public static Number simplify(long l) {
    if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE) {
      return (int) l;
    }
    return l;
  }


  static Number simplify(double v) {
    if (!Double.isFinite(v)) {
      throw new NonFiniteNumberException(v);
    }
    // try to simplify
    if ((v % 1.0) == 0.0) {
      // it's an integer
      if (Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE) {
        return (int) v;
      }
      if (Long.MIN_VALUE <= v && v <= Long.MAX_VALUE) {
        return (long) v;
      }
      // Have to convert via BigDecimal to allow for numbers like 1E+100
    }
    return simplify(new BigDecimal(Double.toString(v)));
  }


  static Number simplify(float v) {
    if (!Float.isFinite(v)) {
      throw new NonFiniteNumberException(v);
    }

    // try to simplify
    if ((v % 1.0f) == 0.0f) {
      // it's an integer
      if (Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE) {
        return (int) v;
      }
      if (Long.MIN_VALUE <= v && v <= Long.MAX_VALUE) {
        return (long) v;
      }
      // Have to convert via BigDecimal to allow for numbers like 1E+100
    }
    return simplify(new BigDecimal(Float.toString(v)));
  }


  static Number simplify(BigDecimal bigDecimal) {
    bigDecimal = bigDecimal.stripTrailingZeros();
    int s = bigDecimal.scale();
    // if scale is positive, the number is a floating point.
    // if scale is negative, it's an integer, but if it has a lot of trailing zeros, we still use a BigDecimal
    if (CJBigInteger.MIN_SCALE <= s && s <= 0) {
      return simplify(bigDecimal.toBigIntegerExact(), false);
    }
    return bigDecimal;
  }


  static Number simplify(BigInteger bigInteger, boolean checkZeros) {
    int bitLength = bigInteger.bitLength();
    if (bitLength < 32) {
      return bigInteger.intValueExact();
    }
    if (bitLength < 64) {
      return bigInteger.longValueExact();
    }

    // check for trailing zeros
    if (checkZeros && bigInteger.mod(CJBigInteger.MAX_ZEROS).signum() == 0) {
      return new BigDecimal(bigInteger).stripTrailingZeros();
    }

    // It's a big integer after all
    return bigInteger;
  }


  /**
   * Recover a double from a canonical, allowing for NaN, Infinity and -Infinity.
   *
   * @param canonical the value
   *
   * @return the Double, or null if it wasn't a double
   */
  public static Double toDouble(JsonValue canonical) {
    if (canonical.getValueType() == ValueType.NUMBER) {
      return ((JsonNumber) canonical).doubleValue();
    }
    if (canonical instanceof CJString) {
      CJString pString = (CJString) canonical;
      switch (pString.getString().toLowerCase()) {
        case "nan":
          return Double.NaN;
        case "inf": // falls through
        case "+inf": // falls through
        case "infinity": // falls through
        case "+infinity": // falls through
          return Double.POSITIVE_INFINITY;
        case "-inf":
        case "-infinity":
          return Double.NEGATIVE_INFINITY;
        default:
          break;
      }
    }
    return null;
  }


  protected void check() {
    // do nothing
  }


  /**
   * The JSON API requires we test for equality via BigDecimal values. As the canonical JSON does not retain trailing zeros, we actually test for equality by
   * the total ordering of real numbers.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof JsonNumber)) {
      return false;
    }
    if (!(o instanceof CJNumber)) {
      o = cast(((JsonNumber) o).bigDecimalValue());
    }
    // API requires comparison as BigDecimals.
    CJNumber pNumber = (CJNumber) o;
    switch (pNumber.getNumberType()) {
      case TYPE_INT:
        return equalsValue(pNumber.intValue());
      case TYPE_LONG:
        return equalsValue(pNumber.longValue());
      case TYPE_BIG_INT:
        return equalsValue(pNumber.bigIntegerValue());
      default:
        return equalsValue(pNumber.bigDecimalValue());
    }
  }


  protected abstract boolean equalsValue(long other);


  protected abstract boolean equalsValue(BigInteger other);


  protected abstract boolean equalsValue(BigDecimal other);


  public abstract int getNumberType();


  @Override
  public ValueType getValueType() {
    return ValueType.NUMBER;
  }


  @Override
  public int hashCode() {
    return super.hashCode();
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    writer.append(toString());
  }

}
