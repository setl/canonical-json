package io.setl.json;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import org.junit.jupiter.api.Test;

import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.BuilderFactory;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.io.CJReader;
import io.setl.json.io.CJWriter;
import io.setl.json.io.Generator;
import io.setl.json.io.GeneratorFactory;
import io.setl.json.io.ReaderFactory;
import io.setl.json.io.WriterFactory;
import io.setl.json.merge.Merge;
import io.setl.json.parser.Parser;
import io.setl.json.parser.ParserFactory;
import io.setl.json.patch.Patch;
import io.setl.json.patch.PatchBuilder;
import io.setl.json.pointer.Pointer;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.numbers.CJBigDecimal;
import io.setl.json.primitive.numbers.CJBigInteger;
import io.setl.json.primitive.numbers.CJInt;
import io.setl.json.primitive.numbers.CJLong;

/**
 * @author Simon Greatrix on 28/01/2020.
 */
public class CanonicalJsonProviderTest {

  @Test
  public void createArrayBuilder() {
    assertTrue(Json.createArrayBuilder() instanceof ArrayBuilder);
  }


  @Test
  public void createArrayBuilderCollection() {
    assertTrue(Json.createArrayBuilder(Arrays.asList(1, 2, 3)) instanceof ArrayBuilder);
  }


  @Test
  public void createArrayBuilderJsonArray() {
    assertTrue(Json.createArrayBuilder(JsonValue.EMPTY_JSON_ARRAY) instanceof ArrayBuilder);
  }


  @Test
  public void createBuilderFactory() {
    assertTrue(Json.createBuilderFactory(null) instanceof BuilderFactory);
  }


  @Test
  public void createDiff() {
    assertTrue(Json.createDiff(JsonValue.EMPTY_JSON_OBJECT, JsonValue.EMPTY_JSON_OBJECT) instanceof Patch);
  }


  @Test
  public void createGenerator() {
    assertTrue(Json.createGenerator(Writer.nullWriter()) instanceof Generator);
  }


  @Test
  public void createGeneratorFactory() {
    assertTrue(Json.createGeneratorFactory(Map.of("a", 1)) instanceof GeneratorFactory);
  }


  @Test
  public void createGeneratorOutputStream() {
    assertTrue(Json.createGenerator(OutputStream.nullOutputStream()) instanceof Generator);
  }


  @Test
  public void createMergeDiff() {
    assertTrue(Json.createMergeDiff(JsonValue.EMPTY_JSON_OBJECT, JsonValue.EMPTY_JSON_OBJECT) instanceof Merge);
  }


  @Test
  public void createMergePatch() {
    assertTrue(Json.createMergePatch(JsonValue.EMPTY_JSON_OBJECT) instanceof Merge);
  }


  @Test
  public void createObjectBuilder() {
    assertTrue(Json.createObjectBuilder() instanceof ObjectBuilder);
  }


  @Test
  public void createObjectBuilderJsonBuilder() {
    assertTrue(Json.createObjectBuilder(JsonValue.EMPTY_JSON_OBJECT) instanceof ObjectBuilder);
  }


  @Test
  public void createObjectBuilderMap() {
    assertTrue(Json.createObjectBuilder(Map.of("b", 2)) instanceof ObjectBuilder);
  }


  @Test
  public void createParserFactory() {
    assertTrue(Json.createParserFactory(null) instanceof ParserFactory);
  }


  @Test
  public void createParserInputStream() {
    assertTrue(Json.createParser(InputStream.nullInputStream()) instanceof Parser);
  }


  @Test
  public void createParserReader() {
    assertTrue(Json.createParser(Reader.nullReader()) instanceof Parser);
  }


  @Test
  public void createPatch() {
    assertTrue(Json.createPatch(JsonValue.EMPTY_JSON_ARRAY) instanceof Patch);
  }


  @Test
  public void createPatchBuilder() {
    assertTrue(Json.createPatchBuilder() instanceof PatchBuilder);
  }


  @Test
  public void createPatchBuilderJsonArray() {
    assertTrue(Json.createPatchBuilder(JsonValue.EMPTY_JSON_ARRAY) instanceof PatchBuilder);
  }


  @Test
  public void createPointer() {
    assertTrue(Json.createPointer("/wibble") instanceof Pointer);
  }


  @Test
  public void createReaderFactory() {
    assertTrue(Json.createReaderFactory(null) instanceof ReaderFactory);
  }


  @Test
  public void createReaderInputStream() {
    assertTrue(Json.createReader(InputStream.nullInputStream()) instanceof CJReader);
  }


  @Test
  public void createReaderReader() {
    assertTrue(Json.createReader(Reader.nullReader()) instanceof CJReader);
  }


  @Test
  public void createValueBigDecimal() {
    assertTrue(Json.createValue(BigDecimal.valueOf(0.35)) instanceof CJBigDecimal);
  }


  @Test
  public void createValueBigInteger() {
    assertTrue(Json.createValue(BigInteger.ONE.shiftLeft(70)) instanceof CJBigInteger);
  }


  @Test
  public void createValueDouble() {
    assertTrue(Json.createValue(1.0E6) instanceof CJInt);
    assertTrue(Json.createValue(0.35) instanceof CJBigDecimal);
  }


  @Test
  public void createValueInt() {
    assertTrue(Json.createValue(1) instanceof CJInt);
  }


  @Test
  public void createValueLong() {
    assertTrue(Json.createValue(10000000001L) instanceof CJLong);
    assertTrue(Json.createValue(100000001L) instanceof CJInt);
  }


  @Test
  public void createValueString() {
    assertTrue(Json.createValue("wibble") instanceof CJString);
  }


  @Test
  public void createWriterFactory() {
    assertTrue(Json.createWriterFactory(null) instanceof WriterFactory);
  }


  @Test
  public void createWriterOutputStream() {
    assertTrue(Json.createWriter(OutputStream.nullOutputStream()) instanceof CJWriter);
  }


  @Test
  public void createWriterWriter() {
    assertTrue(Json.createWriter(Writer.nullWriter()) instanceof CJWriter);
  }


  @Test
  public void provider() {
    JsonProvider provider = JsonProvider.provider();
    assertTrue(provider instanceof CanonicalJsonProvider);
  }

}
