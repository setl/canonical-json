package io.setl.json.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class CJArrayBuilderTest {

  ArrayBuilder builder = new ArrayBuilder();


  @Test
  public void add() {
    builder.add(JsonValue.EMPTY_JSON_ARRAY);
    test(ValueType.ARRAY, "[]");
  }


  @Test
  public void addAll() {
    JsonArrayBuilder b = new ArrayBuilder();
    b.add("B").add(2);
    builder.addAll(b);
    test(ValueType.STRING, "\"B\"");
  }


  @Test
  public void addNull() {
    builder.addNull();
    test(ValueType.NULL, "null");
  }


  @Test
  public void remove() {
    builder.add(1).add(2).remove(0);
    test(ValueType.NUMBER, "2");
  }


  @Test
  public void set() {
    builder.addNull().set(0, JsonValue.NULL);
    test(ValueType.NULL, "null");
  }


  @Test
  public void setNull() {
    builder.add(123).setNull(0);
    test(ValueType.NULL, "null");
  }


  private void test(ValueType type, String txt) {
    JsonArray array = builder.build();
    assertFalse(array.isEmpty());
    assertEquals(type, array.get(0).getValueType());
    assertEquals(txt, array.get(0).toString());
  }


  @Test
  public void testAdd() {
    builder.add("wibble");
    test(ValueType.STRING, "\"wibble\"");
  }


  @Test
  public void testAdd1() {
    builder.add(BigDecimal.TEN);
    test(ValueType.NUMBER, "10");
  }


  @Test
  public void testAdd10() {
    builder.add(123).add(0, "wibble");
    test(ValueType.STRING, "\"wibble\"");
  }


  @Test
  public void testAdd11() {
    builder.add(123).add(0, BigDecimal.TEN);
    test(ValueType.NUMBER, "10");
  }


  @Test
  public void testAdd12() {
    builder.add(123).add(0, BigInteger.TEN);
    test(ValueType.NUMBER, "10");
  }


  @Test
  public void testAdd13() {
    builder.add(123).add(0, 456);
    test(ValueType.NUMBER, "456");
  }


  @Test
  public void testAdd14() {
    builder.add(123).add(0, 4_000_000_000L);
    test(ValueType.NUMBER, "4000000000");
  }


  @Test
  public void testAdd15() {
    builder.add(123).add(0, 20.48);
    test(ValueType.NUMBER, "2.048E1");
  }


  @Test
  public void testAdd16() {
    builder.add(123).add(0, false);
    test(ValueType.FALSE, "false");
  }


  @Test
  public void testAdd17() {
    JsonObjectBuilder b = new ObjectBuilder();
    b.add("B", 2);
    builder.add(123).add(0, b);
    test(ValueType.OBJECT, "{\"B\":2}");
  }


  @Test
  public void testAdd18() {
    JsonArrayBuilder b = new ArrayBuilder();
    b.add("B").add(2);
    builder.add(123).add(0, b);
    test(ValueType.ARRAY, "[\"B\",2]");

  }


  @Test
  public void testAdd2() {
    builder.add(BigInteger.TEN);
    test(ValueType.NUMBER, "10");
  }


  @Test
  public void testAdd3() {
    builder.add(123);
    test(ValueType.NUMBER, "123");
  }


  @Test
  public void testAdd4() {
    builder.add(1234567890123456789L);
    test(ValueType.NUMBER, "1234567890123456789");
  }


  @Test
  public void testAdd5() {
    builder.add(1.024);
    test(ValueType.NUMBER, "1.024E0");
  }


  @Test
  public void testAdd6() {
    builder.add(true);
    test(ValueType.TRUE, "true");
  }


  @Test
  public void testAdd7() {
    JsonArrayBuilder b2 = new ArrayBuilder();
    b2.add(1);
    builder.add(b2);
    test(ValueType.ARRAY, "[1]");
  }


  @Test
  public void testAdd8() {
    JsonObjectBuilder b2 = new ObjectBuilder();
    b2.add("A", 1);
    builder.add(b2);
    test(ValueType.OBJECT, "{\"A\":1}");
  }


  @Test
  public void testAdd9() {
    builder.add(123).add(0, JsonValue.EMPTY_JSON_ARRAY);
    test(ValueType.ARRAY, "[]");
  }


  @Test
  public void testAddNull() {
    builder.add(2).addNull(0);
    test(ValueType.NULL, "null");
  }


  @Test
  public void testSet() {
    builder.addNull().set(0, "wibble");
    test(ValueType.STRING, "\"wibble\"");
  }


  @Test
  public void testSet1() {
    builder.addNull().set(0, BigDecimal.TEN);
    test(ValueType.NUMBER, "10");
  }


  @Test
  public void testSet2() {
    builder.addNull().set(0, BigInteger.TEN);
    test(ValueType.NUMBER, "10");
  }


  @Test
  public void testSet3() {
    builder.addNull().set(0, 10);
    test(ValueType.NUMBER, "10");
  }


  @Test
  public void testSet4() {
    builder.addNull().set(0, 10L);
    test(ValueType.NUMBER, "10");
  }


  @Test
  public void testSet5() {
    builder.addNull().set(0, 409.6);
    test(ValueType.NUMBER, "4.096E2");
  }


  @Test
  public void testSet6() {
    builder.addNull().set(0, true);
    test(ValueType.TRUE, "true");
  }


  @Test
  public void testSet7() {
    JsonObjectBuilder b = new ObjectBuilder();
    b.add("A", 9);
    builder.addNull().set(0, b);
    test(ValueType.OBJECT, "{\"A\":9}");
  }


  @Test
  public void testSet8() {
    JsonArrayBuilder b = new ArrayBuilder();
    b.add("A").add(9);
    builder.addNull().set(0, b);
    test(ValueType.ARRAY, "[\"A\",9]");
  }


  @Test
  public void testToString() {
    assertNotNull(builder.toString());
  }

}
