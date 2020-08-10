package io.setl.json.jackson;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.Test;

import io.setl.json.primitive.PBase;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class PBaseSerializerTest {

  @Test
  public void acceptVisitor() throws JsonMappingException {
    PBaseSerializer instance = new PBaseSerializer();
    instance.acceptJsonFormatVisitor(new Base(), TypeFactory.defaultInstance().constructType(PBase.class));
  }


  @Test
  public void getType() {
    PBaseSerializer instance = new PBaseSerializer();
    assertEquals(PBase.class, instance.handledType());
  }

}