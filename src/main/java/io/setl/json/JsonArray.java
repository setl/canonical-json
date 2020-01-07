package io.setl.json;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.setl.json.exception.IncorrectTypeException;
import io.setl.json.exception.MissingItemException;
import io.setl.json.jackson.JsonArraySerializer;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Representation of an array in JSON.
 *
 * <p>No entry in the list can be null. If you try to add one, it will be replaced by a Primitive instance holding a null.
 *
 * <p>As JSON arrays can contain mixed content, this class provides type-checking accessors to the array members. There are multiple varieties of each accessor
 * which obey these contracts:
 *
 * <dl>
 * <dt><code>get<i>Type</i>(index)</code></dt>
 * <dd>
 * <ul>
 * <li>If the index is less than zero, or greater than or equal to the size of this array, returns null.
 * <li>If the entry is not the required type, returns null.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 *
 * <dt><code>get<i>Type</i>(index, default)</code></dt>
 * <dd>
 * <ul>
 * <li>If the index is less than zero, or greater than or equal to the size of this array, returns the default.
 * <li>If the entry is not the required type, returns the default.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * <dt><code>get<i>Type</i>(index, function)</code></dt>
 * <dd>
 * <ul>
 * <li>If the index is less than zero, or greater than or equal to the size of this array, invokes the function to derive a suitable value.
 * <li>If the entry is not the required type, invokes the function to derive a suitable value.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * <dt><code>get<i>Type</i>Safe(index)</code></dt>
 * <dd>
 * <ul>
 * <li>If the index is less than zero, or greater than or equal to the size of this array, throws a <code>MissingItemException</code>.
 * <li>If the entry is not the required type, throws an <code>IncorrectTypeException</code>.
 * <li>Otherwise returns the entry
 * </ul>
 * </dd>
 * </dl>
 *
 * <p>The numeric accessors follow the normal Java rules for primitive type conversions and consider any number to be the correct type. For example, if you
 * call <code>getIntSafe(0)</code> and element 0 contains the Long value 1L<<50, then the call returns the value of Integer.MAX_VALUE, as would be expected
 * for a narrowing primitive conversion, rather than throwing a <code>IncorrectTypeException</code>.
 */
@JsonSerialize(using = JsonArraySerializer.class)
public class JsonArray extends ArrayList<Primitive> implements JsonContainer {

  /** serial version UID. */
  private static final long serialVersionUID = 2L;


  /**
   * Convert a collection into a JsonArray.
   *
   * @param c the collection
   *
   * @return the JsonArray
   */
  static JsonArray fixCollection(Collection<?> c) {
    if (c instanceof JsonArray) {
      return (JsonArray) c;
    }
    JsonArray out = new JsonArray();
    out.ensureCapacity(c.size());
    for (Object o : c) {
      out.add(Primitive.create(o));
    }
    return out;
  }


  private static Primitive fixNull(Primitive element) {
    return element != null ? element : Primitive.NULL;
  }


  /**
   * Ensure a collection contains no actual nulls.
   *
   * @param c the collection
   *
   * @return a collection with the nulls replaced with JSON nulls.
   */
  static Collection<Primitive> fixPrimitiveCollection(Collection<? extends Primitive> c) {
    Primitive[] array = c.toArray(new Primitive[0]);
    for (int i = array.length - 1; i >= 0; i--) {
      if (array[i] == null) {
        array[i] = Primitive.NULL;
      }
    }
    return Arrays.asList(array);
  }


  public JsonArray() {
    // as super-class
  }


  public JsonArray(Collection<?> c) {
    super(fixCollection(c));
  }


  public boolean add(Boolean value) {
    return add(value != null ? (value ? Primitive.TRUE : Primitive.FALSE) : Primitive.NULL);
  }


  public void add(int index, Boolean value) {
    add(index, value != null ? (value ? Primitive.TRUE : Primitive.FALSE) : Primitive.NULL);
  }


  public void add(int index, JsonArray array) {
    add(index, array != null ? new Primitive(Type.ARRAY, array) : Primitive.NULL);
  }


  public void add(int index, JsonObject object) {
    add(index, object != null ? new Primitive(Type.OBJECT, object) : Primitive.NULL);
  }


  public void add(int index, Number number) {
    add(index, number != null ? new Primitive(Type.NUMBER, number) : Primitive.NULL);
  }


  @Override
  public void add(int index, Primitive element) {
    super.add(index, fixNull(element));
  }


  public void add(int index, String string) {
    add(index, string != null ? new Primitive(Type.STRING, string) : Primitive.NULL);
  }


  public boolean add(JsonArray array) {
    return add(array != null ? new Primitive(Type.ARRAY, array) : Primitive.NULL);
  }


  public boolean add(JsonObject object) {
    return add(object != null ? new Primitive(Type.OBJECT, object) : Primitive.NULL);
  }


  public boolean add(Number number) {
    return add(number != null ? new Primitive(Type.NUMBER, number) : Primitive.NULL);
  }


  @Override
  public boolean add(Primitive e) {
    return super.add(fixNull(e));
  }


  public boolean add(String string) {
    return add(string != null ? new Primitive(Type.STRING, string) : Primitive.NULL);
  }


  @Override
  public boolean addAll(Collection<? extends Primitive> c) {
    return super.addAll(fixPrimitiveCollection(c));
  }


  @Override
  public boolean addAll(int index, Collection<? extends Primitive> c) {
    return super.addAll(index, fixPrimitiveCollection(c));
  }


  public boolean addNull() {
    return add(Primitive.NULL);
  }


  public void addNull(int index) {
    add(index, Primitive.NULL);
  }


  /**
   * Get an array from the array.
   *
   * @return the array, or null
   */
  @Nullable
  public JsonArray getArray(int index) {
    return getQuiet(JsonArray.class, index);
  }


  /**
   * Get an array from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the array, or the default
   */
  public JsonArray getArray(int index, IntFunction<JsonArray> dflt) {
    return getQuiet(JsonArray.class, index, dflt);
  }


  /**
   * Get an array from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the array, or the default
   */
  public JsonArray getArray(int index, JsonArray dflt) {
    return getQuiet(JsonArray.class, index, dflt);
  }


  /**
   * Get an array from the array.
   *
   * @return the array
   */
  @Nonnull
  public JsonArray getArraySafe(int index) {
    return getSafe(JsonArray.class, Type.ARRAY, index);
  }


  /**
   * Get a big decimal from the array.
   *
   * @return the big decimal, or null
   */
  @Nullable
  public BigDecimal getBigDecimal(int index) {
    Number n = getQuiet(Number.class, index);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(int index, BigDecimal dflt) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return dflt;
    }
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the big decimal, or the default
   */
  public BigDecimal getBigDecimal(int index, IntFunction<BigDecimal> dflt) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return dflt.apply(index);
    }
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big decimal from the array.
   *
   * @return the big decimal
   */
  @Nonnull
  public BigDecimal getBigDecimalSafe(int index) {
    Number n = getSafe(Number.class, Type.NUMBER, index);
    return Primitive.toBigDecimal(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @return the big integer, or null
   */
  @Nonnull
  public BigInteger getBigInteger(int index) {
    Number n = getQuiet(Number.class, index);
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(int index, BigInteger dflt) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return dflt;
    }
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the big integer, or the default
   */
  public BigInteger getBigInteger(int index, IntFunction<BigInteger> dflt) {
    Number n = getQuiet(Number.class, index);
    if (n == null) {
      return dflt.apply(index);
    }
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a big integer from the array.
   *
   * @return the big integer
   */
  @Nonnull
  public BigInteger getBigIntegerSafe(int index) {
    Number n = getSafe(Number.class, Type.NUMBER, index);
    return Primitive.toBigInteger(n);
  }


  /**
   * Get a Boolean from the array.
   *
   * @return the Boolean, or null
   */
  @Nullable
  public Boolean getBoolean(int index) {
    return getQuiet(Boolean.class, index);
  }


  /**
   * Get a Boolean from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(int index, boolean dflt) {
    return getQuiet(Boolean.class, index, dflt);
  }


  /**
   * Get a Boolean from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the Boolean, or the default
   */
  public boolean getBoolean(int index, IntPredicate dflt) {
    Boolean value = getQuiet(Boolean.class, index);
    return (value != null) ? value : dflt.test(index);
  }


  /**
   * Get a Boolean from the array.
   *
   * @return the Boolean, or null
   */
  @Nonnull
  public Boolean getBooleanSafe(int index) {
    return getSafe(Boolean.class, Type.BOOLEAN, index);
  }


  /**
   * Get a double from the array.
   *
   * @return the double, or null
   */
  @Nullable
  public Double getDouble(int index) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.doubleValue() : null;
  }


  /**
   * Get a double from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the double, or the default
   */
  public double getDouble(int index, double dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.doubleValue() : dflt;
  }


  /**
   * Get a double from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the double, or the default
   */
  public double getDouble(int index, IntToDoubleFunction dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.doubleValue() : dflt.applyAsDouble(index);
  }


  /**
   * Get a double from the array.
   *
   * @return the double
   */
  public double getDoubleSafe(int index) {
    Number n = getSafe(Number.class, Type.NUMBER, index);
    return n.doubleValue();
  }


  /**
   * Get an integer from the array.
   *
   * @return the integer, or null
   */
  @Nullable
  public Integer getInt(int index) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : null;
  }


  /**
   * Get an integer from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the integer, or the default
   */
  public int getInt(int index, int dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : dflt;
  }


  /**
   * Get an integer from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the integer, or the default
   */
  public int getInt(int index, IntUnaryOperator dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.intValue() : dflt.applyAsInt(index);
  }


  /**
   * Get an integer from the array.
   *
   * @return the integer
   */
  public int getIntSafe(int index) {
    return getSafe(Number.class, Type.NUMBER, index).intValue();
  }


  /**
   * Get a long from the array.
   *
   * @return the long, or null
   */
  @Nullable
  public Long getLong(int index) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : null;
  }


  /**
   * Get a long from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the long, or the default
   */
  public long getLong(int index, IntToLongFunction dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : dflt.applyAsLong(index);
  }


  /**
   * Get a long from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the long, or the default
   */
  public long getLong(int index, long dflt) {
    Number n = getQuiet(Number.class, index);
    return (n != null) ? n.longValue() : dflt;
  }


  /**
   * Get a long from the array.
   *
   * @return the long
   */
  public long getLongSafe(int index) {
    Number n = getSafe(Number.class, Type.NUMBER, index);
    return n.longValue();
  }


  /**
   * Get an object from the array.
   *
   * @return the object, or null
   */
  @Nullable
  public JsonObject getObject(int index) {
    return getQuiet(JsonObject.class, index);
  }


  /**
   * Get an object from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the object, or the default
   */
  public JsonObject getObject(int index, IntFunction<JsonObject> dflt) {
    return getQuiet(JsonObject.class, index, dflt);
  }


  /**
   * Get an object from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the object, or the default
   */
  public JsonObject getObject(int index, JsonObject dflt) {
    return getQuiet(JsonObject.class, index, dflt);
  }


  /**
   * Get an object from the array.
   *
   * @return the object
   */
  @Nonnull
  public JsonObject getObjectSafe(int index) {
    return getSafe(JsonObject.class, Type.OBJECT, index);
  }


  private <T> T getQuiet(Class<T> clazz, int index) {
    return getQuiet(clazz, index, (IntFunction<T>) i -> null);
  }


  private <T> T getQuiet(Class<T> clazz, int index, IntFunction<T> function) {
    if (index < 0 || size() <= index) {
      return function.apply(index);
    }
    Object value = get(index).getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    return function.apply(index);
  }


  private <T> T getQuiet(Class<T> clazz, int index, T dflt) {
    return getQuiet(clazz, index, (IntFunction<T>) i -> dflt);
  }


  private <T> T getSafe(Class<T> clazz, Type type, int index) {
    if (index < 0 || size() <= index) {
      throw new MissingItemException(index, type);
    }
    Primitive primitive = get(index);
    Object value = primitive.getValue();
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    throw new IncorrectTypeException(index, type, primitive.getType());
  }


  /**
   * Get a String from the array.
   *
   * @return the String, or null
   */
  @Nullable
  public String getString(int index) {
    return getQuiet(String.class, index);
  }


  /**
   * Get a String from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the String, or the default
   */
  public String getString(int index, IntFunction<String> dflt) {
    return getQuiet(String.class, index, dflt);
  }


  /**
   * Get a String from the array.
   *
   * @param index the index
   * @param dflt  the default
   *
   * @return the String, or the default
   */
  public String getString(int index, String dflt) {
    return getQuiet(String.class, index, dflt);
  }


  /**
   * Get a String from the array.
   *
   * @return the String
   */
  @Nonnull
  public String getStringSafe(int index) {
    return getSafe(String.class, Type.STRING, index);
  }


  @Override
  public Type getType() {
    return Type.ARRAY;
  }


  public boolean isArray() {
    return true;
  }


  @Override
  public void replaceAll(UnaryOperator<Primitive> operator) {
    super.replaceAll(p -> fixNull(operator.apply(p)));
  }


  @Override
  @Nonnull
  public Primitive set(int index, Primitive element) {
    return super.set(index, fixNull(element));
  }


  @Nonnull
  public Primitive set(int index, JsonArray array) {
    Primitive p = (array != null) ? new Primitive(Type.ARRAY, array) : Primitive.NULL;
    return super.set(index, p);
  }


  @Nonnull
  public Primitive set(int index, JsonObject object) {
    Primitive p = (object != null) ? new Primitive(Type.OBJECT, object) : Primitive.NULL;
    return super.set(index, p);
  }


  @Nonnull
  public Primitive set(int index, Boolean value) {
    Primitive p = (value != null) ? (value ? Primitive.TRUE : Primitive.FALSE) : Primitive.NULL;
    return super.set(index, p);
  }


  @Nonnull
  public Primitive set(int index, Number number) {
    Primitive p = (number != null) ? new Primitive(Type.NUMBER, number) : Primitive.NULL;
    return super.set(index, p);
  }


  @Nonnull
  public Primitive set(int index, String string) {
    Primitive p = (string != null) ? new Primitive(Type.STRING, string) : Primitive.NULL;
    return super.set(index, p);
  }


  @Nonnull
  public Primitive setNull(int index) {
    return super.set(index, Primitive.NULL);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    for (Primitive e : this) {
      buf.append(String.valueOf(e));
      buf.append(',');
    }
    // remove final comma
    if (buf.length() > 1) {
      buf.setLength(buf.length() - 1);
    }
    buf.append(']');
    return buf.toString();
  }


  @Override
  public void writeTo(Writer writer) throws IOException {
    writer.write('[');
    int length = size();
    for (int i = 0; i < length; i++) {
      if (i > 0) {
        writer.write(',');
      }
      get(i).writeTo(writer);
    }
    writer.write(']');
  }
}
