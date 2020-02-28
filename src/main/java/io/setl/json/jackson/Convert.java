package io.setl.json.jackson;

import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.primitive.PFalse;
import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.PTrue;
import io.setl.json.primitive.numbers.PNumber;

/**
 * A utility class to convert between Jackson's JsonNode and javax's JsonValue.
 *
 * @author Simon Greatrix on 26/02/2020.
 */
public class Convert {

  private static ArrayNode createArrayNode(JsonNodeCreator nodeCreator, JsonArray jsonArray) {
    ArrayNode arrayNode = nodeCreator.arrayNode(jsonArray.size());
    for (JsonValue value : jsonArray) {
      arrayNode.add(toJackson(nodeCreator, value));
    }
    return arrayNode;
  }


  private static JsonValue createJsonArray(ArrayNode node) {
    int s = node.size();
    JArray array = new JArray(s);
    for (int i = 0; i < s; i++) {
      array.add(toJson(node.get(i)));
    }
    return array;
  }


  private static JsonValue createJsonObject(ObjectNode node) {
    JObject object = new JObject();
    Iterator<Entry<String, JsonNode>> iterator = node.fields();
    while (iterator.hasNext()) {
      Entry<String, JsonNode> entry = iterator.next();
      object.put(entry.getKey(), toJson(entry.getValue()));
    }
    return object;
  }


  private static ValueNode createNumberNode(JsonNodeCreator nodeCreator, JsonNumber value) {
    if (value instanceof PNumber) {
      PNumber number = (PNumber) value;
      switch (number.getNumberType()) {
        case PNumber.TYPE_INT:
          return nodeCreator.numberNode(number.intValue());
        case PNumber.TYPE_LONG:
          return nodeCreator.numberNode(number.longValue());
        case PNumber.TYPE_BIG_INT:
          return nodeCreator.numberNode(number.bigIntegerValue());
        case PNumber.TYPE_DECIMAL:
          return nodeCreator.numberNode(number.bigDecimalValue());
        default:
          break;
      }
    }

    return value.isIntegral() ? nodeCreator.numberNode(value.bigIntegerValue()) : nodeCreator.numberNode(value.bigDecimalValue());
  }


  private static ObjectNode createObjectNode(JsonNodeCreator nodeCreator, JsonObject jsonObject) {
    ObjectNode objectNode = nodeCreator.objectNode();
    for (Entry<String, JsonValue> entry : jsonObject.entrySet()) {
      objectNode.set(entry.getKey(), toJackson(nodeCreator, entry.getValue()));
    }
    return objectNode;
  }


  /**
   * Convert a javax JsonValue to a Jackson JsonNode.
   *
   * @param nodeCreator factory for Jackson nodes
   * @param value       the value to convert
   *
   * @return the Jackson equivalent
   */
  public static JsonNode toJackson(JsonNodeCreator nodeCreator, JsonValue value) {
    if (value == null) {
      return null;
    }
    switch (value.getValueType()) {
      case OBJECT:
        return createObjectNode(nodeCreator, (JsonObject) value);
      case ARRAY:
        return createArrayNode(nodeCreator, (JsonArray) value);
      case STRING:
        return nodeCreator.textNode(((JsonString) value).getString());
      case NUMBER:
        return createNumberNode(nodeCreator, (JsonNumber) value);
      case NULL:
        return nodeCreator.nullNode();
      case TRUE:
        return nodeCreator.booleanNode(true);
      case FALSE:
        return nodeCreator.booleanNode(false);
      default:
        // should be unreachable
        throw new JsonException("Unknown value type: " + value.getValueType());
    }
  }


  /**
   * Convert a javax JsonValue to a Jackson JsonNode.
   *
   * @param value the value to convert
   *
   * @return the Jackson equivalent
   */
  public static JsonNode toJackson(JsonValue value) {
    return toJackson(JsonNodeFactory.withExactBigDecimals(false), value);
  }


  /**
   * Convert a javax JsonArray to a Jackson ArrayNode.
   *
   * @param value the value to convert
   *
   * @return the Jackson equivalent
   */
  public static ArrayNode toJackson(JsonArray value) {
    return (ArrayNode) toJackson(JsonNodeFactory.withExactBigDecimals(false), value);
  }


  /**
   * Convert a javax JsonObject to a Jackson ObjectNode.
   *
   * @param value the value to convert
   *
   * @return the Jackson equivalent
   */
  public static ObjectNode toJackson(JsonObject value) {
    return (ObjectNode) toJackson(JsonNodeFactory.withExactBigDecimals(false), value);
  }


  /**
   * Convert a javax JsonStructure to a Jackson ContainerNode.
   *
   * @param value the value to convert
   *
   * @return the Jackson equivalent
   */
  @SuppressWarnings("unchecked")
  public static <T extends ContainerNode<T>> ContainerNode<T> toJackson(JsonStructure value) {
    return (ContainerNode<T>) toJackson(JsonNodeFactory.withExactBigDecimals(false), value);
  }


  /**
   * Convert a Jackson container to a javax JsonStructure.
   *
   * @param node the node to convert
   *
   * @return the equivalent JsonStructure
   */
  public static JsonStructure toJson(ContainerNode<?> node) {
    return (JsonStructure) toJson((JsonNode) node);
  }


  /**
   * Convert a Jackson ObjectNode to a javax JsonObject.
   *
   * @param node the node to convert
   *
   * @return the equivalent JsonStructure
   */
  public static JsonObject toJson(ObjectNode node) {
    return (JsonObject) toJson((JsonNode) node);
  }


  /**
   * Convert a Jackson ArrayNode to a javax JsonArray.
   *
   * @param node the node to convert
   *
   * @return the equivalent JsonStructure
   */
  public static JsonArray toJson(ArrayNode node) {
    return (JsonArray) toJson((JsonNode) node);
  }


  /**
   * Convert a Jackson JsonNode to a javax JsonValue.
   *
   * @param node the node to convert
   *
   * @return the equivalent JsonValue
   */
  public static JsonValue toJson(JsonNode node) {
    switch (node.getNodeType()) {
      case OBJECT:
        return createJsonObject((ObjectNode) node);
      case ARRAY:
        return createJsonArray((ArrayNode) node);
      case STRING:
        return PString.create(node.textValue());
      case BOOLEAN:
        return node.booleanValue() ? PTrue.TRUE : PFalse.FALSE;
      case NULL:
        return PNull.NULL;
      case NUMBER:
        return PNumber.cast(node.numberValue());
      case BINARY:
        try {
          return PString.create(Base64.getEncoder().encodeToString(node.binaryValue()));
        } catch (IOException ioe) {
          throw new JsonException("Jackson failure", ioe);
        }
      case POJO:
        throw new JsonException("Jackson POJO nodes are not supported");
      case MISSING:
        throw new JsonException("Jackson MISSING nodes are not supported");
      default:
        // should be unreachable
        throw new JsonException("Unknown Jackson node type: " + node.getNodeType());
    }
  }

}
