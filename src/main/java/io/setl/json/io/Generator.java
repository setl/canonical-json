package io.setl.json.io;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import io.setl.json.Canonical;
import io.setl.json.primitive.CJNull;

/**
 * A JSON generator implementation that directs the convenience methods to the necessary ones.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public interface Generator<GeneratorType extends Generator<GeneratorType>> extends JsonGenerator {

  /**
   * Write the end of a JSON structure.
   *
   * @return this generator
   */
  default GeneratorType end() {
    return writeEnd();
  }

  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the key
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType entry(String name, String value) {
    return write(name, value);
  }

  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the key
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType entry(String name, BigInteger value) {
    return write(name, value);
  }

  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the key
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType entry(String name, BigDecimal value) {
    return write(name, value);
  }

  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the key
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType entry(String name, int value) {
    return write(name, value);
  }

  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the key
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType entry(String name, long value) {
    return write(name, value);
  }

  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the key
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType entry(String name, double value) {
    return write(name, value);
  }

  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the key
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType entry(String name, boolean value) {
    return write(name, value);
  }

  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the key
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType entry(String name, JsonValue value) {
    return write(name, value);
  }


  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the key
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType entry(String name, Canonical value) {
    return write(name, value);
  }


  /**
   * Write a null entry with the given key.
   *
   * @param name the key
   *
   * @return this generator
   */
  default GeneratorType entryNull(String name) {
    return writeNull(name);
  }


  /**
   * Write an object key.
   *
   * @param name the key
   *
   * @return this generator
   */
  default GeneratorType key(String name) {
    return writeKey(name);
  }


  /**
   * Write the start of an array with the given key.
   *
   * @param name the key
   *
   * @return this generator
   */
  default GeneratorType startArray(String name) {
    return writeStartArray(name);
  }


  /**
   * Write the start of an array.
   *
   * @return this generator
   */
  default GeneratorType startArray() {
    return writeStartArray();
  }


  /**
   * Write the start of an object with the give key.
   *
   * @param name the key
   *
   * @return this generator
   */
  default GeneratorType startObject(String name) {
    return writeStartObject(name);
  }


  /**
   * Write the start of an object.
   *
   * @return this generator
   */
  default GeneratorType startObject() {
    return writeStartObject();
  }

  /**
   * Write a value.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  default GeneratorType value(JsonValue value) {
    return write(value);
  }

  /**
   * Write a value.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  default GeneratorType value(String value) {
    return write(value);
  }

  /**
   * Write a value.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  default GeneratorType value(BigDecimal value) {
    return write(value);
  }

  /**
   * Write a value.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  default GeneratorType value(BigInteger value) {
    return write(value);
  }

  /**
   * Write a value.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  default GeneratorType value(int value) {
    return write(value);
  }

  /**
   * Write a value.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  default GeneratorType value(long value) {
    return write(value);
  }

  /**
   * Write a value.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  default GeneratorType value(double value) {
    return write(value);
  }

  /**
   * Write a value.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  default GeneratorType value(boolean value) {
    return write(value);
  }

  /**
   * Write a value.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  default GeneratorType value(Canonical value) {
    return write(value);
  }


  /**
   * Write a null value.
   *
   * @return this generator
   */
  default GeneratorType valueNull() {
    return writeNull();
  }


  @Override
  default GeneratorType write(JsonValue value) {
    return write(Canonical.cast(value));
  }


  /**
   * Write the specified JSON value in the current context.
   *
   * @param value the value to write
   *
   * @return this generator
   */
  GeneratorType write(Canonical value);


  @Override
  default GeneratorType write(String name, String value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(String name, BigInteger value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(String name, BigDecimal value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(String name, int value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(String name, long value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(String name, double value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(String name, boolean value) {
    return writeKey(name).write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(String value) {
    return write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(BigDecimal value) {
    return write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(BigInteger value) {
    return write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(int value) {
    return write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(long value) {
    return write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(double value) {
    return write(Canonical.create(value));
  }


  @Override
  default GeneratorType write(String name, JsonValue value) {
    return writeKey(name).write(value);
  }


  /**
   * Write a JSON name/value pair in the current object context.
   *
   * @param name  the name
   * @param value the value
   *
   * @return this generator
   */
  default GeneratorType write(String name, Canonical value) {
    return writeKey(name).write(value);
  }


  @Override
  default GeneratorType write(boolean value) {
    return write(Canonical.create(value));
  }


  @Override
  GeneratorType writeEnd();


  @Override
  GeneratorType writeKey(String name);


  @Override
  default GeneratorType writeNull(String name) {
    return writeKey(name).write(CJNull.NULL);
  }


  @Override
  default GeneratorType writeNull() {
    return write(CJNull.NULL);
  }


  @Override
  GeneratorType writeStartArray();


  @Override
  default GeneratorType writeStartArray(String name) {
    return writeKey(name).writeStartArray();
  }


  @Override
  GeneratorType writeStartObject();


  @Override
  default GeneratorType writeStartObject(String name) {
    return writeKey(name).writeStartObject();
  }

}
