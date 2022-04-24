package io.setl.json.io;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * A JSON Generator that produces a byte array.
 *
 * @author Simon Greatrix on 24/04/2022.
 */
public class ByteArrayGenerator extends DelegatingGenerator<ByteArrayGenerator> {

  protected final ByteArrayOutputStream buffer = new ByteArrayOutputStream();


  /**
   * New instance.
   *
   * @param factory the factory used to create the generator.
   */
  public ByteArrayGenerator(GeneratorFactory factory) {
    if (factory != null) {
      delegate = factory.createGenerator(buffer);
    } else {
      delegate = new GeneratorFactory(null).createGenerator(buffer);
    }
  }


  /**
   * New instance.
   *
   * @param config the configuration for the factory used to create the generator.
   */
  public ByteArrayGenerator(Map<String, ?> config) {
    delegate = new GeneratorFactory(config).createGenerator(buffer);
  }


  /**
   * New instance using a default generator factory.
   */

  public ByteArrayGenerator() {
    delegate = new GeneratorFactory(null).createGenerator(buffer);
  }


  public byte[] toByteArray() {
    return buffer.toByteArray();
  }


  public String toString() {
    return new String(toByteArray(), StandardCharsets.UTF_8);
  }

}
