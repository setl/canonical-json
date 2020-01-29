package io.setl.json;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JBuilderFactory;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.io.JGenerator;
import io.setl.json.io.JGeneratorFactory;
import io.setl.json.io.JReader;
import io.setl.json.io.JReaderFactory;
import io.setl.json.io.JWriter;
import io.setl.json.io.JWriterFactory;
import io.setl.json.parser.JParser;
import io.setl.json.parser.JParserFactory;
import io.setl.json.pointer.JPointer;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.numbers.PBigDecimal;
import io.setl.json.primitive.numbers.PBigInteger;
import io.setl.json.primitive.numbers.PInt;
import io.setl.json.primitive.numbers.PLong;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import org.junit.Test;

/**
 * @author Simon Greatrix on 28/01/2020.
 */
public class JProviderTest {

  @Test
  public void createArrayBuilder() {
    assertTrue(Json.createArrayBuilder() instanceof JArrayBuilder);
  }


  @Test
  public void createArrayBuilderCollection() {
    assertTrue(Json.createArrayBuilder(Arrays.asList(1, 2, 3)) instanceof JArrayBuilder);
  }


  @Test
  public void createArrayBuilderJsonArray() {
    assertTrue(Json.createArrayBuilder(JsonValue.EMPTY_JSON_ARRAY) instanceof JArrayBuilder);
  }


  @Test
  public void createBuilderFactory() {
    assertTrue(Json.createBuilderFactory(null) instanceof JBuilderFactory);
  }


  @Test
  public void createDiff() {
    fail();
  }


  @Test
  public void createGenerator() {
    assertTrue(Json.createGenerator(Writer.nullWriter()) instanceof JGenerator);
  }


  @Test
  public void createGeneratorFactory() {
    assertTrue(Json.createGeneratorFactory(Map.of("a", 1)) instanceof JGeneratorFactory);
  }


  @Test
  public void createGeneratorOutputStream() {
    assertTrue(Json.createGenerator(OutputStream.nullOutputStream()) instanceof JGenerator);
  }


  @Test
  public void createMergeDiff() {
    fail();
  }


  @Test
  public void createMergePatch() {
    fail();
  }


  @Test
  public void createObjectBuilder() {
    assertTrue(Json.createObjectBuilder() instanceof JObjectBuilder);
  }


  @Test
  public void createObjectBuilderJsonBuilder() {
    assertTrue(Json.createObjectBuilder(JsonValue.EMPTY_JSON_OBJECT) instanceof JObjectBuilder);
  }


  @Test
  public void createObjectBuilderMap() {
    assertTrue(Json.createObjectBuilder(Map.of("b", 2)) instanceof JObjectBuilder);
  }


  @Test
  public void createParserFactory() {
    assertTrue(Json.createParserFactory(null) instanceof JParserFactory);
  }


  @Test
  public void createParserInputStream() {
    assertTrue(Json.createParser(InputStream.nullInputStream()) instanceof JParser);
  }


  @Test
  public void createParserReader() {
    assertTrue(Json.createParser(Reader.nullReader()) instanceof JParser);
  }


  @Test
  public void createPatch() {
    fail();
  }


  @Test
  public void createPatchBuilder() {
    fail();
  }


  @Test
  public void createPatchBuilderJsonArray() {
    fail();
  }


  @Test
  public void createPointer() {
    assertTrue(Json.createPointer("/wibble") instanceof JPointer);
  }


  @Test
  public void createReaderFactory() {
    assertTrue(Json.createReaderFactory(null) instanceof JReaderFactory);
  }


  @Test
  public void createReaderReader() {
    assertTrue(Json.createReader(Reader.nullReader()) instanceof JReader);
  }


  @Test
  public void createReaderInputStream() {
    assertTrue(Json.createReader(InputStream.nullInputStream()) instanceof JReader);
  }


  @Test
  public void createValueBigDecimal() {
    assertTrue(Json.createValue(BigDecimal.valueOf(0.35)) instanceof PBigDecimal);
  }


  @Test
  public void createValueBigInteger() {
    assertTrue(Json.createValue(BigInteger.ONE.shiftLeft(70)) instanceof PBigInteger);
  }


  @Test
  public void createValueDouble() {
    assertTrue(Json.createValue(1.0E6) instanceof PInt);
    assertTrue(Json.createValue(0.35) instanceof PBigDecimal);
  }


  @Test
  public void createValueInt() {
    assertTrue(Json.createValue(1) instanceof PInt);
  }


  @Test
  public void createValueLong() {
    assertTrue(Json.createValue(10000000001L) instanceof PLong);
    assertTrue(Json.createValue(100000001L) instanceof PInt);
  }


  @Test
  public void createValueString() {
    assertTrue(Json.createValue("wibble") instanceof PString);
  }


  @Test
  public void createWriterFactory() {
    assertTrue(Json.createWriterFactory(null) instanceof JWriterFactory);
  }


  @Test
  public void createWriterOutputStream() {
    assertTrue(Json.createWriter(OutputStream.nullOutputStream()) instanceof JWriter);
  }


  @Test
  public void createWriterWriter() {
    assertTrue(Json.createWriter(Writer.nullWriter()) instanceof JWriter);
  }


  @Test
  public void provider() {
    JsonProvider provider = JsonProvider.provider();
    assertTrue(provider instanceof JProvider);
  }
}