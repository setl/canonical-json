package io.setl.json.jackson;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.setl.json.Primitive;
import org.junit.Test;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class PrimitiveSerializerTest {

  @Test
  public void acceptVisitor() throws JsonMappingException {
    PrimitiveSerializer instance = new PrimitiveSerializer();
    instance.acceptJsonFormatVisitor(new Base(), TypeFactory.defaultInstance().constructType(Primitive.class));
  }


  @Test
  public void getType() {
    PrimitiveSerializer instance = new PrimitiveSerializer();
    assertEquals(Primitive.class, instance.handledType());
  }

}