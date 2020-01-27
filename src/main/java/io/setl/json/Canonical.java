package io.setl.json;

import io.setl.json.primitive.numbers.PNumber;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.numbers.PBigDecimal;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

/**
 * Correctly present text and numeric data in the canonical form.
 *
 * @author Simon Greatrix
 */
public class Canonical {


  /**
   * Convert any JsonValue into a canonical one.
   *
   * @param input JSON to convert
   *
   * @return the JSON as a canonical form
   */
  public static JsonValue convert(JsonValue input) {
    return Primitive.create(input);
  }


  /**
   * Convert any JsonArray into a canonical one.
   *
   * @param input JSON to convert
   *
   * @return the JSON as a canonical form
   */
  public static JsonArray convert(JsonArray input) {
    if (input instanceof JArray) {
      return input;
    }
    return JArray.fixCollection(input.asJsonArray());
  }


  /**
   * Convert any JsonObject into a canonical one.
   *
   * @param input JSON to convert
   *
   * @return the JSON as a canonical form
   */
  public static JsonObject convert(JsonObject input) {
    if (input instanceof JObject) {
      return input;
    }
    return JObject.fixMap(input.asJsonObject());
  }


  /**
   * Convert any JsonStructure into a canonical one.
   *
   * @param input JSON to convert
   *
   * @return the JSON as a canonical form
   */
  public static JsonStructure convert(JsonStructure input) {
    return input.getValueType() == ValueType.OBJECT
        ? convert((JsonObject) input)
        : convert((JsonArray) input);
  }


  /**
   * Convert any JsonValue into a canonical one.
   *
   * @param input JSON to convert
   *
   * @return the JSON as a canonical form
   */
  public static JsonNumber convert(JsonNumber input) {
    if (input instanceof PNumber) {
      return input;
    }
    return PNumber.create(input.bigDecimalValue());
  }


  /**
   * Convert any JsonValue into a canonical one.
   *
   * @param input JSON to convert
   *
   * @return the JSON as a canonical form
   */
  public static JsonString convert(JsonString input) {
    if (input instanceof PString) {
      return input;
    }
    return new PString(input.getString());
  }


  /**
   * Output the JSON using UTF-8 handling isolated surrogates.
   *
   * @param output    the output stream
   * @param primitive the primitive to output
   */
  public static void stream(OutputStream output, Primitive primitive) throws IOException {
    String text = primitive.toString();
    output.write(text.getBytes(StandardCharsets.UTF_8));
  }
}
