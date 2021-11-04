package io.setl.json.io;

import javax.json.stream.JsonGenerationException;

import io.setl.json.Canonical;
import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.primitive.CJNull;

/**
 * The safe generator builds a copy of the structure and then passes it to a trusted generator.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
class SafeGenerator extends Generator {

  protected interface Context {

    void write(Canonical canonical);

    Context writeEnd();

    void writeKey(String key);

  }



  protected static class ArrayContext implements Context {

    private final ArrayBuilder builder = new ArrayBuilder();

    private final Context parent;


    ArrayContext(Context parent) {
      this.parent = parent;
    }


    @Override
    public void write(Canonical canonical) {
      builder.add(canonical);
    }


    @Override
    public Context writeEnd() {
      parent.write(builder.build());
      return parent;
    }


    @Override
    public void writeKey(String key) {
      throw new JsonGenerationException("Cannot write key in array context");
    }

  }



  protected static class ObjectContext implements Context {

    private final ObjectBuilder builder = new ObjectBuilder();

    private final Context parent;

    private String key;


    ObjectContext(Context parent) {
      this.parent = parent;
    }


    @Override
    public void write(Canonical canonical) {
      if (key == null) {
        throw new JsonGenerationException("Cannot write value in object context without key");
      }
      builder.add(key, canonical);
      key = null;
    }


    @Override
    public Context writeEnd() {
      parent.write(builder.build());
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

  }



  protected static class RootContext implements Context {

    Canonical output = null;


    @Override
    public void write(Canonical canonical) {
      if (output != null) {
        throw new JsonGenerationException("Cannot write multiple values to root context");
      }
      output = canonical;
    }


    public Context writeEnd() {
      throw new JsonGenerationException("Cannot write end in root context");
    }


    @Override
    public void writeKey(String key) {
      throw new JsonGenerationException("Cannot write key in root context");
    }

  }



  private final TrustedGenerator target;

  private Context context;


  SafeGenerator(Formatter formatter) {
    target = new TrustedGenerator(formatter);
    context = new RootContext();
  }


  @Override
  public void close() {
    if (!(context instanceof RootContext)) {
      throw new JsonGenerationException("Close attempted with unfinished structures");
    }
    Canonical canonical = ((RootContext) context).output;
    if (canonical != null) {
      target.write(canonical);
      ((RootContext) context).output = null;
    }
    target.close();
  }


  @Override
  public void flush() {
    // This is pretty much always a no-op, unless you want to flush anything written to the output prior to starting the generator. During generation nothing
    // is written as it is prepared in-memory.
    if (context instanceof RootContext) {
      target.flush();
    }
  }


  @Override
  public Generator write(Canonical value) {
    if (value == null) {
      value = CJNull.NULL;
    }
    context.write(value);
    return this;
  }


  @Override
  public Generator writeEnd() {
    context = context.writeEnd();
    return this;
  }


  @Override
  public Generator writeKey(String name) {
    context.writeKey(name);
    return this;
  }


  @Override
  public Generator writeStartArray() {
    context = new ArrayContext(context);
    return this;
  }


  @Override
  public Generator writeStartObject() {
    context = new ObjectContext(context);
    return this;
  }

}
