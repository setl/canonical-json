package io.setl.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import io.setl.json.JsonValue;
import java.io.IOException;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class JsonValueSerializer<T extends JsonValue> extends JsonSerializer<T> {

  @Override
  public void serialize(JsonValue value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
    if (gen instanceof CanonicalGenerator) {
      ((CanonicalGenerator) gen).writeRawCanonicalValue(value);
      return;
    }

    gen.writeRawValue(value.toString());
  }


  @Override
  public void serializeWithType(JsonValue object, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
    gen.setCurrentValue(object);
    JsonToken token;
    switch (object.getType()) {
      case ARRAY:
        token = JsonToken.START_ARRAY;
        break;
      case OBJECT:
        token = JsonToken.START_OBJECT;
        break;
      default:
        // We use this when the type is neither an array nor an object.
        token = JsonToken.VALUE_EMBEDDED_OBJECT;
        break;
    }
    WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(object, token));
    if (gen instanceof CanonicalGenerator) {
      ((CanonicalGenerator) gen).writeRawCanonicalType(object, token != JsonToken.VALUE_EMBEDDED_OBJECT);
    } else {
      String text = object.toString();
      if (token != JsonToken.VALUE_EMBEDDED_OBJECT) {
        // Remove the start and end markers
        text = text.substring(1, text.length() - 1);
      }
      gen.writeRawValue(text);
    }
    typeSer.writeTypeSuffix(gen, typeIdDef);
  }
}
