package io.setl.json.io;

import jakarta.json.JsonValue.ValueType;
import jakarta.json.stream.JsonGenerationException;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.Canonical;
import io.setl.json.primitive.CJBase;
import io.setl.json.primitive.CJNull;

/**
 * A trusted generator passes the JSON on to a formatter immediately. If the keys of an object are out of sequence, an exception will be raised.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
class TrustedGenerator implements Generator<TrustedGenerator> {

  interface Context {

    Context writeEnd();

    void writeKey(String key);

    void writeStructure();

    void writeValue(CJBase canonical);

  }



  class ArrayContext implements Context {

    private final Context parent;

    private boolean writtenFirst = false;


    ArrayContext(Context parent) {
      this.parent = parent;
      formatter.writeArrayStart();
    }


    @Override
    public Context writeEnd() {
      formatter.writeArrayEnd();
      return parent;
    }


    @Override
    public void writeKey(String key) {
      throw new JsonGenerationException("Cannot write key in array context");
    }


    public void writeStructure() {
      if (writtenFirst) {
        formatter.writeComma();
      } else {
        writtenFirst = true;
      }
    }


    @Override
    public void writeValue(CJBase canonical) {
      if (writtenFirst) {
        formatter.writeComma();
      } else {
        writtenFirst = true;
      }
      formatter.write(canonical);
    }

  }



  class ObjectContext implements Context {

    private final Context parent;

    boolean writtenKey = false;

    private String lastKey = null;

    private boolean writtenFirst = false;


    ObjectContext(Context parent) {
      this.parent = parent;
      formatter.writeObjectStart();
    }


    @Override
    public Context writeEnd() {
      formatter.writeObjectEnd();
      return parent;
    }


    @Override
    public void writeKey(String key) {
      if (writtenKey) {
        throw new JsonGenerationException("Cannot write key twice in object context");
      }
      if (lastKey != null && CJObject.CODE_POINT_ORDER.compare(lastKey, key) > -1) {
        throw new JsonGenerationException("Key " + Canonical.create(key) + " must not come after " + Canonical.create(lastKey));
      }
      lastKey = key;
      writtenKey = true;
      if (writtenFirst) {
        formatter.writeComma();
      } else {
        writtenFirst = true;
      }
      formatter.writeKey(key);
      formatter.writeColon();
    }


    @Override
    public void writeStructure() {
      if (!writtenKey) {
        throw new JsonGenerationException("Cannot write value in object context without key");
      }
      writtenKey = false;
    }


    @Override
    public void writeValue(CJBase canonical) {
      if (!writtenKey) {
        throw new JsonGenerationException("Cannot write value in object context without key");
      }
      formatter.write(canonical);
      writtenKey = false;
    }

  }



  class RootContext implements Context {

    boolean hasWritten = false;


    public Context writeEnd() {
      throw new JsonGenerationException("Cannot write end in root context");
    }


    @Override
    public void writeKey(String key) {
      throw new JsonGenerationException("Cannot write key in root context");
    }


    @Override
    public void writeStructure() {
      if (hasWritten) {
        throw new JsonGenerationException("Cannot write multiple values in root context");
      }
      hasWritten = true;
    }


    @Override
    public void writeValue(CJBase value) {
      if (hasWritten) {
        throw new JsonGenerationException("Cannot write multiple values in root context");
      }
      hasWritten = true;
      formatter.write(value);
    }

  }



  private final Formatter formatter;

  private Context context = new RootContext();


  TrustedGenerator(Formatter formatter) {
    this.formatter = formatter;
  }


  @Override
  public void close() {
    if (context instanceof RootContext) {
      formatter.close();
      return;
    }
    throw new JsonGenerationException("Closed attempted within structure");
  }


  @Override
  public void flush() {
    formatter.flush();
  }


  @Override
  public TrustedGenerator write(Canonical value) {
    if (value == null) {
      context.writeValue(CJNull.NULL);
    } else if (value instanceof CJBase) {
      context.writeValue((CJBase) value);
    } else if (value.getValueType() == ValueType.ARRAY) {
      CJArray cjArray = (CJArray) value;
      writeStartArray();
      cjArray.canonicalForEach(this::write);
      writeEnd();
    } else {
      CJObject cjObject = (CJObject) value;
      writeStartObject();
      cjObject.canonicalForEach(this::write);
      writeEnd();
    }
    return this;
  }


  @Override
  public TrustedGenerator writeEnd() {
    context = context.writeEnd();
    return this;
  }


  @Override
  public TrustedGenerator writeKey(String name) {
    context.writeKey(name);
    return this;
  }


  @Override
  public TrustedGenerator writeStartArray() {
    context.writeStructure();
    context = new ArrayContext(context);
    return this;
  }


  @Override
  public TrustedGenerator writeStartObject() {
    context.writeStructure();
    context = new ObjectContext(context);
    return this;
  }

}
