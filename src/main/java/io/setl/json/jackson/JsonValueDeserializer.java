package io.setl.json.jackson;

import javax.json.JsonValue;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonValueDeserializer extends JsonDeserializer<JsonValue> {

  @Override
  public JsonValue deserialize(JsonParser p, DeserializationContext ctxt) {
    JacksonParser jacksonParser = new JacksonParser(p);
    return jacksonParser.readValue();
  }

}
