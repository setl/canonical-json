package io.setl.json.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.setl.json.Primitive;

/**
 * @author Simon Greatrix on 06/01/2020.
 */
public class PrimitiveSerializer extends JsonValueSerializer<Primitive> {

  static final PrimitiveSerializer INSTANCE = new PrimitiveSerializer();

  static final JavaType TYPE = TypeFactory.defaultInstance().constructType(Primitive.class);


  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
    // Cannot narrow the format down as a Primitive could be anything.
    visitor.expectAnyFormat(type);
  }


  @Override
  public Class<Primitive> handledType() {
    return Primitive.class;
  }


}
