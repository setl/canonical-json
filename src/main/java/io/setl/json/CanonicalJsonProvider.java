package io.setl.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonMergePatch;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonPatch;
import javax.json.JsonPatchBuilder;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

import io.setl.json.builder.BuilderFactory;
import io.setl.json.io.GeneratorFactory;
import io.setl.json.io.ReaderFactory;
import io.setl.json.io.WriterFactory;
import io.setl.json.merge.Merge;
import io.setl.json.merge.MergeDiff;
import io.setl.json.parser.Parser;
import io.setl.json.parser.ParserFactory;
import io.setl.json.patch.Patch;
import io.setl.json.patch.PatchBuilder;
import io.setl.json.patch.PatchFactory;
import io.setl.json.pointer.PointerFactory;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * The provider.
 *
 * @author Simon Greatrix on 15/01/2020.
 */
public class CanonicalJsonProvider extends JsonProvider {

  static final GeneratorFactory CANONICAL_GENERATOR_FACTORY = new GeneratorFactory(Map.of(
      GeneratorFactory.TRUST_KEY_ORDER, true
  ));

  static final GeneratorFactory PRETTY_GENERATOR_FACTORY = new GeneratorFactory(Map.of(
      JsonGenerator.PRETTY_PRINTING, true,
      GeneratorFactory.TRUST_KEY_ORDER, true,
      GeneratorFactory.SMALL_STRUCTURE_LIMIT, 20
  ));

  static boolean isToStringPretty = false;


  public static boolean isToStringPretty() {
    return isToStringPretty;
  }


  public static void setIsToStringPretty(boolean isToStringPretty) {
    CanonicalJsonProvider.isToStringPretty = isToStringPretty;
  }


  @Override
  public JsonArrayBuilder createArrayBuilder(JsonArray array) {
    return createBuilderFactory(null).createArrayBuilder(array);
  }


  @Override
  public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
    return createBuilderFactory(null).createArrayBuilder(collection);
  }


  @Override
  public JsonArrayBuilder createArrayBuilder() {
    return new BuilderFactory().createArrayBuilder();
  }


  @Override
  public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
    // Our ArrayBuilder and ObjectBuilder do not take any configuration, so we discard what was specified.
    return new BuilderFactory();
  }


  @Override
  public JsonPatch createDiff(JsonStructure source, JsonStructure target) {
    return PatchFactory.create(source, target);
  }


  @Override
  public JsonGenerator createGenerator(Writer writer) {
    return createGeneratorFactory(null).createGenerator(writer);
  }


  @Override
  public JsonGenerator createGenerator(OutputStream out) {
    return createGeneratorFactory(null).createGenerator(out);
  }


  @Override
  public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
    return new GeneratorFactory(config);
  }


  @Override
  public JsonMergePatch createMergeDiff(JsonValue source, JsonValue target) {
    return MergeDiff.create(source, target);
  }


  @Override
  public JsonMergePatch createMergePatch(JsonValue patch) {
    return new Merge(patch);
  }


  @Override
  public JsonObjectBuilder createObjectBuilder(JsonObject object) {
    return createBuilderFactory(null).createObjectBuilder(object);
  }


  @Override
  public JsonObjectBuilder createObjectBuilder(Map<String, Object> map) {
    return createBuilderFactory(null).createObjectBuilder(map);
  }


  @Override
  public JsonObjectBuilder createObjectBuilder() {
    return new BuilderFactory().createObjectBuilder();
  }


  @Override
  public JsonParser createParser(Reader reader) {
    return new Parser(reader);
  }


  @Override
  public JsonParser createParser(InputStream in) {
    return createParserFactory(null).createParser(in);
  }


  @Override
  public JsonParserFactory createParserFactory(Map<String, ?> config) {
    return new ParserFactory(config);
  }


  @Override
  public JsonPatch createPatch(JsonArray array) {
    return new Patch(array);
  }


  @Override
  public JsonPatchBuilder createPatchBuilder() {
    return createPatchBuilder(null);
  }


  @Override
  public JsonPatchBuilder createPatchBuilder(JsonArray array) {
    return new PatchBuilder(array);
  }


  @Override
  public JsonPointer createPointer(String jsonPointer) {
    return PointerFactory.create(jsonPointer);
  }


  @Override
  public JsonReader createReader(Reader reader) {
    return createReaderFactory(null).createReader(reader);
  }


  @Override
  public JsonReader createReader(InputStream in) {
    return createReaderFactory(null).createReader(in);
  }


  @Override
  public JsonReaderFactory createReaderFactory(Map<String, ?> config) {
    // Configuration is ignored
    return new ReaderFactory();
  }


  @Override
  public JsonString createValue(String value) {
    return CJString.create(value);
  }


  @Override
  public JsonNumber createValue(int value) {
    return CJNumber.create(value);
  }


  @Override
  public JsonNumber createValue(long value) {
    return CJNumber.create(value);
  }


  @Override
  public JsonNumber createValue(double value) {
    return CJNumber.cast(value);
  }


  @Override
  public JsonNumber createValue(BigDecimal value) {
    return CJNumber.cast(value);
  }


  @Override
  public JsonNumber createValue(BigInteger value) {
    return CJNumber.cast(value);
  }


  @Override
  public JsonWriter createWriter(Writer writer) {
    return createWriterFactory(null).createWriter(writer);
  }


  @Override
  public JsonWriter createWriter(OutputStream out) {
    return createWriterFactory(null).createWriter(out);
  }


  @Override
  public JsonWriterFactory createWriterFactory(Map<String, ?> config) {
    return new WriterFactory(createGeneratorFactory(config));
  }

}
