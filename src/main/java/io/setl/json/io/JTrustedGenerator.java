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
    public void startChild() {
      if (writtenFirst) {
        writeChar(',');
      } else {
        writtenFirst = true;
      }
    }


    @Override
    public Context writeEnd() {
      writeChar(']');
      return parent;
    }


    @Override
    public void writeKey(String key) {
      throw new JsonGenerationException("Cannot write key in array context");
    }


    @Override
    public void writeStart() {
      writeChar('[');
    }


    @Override
    public void writeValue(Primitive primitive) {
      if (writtenFirst) {
        writeChar(',');
      } else {
        writtenFirst = true;
      }
      writePrimitive(primitive);
    }
  }



  class ObjectContext implements Context {

    private final Context parent;

    boolean writtenKey = false;

    private String lastKey = null;

    private boolean writtenFirst = false;


    ObjectContext(Context parent) {
      this.parent = parent;
      if (parent instanceof ObjectContext) {
        ((ObjectContext) parent).writtenKey = false;
      }
    }


    @Override
    public void startChild() {
      writtenKey = false;
      writtenFirst = true;
    }


    @Override
    public Context writeEnd() {
      writeChar('}');
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
      if (writtenFirst) {
        writeChar(',');
      } else {
        writtenFirst = true;
      }
      PString pString = new PString(key);
      writePrimitive(pString);
      writeChar(':');
    }


    @Override
    public void writeStart() {
      writeChar('{');
    }


    @Override
    public void writeValue(Primitive primitive) {
      if (!writtenKey) {
        throw new JsonGenerationException("Cannot write value in object context without key");
      }
      writePrimitive(primitive);
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


  void writeChar(char ch) {
    try {
      writer.write(ch);
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  void writePrimitive(Primitive primitive) {
    try {
      primitive.writeTo(writer);
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }
}
