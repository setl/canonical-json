package io.setl.json.io;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonConfig;
import jakarta.json.JsonConfig.KeyStrategy;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParsingException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.setl.json.CJObject;
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


  @Test
  public void close2() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("   true   false"));
    reader.readValue();
    assertThrows(JsonParsingException.class, () -> reader.close());
  }


  @Test
  public void close3() throws IOException {
    Reader reader = Mockito.mock(Reader.class);
    Mockito.doThrow(new IOException()).when(reader).close();
    CJReader jReader = new ReaderFactory().createReader(reader);
    assertThrows(JsonParsingException.class, () -> jReader.close());
  }


  @Test
  public void read() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("{}".getBytes(UTF_8)));
    JsonStructure structure = reader.read();
    reader.close();

    assertTrue(structure instanceof JsonObject);
  }


  @Test
  public void read2() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("true".getBytes(UTF_8)));
    assertThrows(JsonParsingException.class, () -> reader.read());
  }


  @Test
  public void readArray() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("[1,2,3]"));
    JsonArray array = reader.readArray();
    reader.close();

    assertEquals(3, array.size());
  }


  @Test
  public void readArray2() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("true".getBytes(UTF_8)));
    assertThrows(JsonParsingException.class, () -> reader.readArray());
  }


  @Test
  public void readObject() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("{\"é\":\"ü\"}".getBytes(ISO_8859_1)), ISO_8859_1);
    JsonObject object = reader.readObject();
    reader.close();

    assertEquals(1, object.size());
    assertEquals("ü", object.getString("é"));
  }


  @Test
  public void readObject2() {
    CJReader reader = new ReaderFactory().createReader(new ByteArrayInputStream("true".getBytes(UTF_8)));
    assertThrows(JsonParsingException.class, () -> reader.readObject());
  }


  @Test
  public void readValue() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("   true   "));
    JsonValue value = reader.readValue();
    reader.close();

    assertEquals(CJTrue.TRUE, value);
  }


  @Test
  public void readValue2() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("   true   "));
    reader.readValue();
    assertThrows(IllegalStateException.class, () -> reader.readValue());
  }


  @Test
  public void readValue3() {
    CJReader reader = new ReaderFactory().createReader(new StringReader("   true   false"));
    JsonValue value = reader.readValue();
    JsonParsingException e = assertThrows(JsonParsingException.class, () -> reader.close());
    assertEquals("Saw 'f' after root value.", e.getMessage());
  }


  @Test
  public void singleEntryInConfig() {
    assertEquals(1, new ReaderFactory().getConfigInUse().size());
  }

  @Test
  public void keyStrategyFirst1() {
    ReaderFactory factory = new ReaderFactory(Map.of(JsonConfig.KEY_STRATEGY, KeyStrategy.FIRST));
    CJReader reader = factory.createReader(new StringReader("{ \"a\":1, \"a\":2 }"));
    JsonObject object =reader.readObject();
    assertEquals(1, object.getInt("a"));
  }

  @Test
  public void keyStrategyFirst2() {
    ReaderFactory factory = new ReaderFactory(Map.of(JsonConfig.KEY_STRATEGY, "First"));
    CJReader reader = factory.createReader(new StringReader("{ \"a\":1, \"a\":2 }"));
    JsonObject object =reader.readObject();
    assertEquals(1, object.getInt("a"));
  }


  @Test
  public void keyStrategyLast() {
    ReaderFactory factory = new ReaderFactory(Map.of(JsonConfig.KEY_STRATEGY, KeyStrategy.LAST));
    CJReader reader = factory.createReader(new StringReader("{ \"a\":1, \"a\":2 }"));
    JsonObject object =reader.readObject();
    assertEquals(2, object.getInt("a"));
  }


  @Test
  public void keyStrategyNone() {
    ReaderFactory factory = new ReaderFactory(Map.of(JsonConfig.KEY_STRATEGY, KeyStrategy.NONE));
    CJReader reader = factory.createReader(new StringReader("{ \"a\":1, \"a\":2 }"));
    assertThrows(JsonParsingException.class, () -> reader.readObject());
  }


  @Test
  public void keyStrategyDefault() {
    ReaderFactory factory = new ReaderFactory(Map.of(JsonConfig.KEY_STRATEGY, "Default"));
    CJReader reader = factory.createReader(new StringReader("{ \"a\":1, \"a\":2 }"));
    JsonObject object =reader.readObject();
    assertEquals(2, object.getInt("a"));
  }
}
