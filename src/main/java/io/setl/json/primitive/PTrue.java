package io.setl.json.primitive;

import io.setl.json.JType;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PTrue extends PBase {

  public static final PTrue TRUE = new PTrue();


  private PTrue() {
    // do nothing
  }


  @Override
  public JType getType() {
    return JType.BOOLEAN;
  }


  @Override
  public Boolean getValue() {
    return Boolean.TRUE;
  }


  @Override
  public ValueType getValueType() {
    return ValueType.TRUE;
  }


  public String toString() {
    return "true";
  }


  @Override
  public void writeTo(Writer writer) throws IOException {
    writer.write("true");
  }
}
