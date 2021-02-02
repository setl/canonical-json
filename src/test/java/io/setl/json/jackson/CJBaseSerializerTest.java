package io.setl.json.jackson;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.Test;

import io.setl.json.primitive.CJBase;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class CJBaseSerializerTest {

  @Test
  public void acceptVisitor() throws JsonMappingException {
    CJBaseSerializer instance = new CJBaseSerializer();
    instance.acceptJsonFormatVisitor(new Base(), TypeFactory.defaultInstance().constructType(CJBase.class));
  }


  @Test
  public void getType() {
    CJBaseSerializer instance = new CJBaseSerializer();
    assertEquals(CJBase.class, instance.handledType());
  }

}
