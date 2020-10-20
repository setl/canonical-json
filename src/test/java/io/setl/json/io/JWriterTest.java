package io.setl.json.io;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import org.junit.Test;
import org.mockito.Mockito;

import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PTrue;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class JWriterTest {


  @Test
  public void close() throws IOException {
    Writer writer = Mockito.mock(Writer.class);
    JWriter jWriter = new JWriter(writer);
    jWriter.close();
    Mockito.verify(writer).close();
  }


  @Test(expected = JsonException.class)
  public void close2() throws IOException {
    Writer writer = Mockito.mock(Writer.class);
    Mockito.doThrow(new IOException()).when(writer).close();
    JWriter jWriter = new JWriter(writer);
    jWriter.close();
  }


  @Test(expected = IllegalStateException.class)
  public void close3() {
    Writer writer = Mockito.mock(Writer.class);
    JWriter jWriter = new JWriter(writer);
    jWriter.close();
    jWriter.write(PNull.NULL);
  }


  @Test
  public void emptyConfig() {
    assertTrue(new JWriterFactory().getConfigInUse().isEmpty());
  }


  @Test(expected = JsonException.class)
  public void failedWrite() throws IOException {
    Writer writer = Mockito.mock(Writer.class);
    Mockito.doThrow(new IOException()).when(writer).append(any(String.class));
    JWriter jWriter = new JWriter(writer);
    jWriter.write(PFalse.FALSE);
  }


  @Test(expected = JsonException.class)
  public void utf8Only() {
    new JWriterFactory().createWriter(new ByteArrayOutputStream(), ISO_8859_1);
  }


  @Test
  public void write() {
    StringWriter writer = new StringWriter();
    JsonWriter jWriter = new JWriterFactory().createWriter(writer);
    jWriter.write(PTrue.TRUE);
    jWriter.close();
    assertEquals("true", writer.toString());
  }


  @Test
  public void writeArray() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonWriter jWriter = new JWriterFactory().createWriter(outputStream, UTF_8);
    JsonArray array = new JArrayBuilder().add("à").add("ç").build();
    jWriter.writeArray(array);
    jWriter.close();
    String out = new String(outputStream.toByteArray(), UTF_8);
    assertEquals("[\"à\",\"ç\"]", out);
  }


  @Test
  public void writeObject() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonWriter jWriter = new JWriterFactory().createWriter(outputStream);
    JsonObject jObject = new JObjectBuilder().add("à", "ç").build();
    jWriter.writeObject(jObject);
    jWriter.close();
    String out = new String(outputStream.toByteArray(), UTF_8);
    assertEquals("{\"à\":\"ç\"}", out);
  }


  @Test
  public void writeStructure() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonWriter jWriter = new JWriterFactory().createWriter(outputStream);
    JsonObject jObject = new JObjectBuilder().add("à", "ç").build();
    jWriter.write(jObject);
    jWriter.close();
    String out = new String(outputStream.toByteArray(), UTF_8);
    assertEquals("{\"à\":\"ç\"}", out);
  }

}
