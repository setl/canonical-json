package io.setl.json.io;

import io.setl.json.Primitive;
import io.setl.json.jackson.CanonicalGenerator;
import java.io.IOException;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JWriter implements JsonWriter {

  private final CanonicalGenerator generator;


  JWriter(CanonicalGenerator generator) {
    this.generator = generator;
  }


  @Override
  public void close() {
    try {
      generator.close();
    } catch (IOException e) {
      throw new JsonException("I/O Failure", e);
    }
  }


  @Override
  public void write(JsonValue value) {
    Primitive p = Primitive.create(value);
    try {
      generator.writeRawCanonicalValue(p);
    } catch (IOException e) {
      throw new JsonException("I/O Failure", e);
    }
  }


  @Override
  public void write(JsonStructure value) {
    write((JsonValue) value);
  }


  @Override
  public void writeArray(JsonArray array) {
    write((JsonValue) array);
  }


  @Override
  public void writeObject(JsonObject object) {
    write((JsonValue) object);
  }
}
