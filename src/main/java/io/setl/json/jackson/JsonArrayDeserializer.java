package io.setl.json.jackson;

import java.io.IOException;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.setl.json.exception.JsonIOException;

/**
 * Deserializer for JSON arrays.
 *
 * @author Simon Greatrix on 12/02/2020.
 */
public class JsonArrayDeserializer extends JsonDeserializer<JsonArray> {

  @Override
  public JsonArray deserialize(JsonParser p, DeserializationContext context) throws IOException {
    try {
      JacksonReader parser = new JacksonReader(p);
      return parser.readArray();
    } catch (JsonIOException jsonIOException) {
      throw jsonIOException.cause();
    } catch (JsonParsingException jsonParsingException) {
      javax.json.stream.JsonLocation l = jsonParsingException.getLocation();
      JsonLocation location = new JsonLocation(null, l.getStreamOffset(), (int) l.getLineNumber(), (int) l.getColumnNumber());
      throw new JsonParseException(p, jsonParsingException.getMessage(), location, jsonParsingException);
    }
  }


  @Override
  public JsonArray getEmptyValue(DeserializationContext context) {
    return JsonValue.EMPTY_JSON_ARRAY;
  }


  @Override
  public Class<?> handledType() {
    return JsonArray.class;
  }

}
