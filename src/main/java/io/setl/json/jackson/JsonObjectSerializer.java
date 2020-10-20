package io.setl.json.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.StringKeySerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.setl.json.JObject;

/**
 * @author Simon Greatrix on 06/01/2020.
 */
public class JsonObjectSerializer extends PrimitiveSerializer<JObject> {

  /** Serialize the string key as strings. Weirdly Jackson defines the String Key Serializer as serializing Objects. */
  private static final JsonSerializer<Object> KEY_SERIALIZER = new StringKeySerializer();

  private static final JavaType KEY_TYPE = TypeFactory.defaultInstance().constructType(String.class);


  @Override
  public void acceptJsonFormatVisitor(
      JsonFormatVisitorWrapper visitor, JavaType type
  ) throws JsonMappingException {
    JsonMapFormatVisitor v2 = visitor.expectMapFormat(type);
    if (v2 != null) {
      v2.keyFormat(KEY_SERIALIZER, KEY_TYPE);
      v2.valueFormat(PBaseSerializer.INSTANCE, PBaseSerializer.TYPE);
    }
  }


  @Override
  public Class<JObject> handledType() {
    return JObject.class;
  }

}
