package io.setl.json.primitive;

import io.setl.json.JType;
import java.io.IOException;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PFalse extends PBase {

  public static final PFalse FALSE = new PFalse();


  private PFalse() {
    // do nothing
  }


  @Override
  public JType getType() {
    return JType.BOOLEAN;
  }


  @Override
  public Boolean getValue() {
    return Boolean.FALSE;
  }


  @Override
  public ValueType getValueType() {
    return ValueType.FALSE;
  }


  @Override
  public String toString() {
    return "false";
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    writer.append("false");
  }
}
