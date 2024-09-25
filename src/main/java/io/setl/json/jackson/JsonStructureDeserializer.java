package io.setl.json.jackson;

import java.io.IOException;
import jakarta.json.JsonStructure;
import jakarta.json.stream.JsonParsingException;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.setl.json.exception.JsonIOException;

/**
 * A deserializer for JSON structures.
 *
 * @author Simon Greatrix on 12/02/2020.
 */
public class JsonStructureDeserializer extends JsonDeserializer<JsonStructure> {

  /** New instance. */
  public JsonStructureDeserializer() {
    // nothing to do
  }


  @Override
  public JsonStructure deserialize(JsonParser p, DeserializationContext context) throws IOException {
    try {
      JacksonReader parser = new JacksonReader(p);
      return parser.read();
    } catch (JsonIOException jsonIOException) {
      throw jsonIOException.cause();
    } catch (JsonParsingException jsonParsingException) {
      jakarta.json.stream.JsonLocation l = jsonParsingException.getLocation();
      JsonLocation location = new JsonLocation(null, l.getStreamOffset(), (int) l.getLineNumber(), (int) l.getColumnNumber());
      throw new JsonParseException(p, jsonParsingException.getMessage(), location, jsonParsingException);
    }
  }


  @Override
  public Class<?> handledType() {
    return JsonStructure.class;
  }

}
