package io.setl.json.io;

import io.setl.json.Primitive;
import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.exception.JsonIOException;
import java.io.IOException;
import java.io.Writer;
import javax.json.stream.JsonGenerationException;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
class JSafeGenerator extends JGenerator {

  protected class ArrayContext implements Context {

    private final JArrayBuilder builder = new JArrayBuilder();

    private final Context parent;


    ArrayContext(Context parent) {
      this.parent = parent;
    }


    @Override
    public Context writeEnd() {
      parent.writeValue(Primitive.create(builder.build()));
      return parent;
    }


    @Override
    public void writeKey(String key) {
      throw new JsonGenerationException("Cannot write key in array context");
    }


    @Override
    public void writeStart() {
      // do nothing
    }


    @Override
    public void writeValue(Primitive primitive) {
      builder.add(primitive);
    }
  }



  protected class ObjectContext implements Context {

    private final JObjectBuilder builder = new JObjectBuilder();

    private final Context parent;

    private String key;


    ObjectContext(Context parent) {
      this.parent = parent;
    }


    @Override
    public Context writeEnd() {
      parent.writeValue(Primitive.create(builder.build()));
      return parent;
    }


    @Override
    public void writeKey(String key) {
      if (this.key == null) {
        this.key = key;
        return;
      }
      throw new JsonGenerationException("Cannot write key twice in object context");
    }


    @Override
    public void writeStart() {
      // do nothing
    }


    @Override
    public void writeValue(Primitive primitive) {
      if (key == null) {
        throw new JsonGenerationException("Cannot write value in object context without key");
      }
      builder.add(key, primitive);
      key = null;
    }
  }


  JSafeGenerator(Writer writer) {
    super(writer);
  }


  @Override
  public void flush() {
    if (context instanceof RootContext) {
      try {
        writer.flush();
      } catch (IOException e) {
        throw new JsonIOException(e);
      }
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
