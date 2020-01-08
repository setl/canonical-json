package io.setl.json.primitive;

import io.setl.json.Canonical;
import io.setl.json.JType;
import java.io.IOException;
import java.io.Writer;
import javax.json.JsonString;

/**
 * @author Simon Greatrix on 08/01/2020.
 */
public class PString extends PBase implements JsonString {

  private final String value;


  public PString(String value) {
    this.value = value;
  }


  @Override
  public CharSequence getChars() {
    return value;
  }


  @Override
  public String getString() {
    return value;
  }


  @Override
  public JType getType() {
    return JType.STRING;
  }


  @Override
  public Object getValue() {
    return value;
  }


  @Override
  public ValueType getValueType() {
    return ValueType.STRING;
  }


  @Override
  public String toString() {
    return Canonical.format(value);
  }


  @Override
  public void writeTo(Writer writer) throws IOException {
    Canonical.format(writer, value);
  }
}
