package io.setl.json.io;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.JsonException;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class JGeneratorFactory implements JsonGeneratorFactory {

  /** Should the generator trust the client to put keys in canonical order. */
  public static final String TRUST_KEY_ORDER = "setl.json.generator.trustKeyOrder";

  private boolean trustKeyOrder = false;


  /**
   * Create a new factory. The configuration may specify a boolean value for TRUST_KEY_ORDER. If true, the generator will write immediately to the output
   * without buffering, but the client MUST provide Object keys in the correct order.
   *
   * @param config the configuration
   */
  public JGeneratorFactory(Map<String, ?> config) {
    if (config != null && config.containsKey(TRUST_KEY_ORDER)) {
      String val = String.valueOf(config.get(TRUST_KEY_ORDER));
      trustKeyOrder = Boolean.valueOf(val);
    }
  }


  @Override
  public JsonGenerator createGenerator(Writer writer) {
    if (trustKeyOrder) {
      return new JTrustedGenerator(writer);
    }
    return new JSafeGenerator(writer);
  }


  @Override
  public JsonGenerator createGenerator(OutputStream out) {
    return createGenerator(new OutputStreamWriter(out, UTF_8));
  }


  @Override
  public JsonGenerator createGenerator(OutputStream out, Charset charset) {
    if (!UTF_8.equals(charset)) {
      throw new JsonException("Canonical JSON must be in UTF-8");
    }
    return createGenerator(out);
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    return Map.of(TRUST_KEY_ORDER, trustKeyOrder);
  }
}
