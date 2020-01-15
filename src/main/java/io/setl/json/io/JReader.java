package io.setl.json.io;

import io.setl.json.parser.JParser;
import java.io.IOException;
import java.io.Reader;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JReader implements JsonReader {

  private final Reader reader;

  private boolean isUsed = false;


  /**
   * New instance.
   *
   * @param reader the text source
   */
  JReader(Reader reader) {
    this.reader = reader;
  }


  @Override
  public void close() {
    isUsed = true;
    try {
      reader.close();
    } catch (IOException e) {
      throw new JsonException("I/O failure", e);
    }
  }


  @Override
  public JsonStructure read() {
    JsonValue value = readValue();
    if (value instanceof JsonStructure) {
      return (JsonStructure) value;
    }
    throw new JsonParsingException("Datum was a " + value.getValueType() + ", not a structure", Location.UNSET);
  }


  @Override
  public JsonArray readArray() {
    JsonValue value = readValue();
    if (value instanceof JsonArray) {
      return (JsonArray) value;
    }
    throw new JsonParsingException("Datum was a " + value.getValueType() + ", not an array", Location.UNSET);
  }


  @Override
  public JsonObject readObject() {
    JsonValue value = readValue();
    if (value instanceof JsonObject) {
      return (JsonObject) value;
    }
    throw new JsonParsingException("Datum was a " + value.getValueType() + ", not an object", Location.UNSET);
  }


  @Override
  public JsonValue readValue() {
    if (isUsed) {
      throw new IllegalStateException("This JsonReader has already been used");
    }
    isUsed = true;
    JParser jParser = new JParser(reader);
    if (!jParser.hasNext()) {
      throw new JsonParsingException("No data found in document", Location.UNSET);
    }
    jParser.next();
    return jParser.getValue();
  }
}
