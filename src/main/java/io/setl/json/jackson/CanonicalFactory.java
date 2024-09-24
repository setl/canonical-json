package io.setl.json.jackson;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.IOContext;

import io.setl.json.io.Utf8Writer;

/**
 * A Jackson JSON output factory that writes canonical JSON.
 */
public class CanonicalFactory extends JsonFactory {

  /** New instance using standard settings. */
  public CanonicalFactory() {
    // as super-class
  }


  /** New instance using the provided code.
   *
   * @param objectCodec the codec to use
   */
  public CanonicalFactory(ObjectCodec objectCodec) {
    super(objectCodec);
  }


  @Override
  protected JsonGenerator _createGenerator(Writer out, IOContext ioContext) {
    return new CanonicalGenerator(ioContext, _generatorFeatures, _objectCodec, out);
  }


  @Override
  protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ioContext) {
    return new CanonicalGenerator(ioContext, _generatorFeatures, _objectCodec, new Utf8Writer(out));
  }


  /**
   * Just like regular JSON, canonical JSON cannot handle binary natively.
   *
   * @return false
   */
  @Override
  public boolean canHandleBinaryNatively() {
    return false;
  }


  @Override
  public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
    if (enc != JsonEncoding.UTF8) {
      throw new UnsupportedOperationException("Canonical encoding must be UTF-8, not " + enc);
    }

    return super.createGenerator(out, enc);
  }


  @Override
  public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
    if (enc != JsonEncoding.UTF8) {
      throw new UnsupportedOperationException("Canonical encoding must be UTF-8, not " + enc);
    }
    return super.createGenerator(f, enc);
  }


  @Override
  public JsonFactory disable(JsonGenerator.Feature f) {
    if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES
        || f == JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS
    ) {
      throw new UnsupportedOperationException("Feature " + f + " may not be disabled for Canonical JSON");
    }
    return super.disable(f);
  }


  @Override
  public JsonFactory enable(JsonGenerator.Feature f) {
    if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII
        || f == JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN
        || f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS
    ) {
      throw new UnsupportedOperationException("Feature " + f + " may not be enabled for Canonical JSON");
    }
    return super.enable(f);
  }


  @Override
  public String getFormatName() {
    return FORMAT_NAME_JSON;
  }


  /**
   * Unlike regular JSON, canonical JSON requires a fixed ordering.
   *
   * @return true
   */
  @Override
  public boolean requiresPropertyOrdering() {
    return true;
  }


  @Override
  public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
    throw new UnsupportedOperationException("Canonical JSON must use standard escapes");
  }

}
