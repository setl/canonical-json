package io.setl.json.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

import io.setl.json.CJArray;

/**
 * Serializer for JSON arrays.
 *
 * @author Simon Greatrix on 06/01/2020.
 */
public class JsonArraySerializer extends CanonicalSerializer<CJArray> {

  /** New instance. */
  public JsonArraySerializer() {
    // nothing to do
  }


  @Override
  public void acceptJsonFormatVisitor(
      JsonFormatVisitorWrapper visitor, JavaType type
  ) throws JsonMappingException {
    JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(type);
    if (v2 != null) {
      v2.itemsFormat(CJBaseSerializer.INSTANCE, CJBaseSerializer.TYPE);
    }
  }


  @Override
  public Class<CJArray> handledType() {
    return CJArray.class;
  }

}
