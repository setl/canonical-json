package io.setl.json.io;

import io.setl.json.Parser;
import io.setl.json.exception.InvalidJsonException;
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

  }


  @Override
  public JsonStructure read() {
    return null;
  }


  @Override
  public JsonArray readArray() {
    return null;
  }


  @Override
  public JsonObject readObject() {
    return null;
  }


  @Override
  public JsonValue readValue() {
    try {
      return Parser.parse(reader);
    } catch (InvalidJsonException e) {
      throw new JsonParsingException(e.getMessage(), e.getLocation());
    } catch (IOException e) {
      throw new JsonException("I/O failure", e);
    }
  }
}
