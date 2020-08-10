package io.setl.json.io;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;

import io.setl.json.Primitive;
import io.setl.json.exception.JsonIOException;
import io.setl.json.primitive.PNull;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public abstract class JGenerator implements JsonGenerator {

  protected interface Context {

    default void endChild() {
      // do nothing
    }

    default void startChild() {
      // do nothing - only needed by root
    }

    Context writeEnd();

    void writeKey(String key);

    void writeStart();

    void writeValue(Primitive primitive);

  }



  protected class RootContext implements Context {

    boolean hasWritten = false;


    @Override
    public void endChild() {
      hasWritten = true;
    }


    @Override
    public void startChild() {
      if (hasWritten) {
        throw new JsonGenerationException("Cannot write multiple values in root context");
      }
    }


    public Context writeEnd() {
      throw new JsonGenerationException("Cannot write end in root context");
    }


    @Override
    public void writeKey(String key) {
      throw new JsonGenerationException("Cannot write key in root context");
    }


    public void writeStart() {
      // do nothing - should never be called
    }


    @Override
    public void writeValue(Primitive primitive) {
      if (hasWritten) {
        throw new JsonGenerationException("Cannot write multiple values in root context");
      }
      hasWritten = true;

      try {
        primitive.writeTo(writer);
      } catch (IOException e) {
        throw new JsonIOException(e);
      }
    }

  }



  protected final Writer writer;

  protected Context context;


  protected JGenerator(Writer writer) {
    this.writer = writer;
    context = new RootContext();
  }


  @Override
  public void close() {
    if (!(context instanceof RootContext)) {
      throw new JsonGenerationException("Close attempted with unfinished structures");
    }
    try {
      writer.close();
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  protected abstract Context newArrayContext();

  protected abstract Context newObjectContext();


  @Override
  public JsonGenerator write(JsonValue value) {
    context.writeValue(Primitive.cast(value));
    return this;
  }


  @Override
  public JsonGenerator write(String name, String value) {
    return writeKey(name).write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(String name, BigInteger value) {
    return writeKey(name).write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(String name, BigDecimal value) {
    return writeKey(name).write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(String name, int value) {
    return writeKey(name).write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(String name, long value) {
    return writeKey(name).write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(String name, double value) {
    return writeKey(name).write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(String name, boolean value) {
    return writeKey(name).write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(String value) {
    return write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(BigDecimal value) {
    return write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(BigInteger value) {
    return write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(int value) {
    return write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(long value) {
    return write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(double value) {
    return write(Primitive.create(value));
  }


  @Override
  public JsonGenerator write(String name, JsonValue value) {
    return writeKey(name).write(value);
  }


  @Override
  public JsonGenerator write(boolean value) {
    return write(Primitive.create(value));
  }


  @Override
  public JsonGenerator writeEnd() {
    context = context.writeEnd();
    context.endChild();
    return this;
  }


  @Override
  public JsonGenerator writeKey(String name) {
    context.writeKey(name);
    return this;
  }


  @Override
  public JsonGenerator writeNull(String name) {
    return writeKey(name).write(PNull.NULL);
  }


  @Override
  public JsonGenerator writeNull() {
    return write(PNull.NULL);
  }


  @Override
  public JsonGenerator writeStartArray() {
    context.startChild();
    context = newArrayContext();
    context.writeStart();
    return this;
  }


  @Override
  public JsonGenerator writeStartArray(String name) {
    return writeKey(name).writeStartArray();
  }


  @Override
  public JsonGenerator writeStartObject() {
    context.startChild();
    context = newObjectContext();
    context.writeStart();
    return this;
  }


  @Override
  public JsonGenerator writeStartObject(String name) {
    return writeKey(name).writeStartObject();
  }

}
