package io.setl.json.io;

import io.setl.json.JObject;
import io.setl.json.Primitive;
import io.setl.json.exception.JsonIOException;
import io.setl.json.primitive.PString;
import java.io.IOException;
import java.io.Writer;
import javax.json.stream.JsonGenerationException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
class JTrustedGenerator extends JGenerator {

  class ArrayContext implements Context {

    private final Context parent;

    private boolean writtenFirst = false;


    ArrayContext(Context parent) {
      this.parent = parent;
    }


    @Override
    public Context writeEnd() {
      try {
        writer.write(']');
      } catch (IOException e) {
        throw new JsonIOException(e);
      }
      return parent;
    }


    @Override
    public void writeKey(String key) {
      throw new JsonGenerationException("Cannot write key in array context");
    }


    @Override
    public void writeStart() {
      try {
        writer.write('[');
      } catch (IOException e) {
        throw new JsonIOException(e);
      }
    }


    @Override
    public void writeValue(Primitive primitive) {
      try {
        if (writtenFirst) {
          writer.write(',');
        } else {
          writtenFirst = true;
        }
        primitive.writeTo(writer);
      } catch (IOException e) {
        throw new JsonIOException(e);
      }
    }
  }



  class ObjectContext implements Context {

    private final Context parent;

    private String lastKey = null;

    private boolean writtenFirst = false;

    private boolean writtenKey = false;


    ObjectContext(Context parent) {
      this.parent = parent;
    }


    @Override
    public Context writeEnd() {
      try {
        writer.write('}');
      } catch (IOException e) {
        throw new JsonIOException(e);
      }
      return parent;
    }


    @Override
    public void writeKey(String key) {
      if (writtenKey) {
        throw new JsonGenerationException("Cannot write key twice in object context");
      }
      if (lastKey != null && JObject.CODE_POINT_ORDER.compare(lastKey, key) > -1) {
        throw new JsonGenerationException("Key " + Primitive.create(key) + " must not come after " + Primitive.create(lastKey));
      }
      lastKey = key;
      writtenKey = true;
      try {
        if (writtenFirst) {
          writer.write(',');
        } else {
          writtenFirst = true;
        }
        PString pString = new PString(key);
        pString.writeTo(writer);
      } catch (IOException e) {
        throw new JsonIOException(e);
      }
    }


    @Override
    public void writeStart() {
      try {
        writer.write('{');
      } catch (IOException e) {
        throw new JsonIOException(e);
      }
    }


    @Override
    public void writeValue(Primitive primitive) {
      try {
        writer.write(':');
        primitive.writeTo(writer);
      } catch (IOException e) {
        throw new JsonIOException(e);
      }
      writtenKey = false;
    }
  }


  JTrustedGenerator(Writer writer) {
    super(writer);
  }


  @Override
  public void flush() {
    try {
      writer.flush();
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  @Override
  protected Context newArrayContext() {
    return new ArrayContext(context);
  }


  @Override
  protected Context newObjectContext() {
    return new ObjectContext(context);
  }
}
