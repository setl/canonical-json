package io.setl.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.StringKeySerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.setl.json.JsonObject;
import java.io.IOException;

/**
 * @author Simon Greatrix on 06/01/2020.
 */
public class JsonObjectSerializer extends JsonSerializer<JsonObject> {

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
      v2.valueFormat(PrimitiveSerializer.INSTANCE, PrimitiveSerializer.TYPE);
    }
  }


  @Override
  public Class<JsonObject> handledType() {
    return JsonObject.class;
  }


  @Override
  public void serialize(JsonObject jsonObject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

  }


  @Override
  public void serializeWithType(
      JsonObject value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer
  ) throws IOException {
    gen.setCurrentValue(value);
    WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT));
    if (!value.isEmpty()) {

      if( gen instanceof CanonicalGenerator ) {

      } else {
        gen.writeRawValue();
      }
      if (_sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
        value = _orderEntries(value, gen, provider);
      }
      PropertyFilter pf;
      if ((_filterId != null) && (pf = findPropertyFilter(provider, _filterId, value)) != null) {
        serializeFilteredFields(value, gen, provider, pf, _suppressableValue);
      } else if ((_suppressableValue != null) || _suppressNulls) {
        serializeOptionalFields(value, gen, provider, _suppressableValue);
      } else if (_valueSerializer != null) {
        serializeFieldsUsing(value, gen, provider, _valueSerializer);
      } else {
        serializeFields(value, gen, provider);
      }
    }
    typeSer.writeTypeSuffix(gen, typeIdDef);
  }
}
