package io.setl.json.io;

import javax.json.stream.JsonGenerator;

import io.setl.json.Canonical;

/**
 * A generator that delegates its operations onwards.
 *
 * @author Simon Greatrix on 24/04/2022.
 */
public class DelegatingGenerator<GeneratorType extends DelegatingGenerator<GeneratorType>> implements Generator<GeneratorType> {

  /** The delegate generator. */
  protected JsonGenerator delegate;


  /**
   * Constructor that allows the delegate to be set later.
   */
  protected DelegatingGenerator() {
    // do nothing
  }


  /**
   * New instance.
   *
   * @param delegate the generator delegate
   */
  public DelegatingGenerator(JsonGenerator delegate) {
    this.delegate = delegate;
  }


  @Override
  public void close() {
    delegate.close();
  }


  @Override
  public void flush() {
    delegate.flush();
  }


  /**
   * Get this instance as the correct type.
   *
   * @return this as the correct type
   */
  @SuppressWarnings("unchecked")
  protected GeneratorType me() {
    return (GeneratorType) this;
  }


  @Override
  public GeneratorType write(Canonical value) {
    delegate = delegate.write(value);
    return me();
  }


  @Override
  public GeneratorType writeEnd() {
    delegate = delegate.writeEnd();
    return me();
  }


  @Override
  public GeneratorType writeKey(String name) {
    delegate = delegate.writeKey(name);
    return me();
  }


  @Override
  public GeneratorType writeStartArray() {
    delegate = delegate.writeStartArray();
    return me();
  }


  @Override
  public GeneratorType writeStartObject() {
    delegate = delegate.writeStartObject();
    return me();
  }

}
