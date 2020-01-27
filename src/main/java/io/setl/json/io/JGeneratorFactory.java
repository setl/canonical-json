package io.setl.json.io;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class JGeneratorFactory implements JsonGeneratorFactory {

  @Override
  public JsonGenerator createGenerator(Writer writer) {
    // TODO : Implement me! simongreatrix 27/01/2020
    return null;
  }


  @Override
  public JsonGenerator createGenerator(OutputStream out) {
    // TODO : Implement me! simongreatrix 27/01/2020
    return null;
  }


  @Override
  public JsonGenerator createGenerator(OutputStream out, Charset charset) {
    // TODO : Implement me! simongreatrix 27/01/2020
    return null;
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    // TODO : Implement me! simongreatrix 27/01/2020
    return null;
  }
}
