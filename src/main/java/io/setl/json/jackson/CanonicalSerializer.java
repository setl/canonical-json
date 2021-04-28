package io.setl.json.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import io.setl.json.Canonical;

/**
 * A serializer of JSON values.
 *
 * @author Simon Greatrix on 2020-01-07.
 */
public class CanonicalSerializer<T extends Canonical> extends JsonSerializer<T> {

  @Override
  public void serialize(Canonical value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
    if (gen instanceof CanonicalGenerator) {
      ((CanonicalGenerator) gen).writeRawCanonicalValue(value);
      return;
    }

    JacksonGenerator jacksonGenerator = new JacksonGenerator(gen);
    jacksonGenerator.generate(value);
  }


  @Override
  public void serializeWithType(Canonical object, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
    gen.setCurrentValue(object);
    JsonToken token;
    switch (object.getValueType()) {
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
      String text = Canonical.toCanonicalString(object);
      if (token != JsonToken.VALUE_EMBEDDED_OBJECT) {
        // Remove the start and end markers
        text = text.substring(1, text.length() - 1);
      }
      gen.writeRawValue(text);
    }
    typeSer.writeTypeSuffix(gen, typeIdDef);
  }

}
