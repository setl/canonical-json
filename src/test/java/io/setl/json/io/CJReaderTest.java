package io.setl.json.io;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

import org.junit.Test;
import org.mockito.Mockito;

import io.setl.json.primitive.CJTrue;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class CJReaderTest {

  @Test
  public void close() throws IOException {
    Reader reader = Mockito.mock(Reader.class);
    CJReader jReader = new ReaderFactory().createReader(reader);
    jReader.close();
    Mockito.verify(reader, Mockito.atLeastOnce()).close();
  }


  @Test(expected = JsonParsingException.class)
  public void close2() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("   true   false"));
    reader.readValue();
    reader.close();
  }


  @Test(expected = JsonParsingException.class)
  public void close3() throws IOException {
    Reader reader = Mockito.mock(Reader.class);
    Mockito.doThrow(new IOException()).when(reader).close();
    CJReader jReader = new ReaderFactory().createReader(reader);
    jReader.close();
  }


  @Test
  public void emptyConfig() {
    assertTrue(new ReaderFactory().getConfigInUse().isEmpty());
  }


  @Test
  public void read() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("{}".getBytes(UTF_8)));
    JsonStructure structure = reader.read();
    reader.close();

    assertTrue(structure instanceof JsonObject);
  }


  @Test(expected = JsonParsingException.class)
  public void read2() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("true".getBytes(UTF_8)));
    reader.read();
  }


  @Test
  public void readArray() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("[1,2,3]"));
    JsonArray array = reader.readArray();
    reader.close();

    assertEquals(3, array.size());
  }


  @Test(expected = JsonParsingException.class)
  public void readArray2() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("true".getBytes(UTF_8)));
    reader.readArray();
  }


  @Test
  public void readObject() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("{\"é\":\"ü\"}".getBytes(ISO_8859_1)), ISO_8859_1);
    JsonObject object = reader.readObject();
    reader.close();

    assertEquals(1, object.size());
    assertEquals("ü", object.getString("é"));
  }


  @Test(expected = JsonParsingException.class)
  public void readObject2() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("true".getBytes(UTF_8)));
    reader.readObject();
  }


  @Test
  public void readValue() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("   true   "));
    JsonValue value = reader.readValue();
    reader.close();

    assertEquals(CJTrue.TRUE, value);
  }


  @Test(expected = IllegalStateException.class)
  public void readValue2() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("   true   "));
    reader.readValue();
    reader.readValue();
  }


  @Test(expected = JsonParsingException.class)
  public void readValue3() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("   true   false"));
    JsonValue value = reader.readValue();
    reader.close();
  }

}
