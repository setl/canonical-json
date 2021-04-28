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
 * A factory for creating JSON Generator instances.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class GeneratorFactory implements JsonGeneratorFactory {

  /** The maximum character size for a small structure which will be printed without new-lines. */
  public static final String SMALL_STRUCTURE_LIMIT = "setl.json.generator.smallStructureLimit";

  /** Should the generator trust the client to put keys in canonical order. */
  public static final String TRUST_KEY_ORDER = "setl.json.generator.trustKeyOrder";

  private boolean prettyPrinting = false;

  private int smallStructureLimit = 30;

  private boolean trustKeyOrder = false;


  /**
   * Create a new factory. The configuration may specify a boolean value for TRUST_KEY_ORDER. If true, the generator will write immediately to the output
   * without buffering, but the client MUST provide Object keys in the correct order.
   *
   * @param config the configuration
   */
  public GeneratorFactory(Map<String, ?> config) {
    if (config != null) {
      if (config.containsKey(TRUST_KEY_ORDER)) {
        String val = String.valueOf(config.get(TRUST_KEY_ORDER));
        trustKeyOrder = Boolean.valueOf(val);
      }
      if (config.containsKey(JsonGenerator.PRETTY_PRINTING)) {
        // The specification says that the value can be anything without saying what any value should mean. For us, anything other than "false" turns it on.
        String val = String.valueOf(config.get(TRUST_KEY_ORDER));
        prettyPrinting = !val.equalsIgnoreCase("false");
      }
      if (config.containsKey(SMALL_STRUCTURE_LIMIT)) {
        Object o = config.get(SMALL_STRUCTURE_LIMIT);
        if (o instanceof Number) {
          smallStructureLimit = Math.max(0, ((Number) o).intValue());
        }
        if (o instanceof String) {
          try {
            smallStructureLimit = Integer.parseInt((String) o);
          } catch (NumberFormatException e) {
            smallStructureLimit = ((String) o).length();
          }
        }
      }
    }
  }


  Formatter createFormatter(Appendable appendable) {
    if (prettyPrinting) {
      return new PrettyFormatter(appendable, smallStructureLimit);
    }
    return new NoOpFormatter(appendable);
  }


  /**
   * Create a generator encapsulating the given appendable.
   *
   * @param appendable the appendable
   *
   * @return the generator
   */
  public Generator createGenerator(Appendable appendable) {
    if (trustKeyOrder) {
      return new TrustedGenerator(createFormatter(appendable));
    }
    return new SafeGenerator(createFormatter(appendable));
  }


  @Override
  public Generator createGenerator(OutputStream out) {
    return createGenerator((Appendable) new OutputStreamWriter(out, UTF_8));
  }


  @Override
  public Generator createGenerator(OutputStream out, Charset charset) {
    if (!UTF_8.equals(charset)) {
      throw new JsonException("Canonical JSON must be in UTF-8");
    }
    return createGenerator(out);
  }


  @Override
  public Generator createGenerator(Writer writer) {
    return createGenerator((Appendable) writer);
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    if (prettyPrinting) {
      return Map.of(TRUST_KEY_ORDER, trustKeyOrder, JsonGenerator.PRETTY_PRINTING, prettyPrinting, SMALL_STRUCTURE_LIMIT, smallStructureLimit);
    }
    return Map.of(TRUST_KEY_ORDER, trustKeyOrder, JsonGenerator.PRETTY_PRINTING, prettyPrinting);
  }

}
