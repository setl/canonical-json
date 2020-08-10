package io.setl.json.primitive;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.setl.json.Primitive;
import io.setl.json.jackson.PBaseSerializer;

/**
 * Representation of a value in a JSON object or array.
 */
@JsonSerialize(using = PBaseSerializer.class)
public abstract class PBase implements Primitive {

  /**
   * Returns this. As most primitives are not mutable, this is the sensible default.
   *
   * @return this
   */
  @Override
  public Primitive copy() {
    return this;
  }


  /**
   * Get the value encapsulated by this primitive.
   *
   * @param <T>          required type
   * @param reqType      the required type
   * @param defaultValue default value if type is not correct
   *
   * @return the value
   */
  @Override
  public <T> T getValue(Class<T> reqType, T defaultValue) {
    Object value = getValue();
    if (reqType.isInstance(value)) {
      return reqType.cast(value);
    }
    return defaultValue;
  }


  /**
   * Get the value encapsulated by this primitive. Throws a ClassCastException if the type is incorrect.
   *
   * @param <T>     required type
   * @param reqType the required type
   *
   * @return the value
   */
  @Override
  public <T> T getValueSafe(Class<T> reqType) {
    return reqType.cast(getValue());
  }


  @Override
  public int hashCode() {
    return getValue().hashCode();
  }

}
