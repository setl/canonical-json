package io.setl.json.primitive;

import io.setl.json.JType;
import java.io.IOException;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PNull extends PBase {

  public static final PNull NULL = new PNull();


  private PNull() {
    // do nothing
  }


  @Override
  public JType getType() {
    return JType.NULL;
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
    return 0;
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
