package io.setl.json.builder;

import jakarta.json.JsonStructure;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.Canonical;
import io.setl.json.io.InMemoryGenerator;

/**
 * A union of object and array builder for building deep structures.
 *
 * @author Simon Greatrix on 22/04/2022.
 */
public class StructureGenerator<X extends Canonical & JsonStructure> extends InMemoryGenerator<StructureGenerator<X>> {

  /**
   * Start generating an array.
   *
   * @return the generator
   */
  @SuppressWarnings("java:S2095") // no need to close the generator that is returned
  public static StructureGenerator<CJArray> newArray() {
    StructureGenerator<CJArray> g = new StructureGenerator<>();
    return g.writeStartArray();
  }


  /**
   * Start generating an object.
   *
   * @return the generator
   */
  @SuppressWarnings("java:S2095") // no need to close the generator that is returned
  public static StructureGenerator<CJObject> newObject() {
    StructureGenerator<CJObject> g = new StructureGenerator<>();
    return g.writeStartObject();
  }


  private X result;


  private StructureGenerator() {
  }


  /**
   * Build the structure and return it.
   *
   * @return the constructed JSON
   */
  public X build() {
    if (result == null) {
      // if we are in the top structure rather than the root, end the structure.
      if (context.getParent() != null && context.getParent().getParent() == null) {
        end();
      }
      close();
    }
    return result;
  }


  @Override
  protected void closeWith(Canonical canonical) {
    @SuppressWarnings("unchecked")
    X x = (X) canonical;
    result = x;
  }


  @Override
  public void flush() {
    // do nothing
  }

}
