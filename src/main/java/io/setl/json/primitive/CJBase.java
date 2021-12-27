package io.setl.json.primitive;

import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.setl.json.Canonical;
import io.setl.json.jackson.CJBaseSerializer;

/**
 * Representation of a value in a JSON object or array.
 */
@JsonSerialize(using = CJBaseSerializer.class)
public abstract class CJBase implements Canonical {

  /**
   * Returns this. As most Canonicals are not mutable, this is the sensible default.
   *
   * @return this
   */
  @Override
  public Canonical copy() {
    return this;
  }


  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other instanceof Canonical) {
      return Objects.equals(getValue(), ((Canonical) other).getValue());
    }
    return false;
  }


  /**
   * Get the value encapsulated by this.
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
   * Get the value encapsulated by this. Throws a ClassCastException if the type is incorrect.
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
