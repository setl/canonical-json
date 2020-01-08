package io.setl.json.primitive;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.setl.json.Primitive;
import io.setl.json.jackson.PBaseSerializer;
import java.util.Objects;

/**
 * Representation of a value in a JSON object or array.
 */
@JsonSerialize(using = PBaseSerializer.class)
public abstract class PBase implements Primitive {


  /**
   * Get the value encapsulated by this primitive.
   *
   * @param <T>     required type
   * @param reqType the required type
   * @param dflt    default value if type is not correct
   *
   * @return the value
   */
  @Override
  public <T> T getValue(Class<T> reqType, T dflt) {
    Object value = getValue();
    if (reqType.isInstance(value)) {
      return reqType.cast(value);
    }
    return dflt;
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
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (! PBase.class.isInstance(obj)) {
      return false;
    }
    PBase other = (PBase) obj;
    if (getType() != other.getType()) {
      return false;
    }
    return Objects.equals(getValue(),other.getValue());
  }

  @Override
  public int hashCode() {
    return getValue().hashCode();
  }
}
