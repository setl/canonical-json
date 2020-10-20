package io.setl.json.primitive;

import java.io.IOException;
import javax.json.JsonValue;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PNull extends PBase {

  public static final PNull NULL = new PNull();


  private PNull() {
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
