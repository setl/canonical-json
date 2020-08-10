package io.setl.json.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.setl.json.primitive.PBase;

/**
 * @author Simon Greatrix on 06/01/2020.
 */
public class PBaseSerializer extends PrimitiveSerializer<PBase> {

  static final PBaseSerializer INSTANCE = new PBaseSerializer();

  static final JavaType TYPE = TypeFactory.defaultInstance().constructType(PBase.class);


  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
    // Cannot narrow the format down as a Primitive could be anything.
    visitor.expectAnyFormat(type);
  }


  @Override
  public Class<PBase> handledType() {
    return PBase.class;
  }


}
