package io.setl.json.io;

import java.io.Reader;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;

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
   * @param reader the text source
   */
  CJReader(Reader reader) {
    jParser = new Parser(reader);
  }


  private JsonParsingException badType(String expected, ValueType actual) {
    return new JsonParsingException("Datum was a " + actual + ", not a " + expected, Location.UNSET);
  }


  @Override
  public void close() {
    if (isUsed && jParser.hasNext()) {
      // Currently JParser.hasNext fails if there is more than one root, so this line is unnecessary
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
