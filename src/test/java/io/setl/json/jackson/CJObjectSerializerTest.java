package io.setl.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.Test;

import io.setl.json.CJObject;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class CJObjectSerializerTest {

  @Test
  public void acceptVisitor() throws JsonMappingException {
    JsonObjectSerializer instance = new JsonObjectSerializer();
    JsonFormatVisitorWrapper mock = mock(JsonFormatVisitorWrapper.class);
    instance.acceptJsonFormatVisitor(mock, TypeFactory.defaultInstance().constructType(CJObject.class));
    verify(mock).expectMapFormat(any());
  }


  @Test
  public void getType() {
    JsonObjectSerializer instance = new JsonObjectSerializer();
    assertEquals(CJObject.class, instance.handledType());
  }

}
