package io.setl.json.io;

import java.io.IOException;

import io.setl.json.io.PrettyOutput.Special;
import io.setl.json.primitive.CJBase;
import io.setl.json.primitive.CJString;

/**
 * A JSON formatter which does pretty printing.
 *
 * @author Simon Greatrix on 18/11/2020.
 */
public class PrettyFormatter implements Formatter, Appendable {

  private PrettyOutput prettyOutput;


  public PrettyFormatter(Appendable appendable, int smallStructureLimit) {
    prettyOutput = new AppendableOutput(appendable, smallStructureLimit);
  }


  @Override
  public Appendable append(CharSequence csq) {
    prettyOutput = prettyOutput.append(csq);
    return prettyOutput;
  }


  @Override
  public Appendable append(CharSequence csq, int start, int end) {
    prettyOutput = prettyOutput.append(csq, start, end);
    return prettyOutput;
  }


  @Override
  public Appendable append(char c) {
    prettyOutput = prettyOutput.append(c);
    return prettyOutput;
  }


  @Override
  public void close() {
    prettyOutput.close();
  }


  @Override
  public void flush() {
    prettyOutput = prettyOutput.flush();
  }


  @Override
  public void write(CJBase value) {
    try {
      value.writeTo(this);
    } catch (IOException exception) {
      throw new InternalError("Impossible IOException", exception);
    }
  }


  @Override
  public void writeArrayEnd() {
    prettyOutput = prettyOutput.append(Special.END_ARRAY);
  }


  @Override
  public void writeArrayStart() {
    prettyOutput = prettyOutput.append(Special.START_ARRAY);
  }


  @Override
  public void writeColon() {
    prettyOutput = prettyOutput.append(": ");
  }


  @Override
  public void writeComma() {
    prettyOutput = prettyOutput.append(Special.SEPARATOR);
  }


  @Override
  public void writeKey(String key) {
    try {
      CJString.format(this, key);
    } catch (IOException exception) {
      throw new InternalError("Impossible IOException", exception);
    }
  }


  @Override
  public void writeObjectEnd() {
    prettyOutput = prettyOutput.append(Special.END_OBJECT);
  }


  @Override
  public void writeObjectStart() {
    prettyOutput = prettyOutput.append(Special.START_OBJECT);
  }

}
