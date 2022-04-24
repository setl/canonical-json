package io.setl.json.io;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.setl.json.CanonicalJsonProvider;
import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.primitive.CJFalse;
import io.setl.json.primitive.CJNull;
import io.setl.json.primitive.CJTrue;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class CJWriterTest {


  @Test
  public void close() throws IOException {
    JsonGenerator writer = Mockito.mock(JsonGenerator.class);
    CJWriter jWriter = new CJWriter(writer);
    jWriter.close();
    Mockito.verify(writer).close();
  }


  @Test
  public void close2() throws IOException {
    JsonGenerator writer = Mockito.mock(JsonGenerator.class);
    Mockito.doThrow(new JsonException("test")).when(writer).close();
    CJWriter jWriter = new CJWriter(writer);
    JsonException e = assertThrows(JsonException.class, () -> jWriter.close());
    assertEquals("test", e.getMessage());
  }


  @Test
  public void close3() {
    JsonGenerator writer = Mockito.mock(JsonGenerator.class);
    CJWriter jWriter = new CJWriter(writer);
    jWriter.close();
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> jWriter.write(CJNull.NULL));
    assertEquals("This JsonWriter has already been used", e.getMessage());
  }


  @Test
  public void failedWrite() throws IOException {
    JsonGenerator writer = Mockito.mock(JsonGenerator.class);
    Mockito.doThrow(new JsonException("test")).when(writer).write(any(JsonValue.class));
    CJWriter jWriter = new CJWriter(writer);
    JsonException e = assertThrows(JsonException.class, () -> jWriter.write(CJFalse.FALSE));
    assertEquals("test", e.getMessage());
  }


  @Test
  public void utf8Only() {
    WriterFactory factory = new WriterFactory(CanonicalJsonProvider.provider().createGeneratorFactory(null));
    JsonException e = assertThrows(JsonException.class, () -> factory.createWriter(new ByteArrayOutputStream(), ISO_8859_1));
    assertEquals("Canonical JSON must be in UTF-8", e.getMessage());
  }


  @Test
  public void write() {
    StringWriter writer = new StringWriter();
    JsonWriter jWriter = new WriterFactory(CanonicalJsonProvider.provider().createGeneratorFactory(null)).createWriter(writer);
    jWriter.write(CJTrue.TRUE);
    jWriter.close();
    assertEquals("true", writer.toString());
  }


  @Test
  public void writeArray() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonWriter jWriter = new WriterFactory(CanonicalJsonProvider.provider().createGeneratorFactory(null)).createWriter(outputStream, UTF_8);
    JsonArray array = new ArrayBuilder().add("à").add("ç").build();
    jWriter.writeArray(array);
    jWriter.close();
    String out = new String(outputStream.toByteArray(), UTF_8);
    assertEquals("[\"à\",\"ç\"]", out);
  }


  @Test
  public void writeObject() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonWriter jWriter = new WriterFactory(CanonicalJsonProvider.provider().createGeneratorFactory(null)).createWriter(outputStream);
    JsonObject jObject = new ObjectBuilder().add("à", "ç").build();
    jWriter.writeObject(jObject);
    jWriter.close();
    String out = new String(outputStream.toByteArray(), UTF_8);
    assertEquals("{\"à\":\"ç\"}", out);
  }


  @Test
  public void writeStructure() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonWriter jWriter = new WriterFactory(CanonicalJsonProvider.provider().createGeneratorFactory(null)).createWriter(outputStream);
    JsonObject jObject = new ObjectBuilder().add("à", "ç").build();
    jWriter.write(jObject);
    jWriter.close();
    String out = new String(outputStream.toByteArray(), UTF_8);
    assertEquals("{\"à\":\"ç\"}", out);
  }

}
