package io.setl.json.jackson;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.setl.json.JsonObject;
import org.junit.Test;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class JsonObjectSerializerTest {

  @Test
  public void acceptVisitor() throws JsonMappingException {
    JsonObjectSerializer instance = new JsonObjectSerializer();
    instance.acceptJsonFormatVisitor(new Base(), TypeFactory.defaultInstance().constructType(JsonObject.class));
  }


  @Test
  public void getType() {
    JsonObjectSerializer instance = new JsonObjectSerializer();
    assertEquals(JsonObject.class, instance.handledType());
  }

}