package io.setl.json.jackson;

import java.io.IOException;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.exception.JsonIOException;
import io.setl.json.io.Location;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import io.setl.json.primitive.numbers.PNumber;

/**
 * @author Simon Greatrix on 31/01/2020.
 */
public class JacksonReader implements JsonReader {

  private final JsonParser jsonParser;

  private boolean isUsed = false;


  public JacksonReader(JsonParser jsonParser) {
    this.jsonParser = jsonParser;
  }


  public JacksonReader(TreeNode treeNode) {
    this.jsonParser = treeNode.traverse();
  }


  private void checkUsed() {
    if (isUsed) {
      throw new IllegalStateException("Parser has already read a value");
    }
    isUsed = true;
  }


  @Override
  public void close() {
    try {
      jsonParser.close();
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  private JsonArray doReadArray() throws IOException {
    // Read the array. The start array token has already been read.
    JArray jArray = new JArray();
    while (true) {
      JsonToken token = jsonParser.nextToken();
      if (token == JsonToken.END_ARRAY) {
        return jArray;
      }
      jArray.add(doReadValue(token));
    }
  }


  private JsonObject doReadObject() throws IOException {
    // Read the object. The start object token has already been read.
    JObject jObject = new JObject();
    while (true) {
      JsonToken token = jsonParser.nextToken();
      if (token == JsonToken.END_OBJECT) {
        return jObject;
      }

      if (token != JsonToken.FIELD_NAME) {
        // hopefully unreachable
        throw new IllegalStateException("Expected a field name inside object, but saw " + token);
      }
      String fieldName = jsonParser.getText();

      jObject.put(fieldName, doReadValue(jsonParser.nextToken()));
    }
  }


  private JsonValue doReadValue(JsonToken token) throws IOException {
    if (token == null) {
      throw new JsonParsingException("Value not found", getLocation());
    }
    switch (token) {
      case START_ARRAY:
        return doReadArray();
      case START_OBJECT:
        return doReadObject();
      case VALUE_FALSE:
        return PFalse.FALSE;
      case VALUE_TRUE:
        return PTrue.TRUE;
      case VALUE_NULL:
        return PNull.NULL;
      case VALUE_STRING:
      case VALUE_EMBEDDED_OBJECT:
        return PString.create(jsonParser.getText());
      case VALUE_NUMBER_INT:
        switch (jsonParser.getNumberType()) {
          case INT:
            return PNumber.create(jsonParser.getIntValue());
          case LONG:
            return PNumber.create(jsonParser.getLongValue());
          case BIG_INTEGER:
            return PNumber.cast(jsonParser.getBigIntegerValue());
          default:
            // hopefully unreachable
            throw new IllegalStateException("Unexpected integer type: " + jsonParser.getNumberType());
        }
      case VALUE_NUMBER_FLOAT:
        return PNumber.cast(jsonParser.getDecimalValue());
      default:
        // Reachable if the encoding uses stuff beyond JSON like embedded objects and reference IDs
        throw new IllegalStateException("Unhandled token from Jackson: " + token);
    }
  }


  private Location getLocation() {
    JsonLocation l = jsonParser.getTokenLocation();
    return new Location(l.getColumnNr(), l.getLineNr(), l.getByteOffset());
  }


  @Override
  public JsonStructure read() {
    checkUsed();
    try {
      JsonToken token = jsonParser.hasCurrentToken() ? jsonParser.currentToken() : jsonParser.nextToken();
      if (token == JsonToken.START_ARRAY) {
        return doReadArray();
      }
      if (token == JsonToken.START_OBJECT) {
        return doReadObject();
      }
      throw new JsonParsingException("Cannot create structure when next item in JSON stream is " + token, getLocation());
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  @Override
  public JsonArray readArray() {
    checkUsed();
    try {
      JsonToken token = jsonParser.hasCurrentToken() ? jsonParser.currentToken() : jsonParser.nextToken();
      if (token == JsonToken.START_ARRAY) {
        return doReadArray();
      }
      throw new JsonParsingException("Cannot create array when next item in JSON stream is " + token, getLocation());
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  @Override
  public JsonObject readObject() {
    checkUsed();
    try {
      JsonToken token = jsonParser.hasCurrentToken() ? jsonParser.currentToken() : jsonParser.nextToken();
      if (token == JsonToken.START_OBJECT) {
        return doReadObject();
      }
      throw new JsonParsingException("Cannot create object when next item in JSON stream is " + token, getLocation());
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }


  @Override
  public JsonValue readValue() {
    checkUsed();
    try {
      JsonToken token = jsonParser.hasCurrentToken() ? jsonParser.currentToken() : jsonParser.nextToken();
      return doReadValue(token);
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }

}
