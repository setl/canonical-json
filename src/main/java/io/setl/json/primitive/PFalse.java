package io.setl.json.primitive;

import java.io.IOException;
import javax.json.JsonValue;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PFalse extends PBase implements PBoolean {

  public static final PFalse FALSE = new PFalse();


  private PFalse() {
    // do nothing
  }


  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  @Override
  public boolean equals(Object obj) {
    return JsonValue.FALSE.equals(obj);
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
  public int hashCode() {
    return JsonValue.FALSE.hashCode();
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
