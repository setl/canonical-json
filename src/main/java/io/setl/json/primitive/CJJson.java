package io.setl.json.primitive;

import java.io.IOException;

/**
 * A special primitive used to represent a fragment of a canonical JSON document that has already been converted to its textual form.
 *
 * @author Simon Greatrix on 08/01/2020.
 */
public class CJJson extends CJBase {

  private final String json;


  /**
   * New instance.
   *
   * @param json the JSON which must be in canonical form.
   */
  public CJJson(String json) {
    this.json = json;
  }


  @Override
  public boolean equals(Object other) {
    return super.equals(other);
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
  public int hashCode() {
    return super.hashCode();
  }


  @Override
  public String toString() {
    return json;
  }


  @Override
  public void writeTo(Appendable writer) throws IOException {
    writer.append(json);
  }

}
