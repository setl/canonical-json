package io.setl.json.jackson;

import java.io.IOException;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.setl.json.exception.JsonIOException;

/**
 * A deserializer for JSON values.
 *
 * @author Simon Greatrix on 12/02/2020.
 */
public class JsonValueDeserializer extends JsonDeserializer<JsonValue> {

  /** New instance. */
  public JsonValueDeserializer() {
    // nothing to do
  }


  @Override
  public JsonValue deserialize(JsonParser p, DeserializationContext context) throws IOException {
    try {
      JacksonReader parser = new JacksonReader(p);
      return parser.readValue();
    } catch (JsonIOException jsonIOException) {
      throw jsonIOException.cause();
    } catch (JsonParsingException jsonParsingException) {
      javax.json.stream.JsonLocation l = jsonParsingException.getLocation();
      JsonLocation location = new JsonLocation(null, l.getStreamOffset(), (int) l.getLineNumber(), (int) l.getColumnNumber());
      throw new JsonParseException(p, jsonParsingException.getMessage(), location, jsonParsingException);
    }
  }


  @Override
  public JsonValue getEmptyValue(DeserializationContext context) {
    return JsonValue.NULL;
  }


  @Override
  public Class<?> handledType() {
    return JsonValue.class;
  }

}
