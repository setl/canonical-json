package io.setl.json.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.Test;

import io.setl.json.primitive.CJBase;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class CJBaseSerializerTest {

  @Test
  public void acceptVisitor() throws JsonMappingException {
    CJBaseSerializer instance = new CJBaseSerializer();
    JsonFormatVisitorWrapper mock = mock(JsonFormatVisitorWrapper.class);
    instance.acceptJsonFormatVisitor(mock, TypeFactory.defaultInstance().constructType(CJBase.class));
    verify(mock).expectAnyFormat(any());
  }


  @Test
  public void getType() {
    CJBaseSerializer instance = new CJBaseSerializer();
    assertEquals(CJBase.class, instance.handledType());
  }

}
