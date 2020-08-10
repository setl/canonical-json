package io.setl.json.io;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonException;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JWriterFactory implements JsonWriterFactory {

  @Override
  public JsonWriter createWriter(Writer writer) {
    return new JWriter(writer);
  }


  @Override
  public JsonWriter createWriter(OutputStream out) {
    return new JWriter(new OutputStreamWriter(out, UTF_8));
  }


  @Override
  public JsonWriter createWriter(OutputStream out, Charset charset) {
    if (!UTF_8.equals(charset)) {
      throw new JsonException("Canonical JSON must be in UTF-8");
    }
    return createWriter(out);
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    return Collections.emptyMap();
  }

}
