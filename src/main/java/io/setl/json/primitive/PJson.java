package io.setl.json.primitive;

import io.setl.json.JType;
import java.io.IOException;
import java.io.Writer;

/**
 * A special primitive used to represent a fragment of a canonical JSON document that has already been converted to its textual form.
 *
 * @author Simon Greatrix on 08/01/2020.
 */
public class PJson extends PBase {

  private final String json;


  public PJson(String json) {
    this.json = json;
  }


  @Override
  public JType getType() {
    return JType.JSON;
  }


  @Override
  public Object getValue() {
    return json;
  }


  @Override
  public ValueType getValueType() {
    return null;
  }


  @Override
  public String toString() {
    return json;
  }


  @Override
  public void writeTo(Writer writer) throws IOException {
    writer.write(json);
  }
}
