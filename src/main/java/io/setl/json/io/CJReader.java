package io.setl.json.io;

import java.io.Reader;

import jakarta.json.JsonArray;
import jakarta.json.JsonConfig.KeyStrategy;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;

import io.setl.json.parser.Parser;

/**
 * A JSON reader implementation.
 *
 * @author Simon Greatrix on 10/01/2020.
 */
public class CJReader implements JsonReader {

  private final JsonParser jParser;

  private boolean isUsed = false;


  /**
   * New instance.
   *
   * @param reader      the text source
   * @param keyStrategy the key strategy
   */
  CJReader(Reader reader, KeyStrategy keyStrategy) {
    jParser = new Parser(reader, keyStrategy);
  }


  private JsonParsingException badType(String expected, ValueType actual) {
    return new JsonParsingException("Datum was a " + actual + ", not a " + expected, Location.UNSET);
  }


  @Override
  public void close() {
    if (isUsed && jParser.hasNext()) {
      // Currently, JParser.hasNext fails if there is more than one root, so this line is unnecessary
      throw new JsonParsingException("Additional data found after first value", jParser.getLocation());
    }
    isUsed = true;

    // closing the parser also closes the reader
    jParser.close();
  }


  @Override
  public JsonStructure read() {
    JsonValue value = readValue();
    if (value instanceof JsonStructure) {
      return (JsonStructure) value;
    }
    throw badType("STRUCTURE", value.getValueType());
  }


  @Override
  public JsonArray readArray() {
    JsonValue value = readValue();
    if (value instanceof JsonArray) {
      return (JsonArray) value;
    }
    throw badType("ARRAY", value.getValueType());
  }


  @Override
  public JsonObject readObject() {
    JsonValue value = readValue();
    if (value instanceof JsonObject) {
      return (JsonObject) value;
    }
    throw badType("OBJECT", value.getValueType());
  }


  @Override
  public JsonValue readValue() {
    if (isUsed) {
      throw new IllegalStateException("This JsonReader has already been used");
    }
    isUsed = true;
    if (!jParser.hasNext()) {
      throw new JsonParsingException("No data found in document", Location.UNSET);
    }
    jParser.next();
    return jParser.getValue();
  }

}
