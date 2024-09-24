package io.setl.json.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.setl.json.primitive.CJBase;

/**
 * A writer of JSON values.
 *
 * @author Simon Greatrix on 06/01/2020.
 */
public class CJBaseSerializer extends CanonicalSerializer<CJBase> {

  static final CJBaseSerializer INSTANCE = new CJBaseSerializer();

  static final JavaType TYPE = TypeFactory.defaultInstance().constructType(CJBase.class);


  /** New instance. */
  public CJBaseSerializer() {
    // nothing to do
  }


  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
    // Cannot narrow the format down as a json value could be anything.
    visitor.expectAnyFormat(type);
  }


  @Override
  public Class<CJBase> handledType() {
    return CJBase.class;
  }


}
