package io.setl.json;

import io.setl.json.builder.JBuilderFactory;
import io.setl.json.io.JGeneratorFactory;
import io.setl.json.io.JReaderFactory;
import io.setl.json.io.JWriterFactory;
import io.setl.json.merge.JMerge;
import io.setl.json.merge.MergeDiff;
import io.setl.json.parser.JParser;
import io.setl.json.parser.JParserFactory;
import io.setl.json.patch.JPatch;
import io.setl.json.patch.JPatchBuilder;
import io.setl.json.patch.PatchDiff;
import io.setl.json.pointer.JPointerFactory;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.numbers.PNumber;
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

/**
 * @author Simon Greatrix on 15/01/2020.
 */
public class JProvider extends JsonProvider {

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
    return new JBuilderFactory().createArrayBuilder();
  }


  @Override
  public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
    return new JBuilderFactory();
  }


  @Override
  public JsonPatch createDiff(JsonStructure source, JsonStructure target) {
    return PatchDiff.create(source, target);
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
    return new JGeneratorFactory(config);
  }


  @Override
  public JsonMergePatch createMergeDiff(JsonValue source, JsonValue target) {
    return MergeDiff.create(source, target);
  }


  @Override
  public JsonMergePatch createMergePatch(JsonValue patch) {
    return new JMerge(patch);
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
    return new JBuilderFactory().createObjectBuilder();
  }


  @Override
  public JsonParser createParser(Reader reader) {
    return new JParser(reader);
  }


  @Override
  public JsonParser createParser(InputStream in) {
    return createParserFactory(null).createParser(in);
  }


  @Override
  public JsonParserFactory createParserFactory(Map<String, ?> config) {
    return new JParserFactory(config);
  }


  @Override
  public JsonPatch createPatch(JsonArray array) {
    return new JPatch(array);
  }


  @Override
  public JsonPatchBuilder createPatchBuilder() {
    return createPatchBuilder(null);
  }


  @Override
  public JsonPatchBuilder createPatchBuilder(JsonArray array) {
    return new JPatchBuilder();
  }


  @Override
  public JsonPointer createPointer(String jsonPointer) {
    return JPointerFactory.create(jsonPointer);
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
    return new JReaderFactory();
  }


  @Override
  public JsonString createValue(String value) {
    return PString.create(value);
  }


  @Override
  public JsonNumber createValue(int value) {
    return PNumber.create(value);
  }


  @Override
  public JsonNumber createValue(long value) {
    return PNumber.create(value);
  }


  @Override
  public JsonNumber createValue(double value) {
    return PNumber.create(value);
  }


  @Override
  public JsonNumber createValue(BigDecimal value) {
    return PNumber.create(value);
  }


  @Override
  public JsonNumber createValue(BigInteger value) {
    return PNumber.create(value);
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
    return new JWriterFactory();
  }
}
