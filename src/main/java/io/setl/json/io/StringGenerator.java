package io.setl.json.io;

import java.io.StringWriter;
import java.util.Map;

/**
 * A JSON Generator that produces a String.
 *
 * @author Simon Greatrix on 24/04/2022.
 */
public class StringGenerator extends DelegatingGenerator<StringGenerator> {

  protected final StringWriter writer = new StringWriter();


  /**
   * New instance.
   *
   * @param factory factory used to create the generator.
   */
  public StringGenerator(GeneratorFactory factory) {
    if (factory != null) {
      delegate = factory.createGenerator(writer);
    } else {
      delegate = new GeneratorFactory(null).createGenerator(writer);
    }
  }


  /**
   * New instance.
   *
   * @param config configuration for the factory used to create the generator.
   */
  public StringGenerator(Map<String, ?> config) {
    delegate = new GeneratorFactory(config).createGenerator(writer);
  }


  /**
   * New instance using a default generator factory.
   */
  public StringGenerator() {
    delegate = new GeneratorFactory(null).createGenerator(writer);
  }


  public String toString() {
    return writer.toString();
  }

}
