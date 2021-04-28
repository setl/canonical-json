package io.setl.json.jackson;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonWriteContext;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.Canonical;
import io.setl.json.primitive.CJFalse;
import io.setl.json.primitive.CJJson;
import io.setl.json.primitive.CJNull;
import io.setl.json.primitive.CJTrue;

/**
 * Generator for canonical JSON. Note that as the canonical form requires a specific ordering of object properties, no output is created until the root object
 * is complete.
 *
 * @author Simon Greatrix on 16/09/2019.
 */
public class CanonicalGenerator extends JsonGenerator {

  private static final int DISALLOWED_FEATURES = Feature.WRITE_NUMBERS_AS_STRINGS.getMask()
      + Feature.WRITE_BIGDECIMAL_AS_PLAIN.getMask()
      + Feature.ESCAPE_NON_ASCII.getMask();


  private static final int REQUIRED_FEATURES = Feature.QUOTE_FIELD_NAMES.getMask()
      + Feature.QUOTE_NON_NUMERIC_NUMBERS.getMask();

  private static final int DEFAULT_FEATURE_MASK = Feature.AUTO_CLOSE_TARGET.getMask()
      + Feature.AUTO_CLOSE_JSON_CONTENT.getMask()
      + Feature.FLUSH_PASSED_TO_STREAM.getMask()
      + REQUIRED_FEATURES;



  interface Container {

    void add(String key, Canonical value);

    void set(JsonWriteContext parent, Canonical raw);

    /**
     * Write the container.
     *
     * @param writer the writer
     */
    void writeTo(Writer writer) throws IOException;

  }



  static class ArrayContainer implements Container {

    final CJArray array = new CJArray();


    @Override
    public void add(String key, Canonical value) {
      array.add(value);
    }


    @Override
    public void set(JsonWriteContext parent, Canonical raw) {
      array.set(parent.getCurrentIndex(), raw);
    }


    @Override
    public void writeTo(Writer writer) throws IOException {
      array.writeTo(writer);
    }

  }



  static class ObjectContainer implements Container {

    final CJObject object = new CJObject();


    @Override
    public void add(String key, Canonical value) {
      object.put(key, value);
    }


    @Override
    public void set(JsonWriteContext parent, Canonical raw) {
      object.put(parent.getCurrentName(), raw);
    }


    @Override
    public void writeTo(Writer writer) throws IOException {
      object.writeTo(writer);
    }

  }



  static class RawContainer implements Container {

    private final String raw;


    RawContainer(String raw) {
      this.raw = raw;
    }


    @Override
    public void add(String key, Canonical value) {
      throw new UnsupportedOperationException("Raw containers cannot be added to");
    }


    @Override
    public void set(JsonWriteContext parent, Canonical raw) {
      throw new UnsupportedOperationException("Raw containers cannot be reset.");
    }


    @Override
    public void writeTo(Writer writer) throws IOException {
      writer.write(raw);
    }

  }



  private final boolean isResourceManaged;

  private final LinkedList<Container> stack = new LinkedList<>();

  private final Writer writer;

  private boolean closed = false;

  private int featureMask = DEFAULT_FEATURE_MASK;

  private ObjectCodec objectCodec;

  private JsonWriteContext writeContext;


  /**
   * New instance.
   *
   * @param ioContext   the context
   * @param features    the generator features that are enabled
   * @param objectCodec the object codec
   * @param writer      the output's writer
   */
  public CanonicalGenerator(IOContext ioContext, int features, ObjectCodec objectCodec, Writer writer) {
    isResourceManaged = ioContext.isResourceManaged();
    this.objectCodec = objectCodec;
    this.writer = writer;

    for (Feature f : Feature.values()) {
      int mask = f.getMask();
      if ((features & mask) != 0) {
        enable(f);
      }
    }

    DupDetector detector = Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features)
        ? DupDetector.rootDetector(this) : null;
    writeContext = JsonWriteContext.createRootContext(detector);
  }


  /**
   * New instance.
   *
   * @param writer            the output's writer
   * @param isResourceManaged should closing this generator close the output writer?
   */
  public CanonicalGenerator(Writer writer, boolean isResourceManaged) {
    this.isResourceManaged = isResourceManaged;
    objectCodec = null;
    this.writer = writer;
    writeContext = JsonWriteContext.createRootContext(null);
  }


  @Override
  public void close() throws IOException {
    if (closed) {
      return;
    }

    closed = true;

    if (isEnabled(Feature.AUTO_CLOSE_JSON_CONTENT)) {
      while (true) {
        JsonStreamContext context = getOutputContext();
        if (context.inArray()) {
          writeEndArray();
        } else if (context.inObject()) {
          writeEndObject();
        } else {
          break;
        }
      }
    }

    if (isResourceManaged || isEnabled(Feature.AUTO_CLOSE_TARGET)) {
      writer.close();
    } else if (isEnabled(Feature.FLUSH_PASSED_TO_STREAM)) {
      writer.flush();
    }
  }


  @Override
  public JsonGenerator disable(Feature f) {
    if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES
        || f == JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS
    ) {
      throw new UnsupportedOperationException("Feature " + f + " may not be disabled for Canonical JSON");
    }
    featureMask &= ~f.getMask();
    return this;
  }


  @Override
  public JsonGenerator enable(Feature f) {
    if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII
        || f == JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN
        || f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS
    ) {
      throw new UnsupportedOperationException("Feature " + f + " may not be enabled for Canonical JSON");
    }
    featureMask |= f.getMask();
    return this;
  }


  @Override
  public void flush() throws IOException {
    if (isEnabled(Feature.FLUSH_PASSED_TO_STREAM)) {
      writer.flush();
    }
  }


  @Override
  public ObjectCodec getCodec() {
    return objectCodec;
  }


  @Override
  public int getFeatureMask() {
    return featureMask;
  }


  @Override
  public JsonStreamContext getOutputContext() {
    return writeContext;
  }


  @Override
  public boolean isClosed() {
    return closed;
  }


  @Override
  public boolean isEnabled(Feature f) {
    return (featureMask & f.getMask()) != 0;
  }


  private void rawNotSupported() {
    throw new UnsupportedOperationException("Canonical JSON does not support raw content");
  }


  @Override
  public JsonGenerator setCodec(ObjectCodec oc) {
    objectCodec = oc;
    return this;
  }


  /**
   * {@inheritDoc}
   * .
   *
   * @deprecated Deprecated in parent class
   */
  @Deprecated(since = "always")
  @Override
  public JsonGenerator setFeatureMask(int values) {
    if ((values & DISALLOWED_FEATURES) != 0) {
      throw new UnsupportedOperationException("Contains disallowed features: " + values);
    }
    if ((values & REQUIRED_FEATURES) != REQUIRED_FEATURES) {
      throw new UnsupportedOperationException("Does not contain required features: " + values);
    }
    featureMask = values;
    return this;
  }


  @Override
  public JsonGenerator useDefaultPrettyPrinter() {
    throw new UnsupportedOperationException("Pretty printing is not allowed for canonical form");
  }


  protected final void verifyValueWrite(String typeMsg) throws IOException {
    final int status = writeContext.writeValue();
    switch (status) {
      case JsonWriteContext.STATUS_OK_AFTER_SPACE: // root-value separator
        writer.write(' ');
        break;
      case JsonWriteContext.STATUS_EXPECT_NAME:
        _reportError(String.format("Can not %s, expecting field name (context: %s)",
            typeMsg, writeContext.typeDesc()
        ));
        break;
      default:
        break;
    }
  }


  @Override
  public Version version() {
    return JsonModule.LIBRARY_VERSION;
  }


  @Override
  public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) {
    try {
      writeBinary(bv, new ByteArrayInputStream(data, offset, len), len);
    } catch (IOException e) {
      throw new InternalError("IO Exception without I/O", e);
    }
  }


  @Override
  public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException {
    // Jackson's Base64Variant class forces callers to do most of the encoding themselves.
    int length = 0;
    StringBuilder buffer = new StringBuilder();
    if (dataLength > 0) {
      // Add 3/8 overhead as a convenient estimate of 1/3
      buffer.ensureCapacity(dataLength + (dataLength >>> 2) + (dataLength >>> 3));
    }

    final int chunksBeforeLF = bv.getMaxLineLength() >> 2;
    int chunksLeft = chunksBeforeLF;
    int bits24;
    int extraBytes;
    outer:
    while (true) {
      // attempt to read 3 bytes
      bits24 = 0;
      for (int i = 2; i >= 0; i--) {
        int r = data.read();
        if (r == -1) {
          extraBytes = 2 - i;
          break outer;
        }
        length++;
        bits24 |= r << (i << 3);
      }

      if (dataLength >= 0 && length > dataLength) {
        throw new IOException("Data length exceeded");
      }
      bv.encodeBase64Chunk(buffer, bits24);

      chunksLeft--;
      if (chunksLeft <= 0) {
        // This is incorrect, but consistent with Jackson's handling. It is incorrect because (a) the line breaks should be CR+LF, and (b) encodings that
        // should not have line breaks get them if the data exceeds Integer.MAX_VALUE characters.
        buffer.append('\n');
        chunksLeft = chunksBeforeLF;
      }
    }

    if (dataLength >= 0 && length > dataLength) {
      throw new IOException("Data length exceeded");
    }
    if (extraBytes > 0) {
      bv.encodeBase64Partial(buffer, bits24, extraBytes);
    }

    writeCanonical(Canonical.create(buffer.toString()));
    return length;
  }


  @Override
  public void writeBoolean(boolean state) throws IOException {
    writeCanonical(state ? CJTrue.TRUE : CJFalse.FALSE);
  }


  private void writeCanonical(Canonical canonical) throws IOException {
    ValueType valueType = canonical.getValueType();
    String typeMessage = valueType == null ? "RAW" : valueType.name();
    verifyValueWrite(typeMessage);
    if (stack.isEmpty()) {
      canonical.writeTo(writer);
      return;
    }
    Container container = stack.peek();
    container.add(writeContext.getCurrentName(), canonical);
  }


  @Override
  public void writeEndArray() throws IOException {
    if (!writeContext.inArray()) {
      _reportError("Current context not Array but " + writeContext.typeDesc());
    }
    writeContext = writeContext.clearAndGetParent();
    Container c = stack.pop();
    if (stack.isEmpty()) {
      c.writeTo(writer);
    }
  }


  @Override
  public void writeEndObject() throws IOException {
    if (!writeContext.inObject()) {
      _reportError("Current context not Object but " + writeContext.typeDesc());
    }
    writeContext = writeContext.clearAndGetParent();
    Container c = stack.pop();
    if (stack.isEmpty()) {
      c.writeTo(writer);
    }
  }


  @Override
  public void writeFieldName(String name) throws IOException {
    int status = writeContext.writeFieldName(name);
    if (status == JsonWriteContext.STATUS_EXPECT_VALUE) {
      _reportError("Can not write a field name, expecting a value");
    }
  }


  @Override
  public void writeFieldName(SerializableString name) throws IOException {
    writeFieldName(name.getValue());
  }


  @Override
  public void writeNull() throws IOException {
    writeCanonical(CJNull.NULL);
  }


  @Override
  public void writeNumber(int v) throws IOException {
    writeCanonical(Canonical.create(v));
  }


  @Override
  public void writeNumber(long v) throws IOException {
    writeCanonical(Canonical.create(v));
  }


  @Override
  public void writeNumber(BigInteger v) throws IOException {
    writeCanonical(Canonical.create(v));
  }


  @Override
  public void writeNumber(double v) throws IOException {
    writeCanonical(Canonical.create(v));
  }


  @Override
  public void writeNumber(float v) throws IOException {
    writeCanonical(Canonical.create(v));
  }


  @Override
  public void writeNumber(BigDecimal v) throws IOException {
    writeCanonical(Canonical.create(v));
  }


  @Override
  public void writeNumber(String encodedValue) throws IOException {
    // In keeping with this method's contract, we actually output a String
    writeCanonical(Canonical.create(encodedValue));
  }


  @Override
  public void writeObject(Object value) throws IOException {
    if (value == null) {
      writeNull();
      return;
    }

    if (value instanceof Canonical) {
      writeCanonical((Canonical) value);
      return;
    }
    if (value instanceof JsonValue) {
      writeCanonical(Canonical.cast((JsonValue) value));
      return;
    }

    if (objectCodec != null) {
      objectCodec.writeValue(this, value);
      return;
    }
    _writeSimpleObject(value);
  }


  @Override
  public void writeRaw(String text) {
    rawNotSupported();
  }


  @Override
  public void writeRaw(String text, int offset, int len) {
    rawNotSupported();
  }


  @Override
  public void writeRaw(char[] text, int offset, int len) {
    rawNotSupported();
  }


  @Override
  public void writeRaw(char c) {
    rawNotSupported();
  }


  /**
   * Write a Json Value which is being processed as a type. This means the start and end markers are being written by the Jackson type processor.
   *
   * @param object      the value to write
   * @param isContainer is the value a container? i.e. does it have start and end markers?
   */
  public void writeRawCanonicalType(Canonical object, boolean isContainer) throws IOException {
    String json = Canonical.toCanonicalString(object);
    Canonical raw = new CJJson(json);

    if (isContainer) {
      // The caller has already pushed the start marker, creating the container. We pop the new container off the stack and discard it.
      RawContainer rawContainer = new RawContainer(json);
      stack.pop();
      if (!stack.isEmpty()) {
        // have to replace the link to the new container in the parent with the raw JSON
        Container parent = stack.peek();
        parent.set(writeContext.getParent(), raw);
      }
      stack.push(rawContainer);

      return;
    }

    // Not a container, so no markers to handle
    writeCanonical(raw);
  }


  /**
   * Write a Json Value as a value.
   *
   * @param object the value
   */
  public void writeRawCanonicalValue(Canonical object) throws IOException {
    writeCanonical(new CJJson(Canonical.toCanonicalString(object)));
  }


  @Override
  public void writeRawUTF8String(byte[] text, int offset, int length) {
    rawNotSupported();
  }


  @Override
  public void writeRawValue(String text) {
    rawNotSupported();
  }


  @Override
  public void writeRawValue(String text, int offset, int len) {
    rawNotSupported();
  }


  @Override
  public void writeRawValue(char[] text, int offset, int len) {
    rawNotSupported();
  }


  @Override
  public void writeStartArray() throws IOException {
    verifyValueWrite("start an array");

    ArrayContainer arrayContainer = new ArrayContainer();
    if (!stack.isEmpty()) {
      Container container = stack.peek();
      container.add(writeContext.getCurrentName(), arrayContainer.array);
    }

    writeContext = writeContext.createChildArrayContext();
    stack.push(arrayContainer);
  }


  @Override
  public void writeStartObject() throws IOException {
    verifyValueWrite("start an object");

    ObjectContainer objectContainer = new ObjectContainer();
    if (!stack.isEmpty()) {
      Container container = stack.peek();
      container.add(writeContext.getCurrentName(), objectContainer.object);
    }

    writeContext = writeContext.createChildObjectContext();
    stack.push(objectContainer);
  }


  @Override
  public void writeString(String text) throws IOException {
    writeCanonical(Canonical.create(text));
  }


  @Override
  public void writeString(char[] text, int offset, int len) throws IOException {
    writeCanonical(Canonical.create(new String(text, offset, len)));
  }


  @Override
  public void writeString(SerializableString text) throws IOException {
    writeCanonical(Canonical.create(text.getValue()));
  }


  @Override
  public void writeTree(TreeNode rootNode) throws IOException {
    if (rootNode == null) {
      writeNull();
    } else {
      if (objectCodec == null) {
        throw new IllegalStateException("No ObjectCodec defined");
      }
      objectCodec.writeValue(this, rootNode);
    }
  }


  @Override
  public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
    writeCanonical(Canonical.create(new String(text, offset, length, UTF_8)));
  }

}
