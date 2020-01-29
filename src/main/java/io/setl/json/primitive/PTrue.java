package io.setl.json.primitive;

import java.io.IOException;
import javax.json.JsonValue;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PTrue extends PBase {

  public static final PTrue TRUE = new PTrue();


  private PTrue() {
    // do nothing
  }


  @Override
  public boolean equals(Object obj) {
    return JsonValue.TRUE.equals(obj);
  }


  @Override
  public Boolean getValue() {
    return Boolean.TRUE;
  }


  @Override
  public ValueType getValueType() {
    return ValueType.TRUE;
  }


  @Override
  public int hashCode() {
    return JsonValue.TRUE.hashCode();
  }


  public String toString() {
    return "true";
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    writer.append("true");
  }
}
