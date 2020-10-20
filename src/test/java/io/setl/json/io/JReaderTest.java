package io.setl.json.io;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.setl.json.primitive.PTrue;
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

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class JReaderTest {

  @Test
  public void close() throws IOException {
    Reader reader = Mockito.mock(Reader.class);
    JReader jReader = new JReaderFactory().createReader(reader);
    jReader.close();
    Mockito.verify(reader, Mockito.atLeastOnce()).close();
  }


  @Test(expected = JsonParsingException.class)
  public void close2() {
    JReader jReader = new JReaderFactory().createReader(new StringReader("   true   false"));
    jReader.readValue();
    jReader.close();
  }

  @Test(expected = JsonParsingException.class)
  public void close3() throws IOException {
    Reader reader = Mockito.mock(Reader.class);
    Mockito.doThrow(new IOException()).when(reader).close();
    JReader jReader = new JReaderFactory().createReader(reader);
    jReader.close();
  }

  @Test
  public void emptyConfig() {
    assertTrue(new JReaderFactory().getConfigInUse().isEmpty());
  }


  @Test
  public void read() {
    JReader jReader = new JReaderFactory().createReader(new ByteArrayInputStream("{}".getBytes(UTF_8)));
    JsonStructure structure = jReader.read();
    jReader.close();

    assertTrue(structure instanceof JsonObject);
  }


  @Test(expected = JsonParsingException.class)
  public void read2() {
    JReader jReader = new JReaderFactory().createReader(new ByteArrayInputStream("true".getBytes(UTF_8)));
    jReader.read();
  }


  @Test
  public void readArray() {
    JReader jReader = new JReaderFactory().createReader(new StringReader("[1,2,3]"));
    JsonArray array = jReader.readArray();
    jReader.close();

    assertEquals(3, array.size());
  }


  @Test(expected = JsonParsingException.class)
  public void readArray2() {
    JReader jReader = new JReaderFactory().createReader(new ByteArrayInputStream("true".getBytes(UTF_8)));
    jReader.readArray();
  }


  @Test
  public void readObject() {
    JReader jReader = new JReaderFactory().createReader(new ByteArrayInputStream("{\"é\":\"ü\"}".getBytes(ISO_8859_1)), ISO_8859_1);
    JsonObject object = jReader.readObject();
    jReader.close();

    assertEquals(1, object.size());
    assertEquals("ü", object.getString("é"));
  }


  @Test(expected = JsonParsingException.class)
  public void readObject2() {
    JReader jReader = new JReaderFactory().createReader(new ByteArrayInputStream("true".getBytes(UTF_8)));
    jReader.readObject();
  }


  @Test
  public void readValue() {
    JReader jReader = new JReaderFactory().createReader(new StringReader("   true   "));
    JsonValue value = jReader.readValue();
    jReader.close();

    assertEquals(PTrue.TRUE, value);
  }


  @Test(expected = IllegalStateException.class)
  public void readValue2() {
    JReader jReader = new JReaderFactory().createReader(new StringReader("   true   "));
    jReader.readValue();
    jReader.readValue();
  }


  @Test(expected = JsonParsingException.class)
  public void readValue3() {
    JReader jReader = new JReaderFactory().createReader(new StringReader("   true   false"));
    JsonValue value = jReader.readValue();
    jReader.close();
  }
}