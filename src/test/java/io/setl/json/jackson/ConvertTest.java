package io.setl.json.jackson;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;

/**
 * @author Simon Greatrix on 28/02/2020.
 */
public class ConvertTest {

  @Test
  public void test() {
    ObjectBuilder builder = new ObjectBuilder();
    builder.add("a", new ArrayBuilder()
            .add(1).add(2.0).add(1_000_000_000_000L).add(BigInteger.ONE.shiftLeft(100)).add(BigDecimal.valueOf(Math.PI)).add("Hello"))
        .add("b0", true)
        .add("b1", false)
        .addNull("n")
        .add("o", new ObjectBuilder().add("x", "y").add("a", "b"));
    JsonObject jsonObject = builder.build();

    JsonNode jsonNode = Convert.toJackson(jsonObject);
    JsonValue jsonValue = Convert.toJson(jsonNode);
    assertEquals(jsonObject.toString(), jsonValue.toString());
  }


  @Test
  public void testArray() {
    JsonArray array = new ArrayBuilder()
        .add(1).add(2.0).add(1_000_000_000_000L).add(BigInteger.ONE.shiftLeft(100)).add(BigDecimal.valueOf(Math.PI)).add("Hello").build();

    ArrayNode jsonNode = Convert.toJackson(array);
    JsonArray jsonValue = Convert.toJson(jsonNode);
    assertEquals(array.toString(), jsonValue.toString());
  }


  @Test
  public void testBinary() throws IOException {
    byte[] binary = new byte[]{0, 1, 2, 3, 4};
    BinaryNode node = new BinaryNode(binary);
    JsonValue json = Convert.toJson(node);
    JsonNode out = Convert.toJackson(json);
    assertArrayEquals(binary, out.binaryValue());
  }


  @Test
  public void testObject() {
    ObjectBuilder builder = new ObjectBuilder();
    builder.add("b0", true)
        .add("b1", false)
        .addNull("n");
    JsonObject jsonObject = builder.build();

    ObjectNode jsonNode = Convert.toJackson(jsonObject);
    JsonObject jsonValue = Convert.toJson(jsonNode);
    assertEquals(jsonObject.toString(), jsonValue.toString());
  }

}