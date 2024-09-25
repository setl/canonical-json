package io.setl.json.jackson;

import java.io.IOException;
import java.util.Map.Entry;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

import com.fasterxml.jackson.core.JsonGenerator;

import io.setl.json.exception.JsonIOException;
import io.setl.json.exception.NonFiniteNumberException;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * Write a Json Value out to a Jackson Generator.
 *
 * @author Simon Greatrix on 30/01/2020.
 */
public class JacksonGenerator {

  private final JsonGenerator jsonGenerator;


  /**
   * New instance.
   *
   * @param jsonGenerator the Jackson generator to use
   */
  public JacksonGenerator(JsonGenerator jsonGenerator) {
    this.jsonGenerator = jsonGenerator;
  }


  /**
   * Close this and the underlying generator.
   */
  public void close() {
    try {
      jsonGenerator.close();
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  /**
   * Pass the JSON value to the Jackson Generator.
   *
   * @param jsonValue the value
   *
   * @throws IOException if the write fails
   */
  public void generate(JsonValue jsonValue) throws IOException {
    if (jsonValue == null) {
      jsonGenerator.writeNull();
      return;
    }
    switch (jsonValue.getValueType()) {
      case OBJECT:
        generateObject((JsonObject) jsonValue);
        return;
      case ARRAY:
        generateArray((JsonArray) jsonValue);
        return;
      case NULL:
        jsonGenerator.writeNull();
        return;
      case NUMBER:
        generateNumber((JsonNumber) jsonValue);
        return;
      case STRING:
        jsonGenerator.writeString(((JsonString) jsonValue).getString());
        return;
      case TRUE:
        jsonGenerator.writeBoolean(true);
        return;
      case FALSE:
        jsonGenerator.writeBoolean(false);
        return;
      default:
        // should be unreachable
        throw new IllegalArgumentException("Unknown value type: " + jsonValue.getValueType());
    }
  }


  private void generateArray(JsonArray jsonArray) throws IOException {
    jsonGenerator.writeStartArray();
    for (JsonValue jsonValue : jsonArray) {
      generate(jsonValue);
    }
    jsonGenerator.writeEndArray();
  }


  private void generateNumber(JsonNumber jsonValue) throws IOException {
    CJNumber pNumber;
    try {
      pNumber = CJNumber.cast(jsonValue);
    } catch (NonFiniteNumberException e) {
      jsonGenerator.writeString(e.getRepresentation().getString());
      return;
    }
    switch (pNumber.getNumberType()) {
      case CJNumber.TYPE_INT:
        jsonGenerator.writeNumber(pNumber.intValue());
        return;
      case CJNumber.TYPE_LONG:
        jsonGenerator.writeNumber(pNumber.longValue());
        return;
      case CJNumber.TYPE_BIG_INT:
        jsonGenerator.writeNumber(pNumber.bigIntegerValue());
        return;
      case CJNumber.TYPE_DECIMAL:
        jsonGenerator.writeNumber(pNumber.bigDecimalValue());
        return;
      default:
        // should be unreachable
        throw new IllegalArgumentException("Unknown number type: " + pNumber.getNumberType());
    }
  }


  private void generateObject(JsonObject jsonObject) throws IOException {
    jsonGenerator.writeStartObject();
    for (Entry<String, JsonValue> entry : jsonObject.entrySet()) {
      jsonGenerator.writeFieldName(entry.getKey());
      generate(entry.getValue());
    }
    jsonGenerator.writeEndObject();
  }

}
