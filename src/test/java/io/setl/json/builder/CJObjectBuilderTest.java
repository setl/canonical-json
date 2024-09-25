package io.setl.json.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class CJObjectBuilderTest {

  private ObjectBuilder builder = new ObjectBuilder();


  @Test
  public void add() {
    builder.add("A", JsonValue.EMPTY_JSON_ARRAY);
    test("{\"A\":[]}");
  }


  @Test
  public void addAll() {
    ObjectBuilder b = new ObjectBuilder();
    b.add("@", 0).add("Z", 3);
    builder.addAll(b);
    test("{\"@\":0,\"Z\":3}");
  }


  @Test
  public void addNull() {
    builder.addNull("A");
    test("{\"A\":null}");
  }


  @Test
  public void remove() {
    builder.add("A", true).remove("A");
    test("{}");
  }


  private void test(String txt) {
    assertEquals(txt, builder.build().toString());
  }


  @Test
  public void testAdd() {
    builder.add("A", "wibble");
    test("{\"A\":\"wibble\"}");
  }


  @Test
  public void testAdd1() {
    builder.add("A", BigInteger.ONE);
    test("{\"A\":1}");
  }


  @Test
  public void testAdd2() {
    builder.add("A", BigDecimal.ONE.scaleByPowerOfTen(-2));
    test("{\"A\":1.0E-2}");
  }


  @Test
  public void testAdd3() {
    builder.add("A", 123);
    test("{\"A\":123}");
  }


  @Test
  public void testAdd4() {
    builder.add("A", 123L);
    test("{\"A\":123}");
  }


  @Test
  public void testAdd5() {
    builder.add("A", 16.384);
    test("{\"A\":1.6384E1}");
  }


  @Test
  public void testAdd6() {
    builder.add("A", true);
    test("{\"A\":true}");
  }


  @Test
  public void testAdd7() {
    builder.add("A", new ArrayBuilder());
    test("{\"A\":[]}");
  }


  @Test
  public void testAdd8() {
    builder.add("A", new ObjectBuilder());
    test("{\"A\":{}}");
  }


  @Test
  public void testToString() {
    assertNotNull(builder.toString());
  }

}
