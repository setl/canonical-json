package io.setl.json.io;

import io.setl.json.Primitive;
import io.setl.json.exception.JsonIOException;
import java.io.IOException;
import java.io.Writer;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JWriter implements JsonWriter {

  private final Writer writer;

  private boolean isUsed = false;


  JWriter(Writer writer) {
    this.writer = writer;
  }


  private void checkUsed() {
    if (isUsed) {
      throw new IllegalStateException("This JsonWriter has already been used");
    }
    isUsed = true;
  }


  @Override
  public void close() {
    isUsed = true;
    try {
      writer.close();
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  @Override
  public void write(JsonValue value) {
    checkUsed();
    Primitive p = Primitive.create(value);
    try {
      p.writeTo(writer);
    } catch (IOException e) {
      throw new JsonIOException(e);
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
