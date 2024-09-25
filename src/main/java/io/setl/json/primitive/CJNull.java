package io.setl.json.primitive;

import java.io.IOException;
import jakarta.json.JsonValue;

/**
 * Representation of "null".
 *
 * @author Simon Greatrix on 08/01/2020.
 */
public class CJNull extends CJBase {

  /** The singleton instance. */
  public static final CJNull NULL = new CJNull();


  private CJNull() {
    // do nothing
  }


  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  @Override
  public boolean equals(Object obj) {
    return JsonValue.NULL.equals(obj);
  }


  @Override
  public Object getValue() {
    return null;
  }


  @Override
  public ValueType getValueType() {
    return ValueType.NULL;
  }


  @Override
  public int hashCode() {
    return JsonValue.NULL.hashCode();
  }


  @Override
  public String toString() {
    return "null";
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    writer.append("null");
  }

}
