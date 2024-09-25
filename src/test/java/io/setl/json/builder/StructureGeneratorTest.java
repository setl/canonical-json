package io.setl.json.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.Test;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.Canonical;

/**
 * @author Simon Greatrix on 22/04/2022.
 */
class StructureGeneratorTest {

  @Test
  void end() {
    CJArray array = StructureGenerator.newArray().value(1).value(2).end().build();
    assertEquals("[1,2]", array.toCanonicalString());
  }


  @Test
  void entryNull() {
    CJArray array = StructureGenerator.newArray().startObject().entryNull("a").end().end().build();
    assertEquals("[{\"a\":null}]", array.toCanonicalString());
  }


  @Test
  void entryStringBigDecimal() {
    CJObject object = StructureGenerator.newObject().entry("x", new BigDecimal("3.141592")).end().build();
    assertEquals("{\"x\":3.141592E0}", object.toCanonicalString());
  }


  @Test
  void entryStringBigInteger() {
    CJObject object = StructureGenerator.newObject().entry("x", new BigInteger("141592")).end().build();
    assertEquals("{\"x\":141592}", object.toCanonicalString());
  }


  @Test
  void entryStringBoolean() {
    CJObject object = StructureGenerator.newObject().entry("x", true).end().build();
    assertEquals("{\"x\":true}", object.toCanonicalString());
  }


  @Test
  void entryStringCanonical() {
    CJObject object = StructureGenerator.newObject().entry("x", Canonical.cast(12345)).end().build();
    assertEquals("{\"x\":12345}", object.toCanonicalString());
  }


  @Test
  void entryStringDouble() {
    CJObject object = StructureGenerator.newObject()
        .entry("x", 12e+100d)
        .end().build();
    assertEquals("{\"x\":1.2E101}", object.toCanonicalString());
  }


  @Test
  void entryStringInt() {
    CJObject object = StructureGenerator.newObject().entry("x", 12).end().build();
    assertEquals("{\"x\":12}", object.toCanonicalString());
  }


  @Test
  void entryStringLong() {
    CJObject object = StructureGenerator.newObject().entry("x", 12_000_000_000L).end().build();
    assertEquals("{\"x\":12000000000}", object.toCanonicalString());
  }


  @Test
  void entryStringString() {
    CJObject object = StructureGenerator.newObject().entry("x", "y").end().build();
    assertEquals("{\"x\":\"y\"}", object.toCanonicalString());
  }


  @Test
  void entyStringJson() {
    CJObject object = StructureGenerator.newObject().entry("x", JsonValue.NULL).end().build();
    assertEquals("{\"x\":null}", object.toCanonicalString());
  }


  @Test
  void flush() {
  }


  @Test
  void key() {
    CJObject object = StructureGenerator.newObject().key("x").value(JsonValue.NULL).end().build();
    assertEquals("{\"x\":null}", object.toCanonicalString());
  }


  @Test
  void startArray() {
    CJArray array = StructureGenerator.newArray().startArray().startArray().startArray().end().end().end().build();
    assertEquals("[[[[]]]]", array.toCanonicalString());
  }


  @Test
  void startArrayString() {
    CJObject object = StructureGenerator.newObject().startArray("a").value(1).value(2).end().build();
    assertEquals("{\"a\":[1,2]}", object.toCanonicalString());
  }


  @Test
  void startObject() {
    CJObject object = StructureGenerator.newObject().key("a").startObject().entry("b", 2).end().build();
    assertEquals("{\"a\":{\"b\":2}}", object.toCanonicalString());
  }


  @Test
  void startObjectString() {
    CJObject object = StructureGenerator.newObject().startObject("a").entry("b", 2).end().build();
    assertEquals("{\"a\":{\"b\":2}}", object.toCanonicalString());
  }


  @Test
  void valueBigDecimal() {
    CJObject object = StructureGenerator.newObject().key("a").value(new BigDecimal("123.456789")).build();
    assertEquals("{\"a\":1.23456789E2}", object.toCanonicalString());
  }


  @Test
  void valueBigInteger() {
    CJObject object = StructureGenerator.newObject().key("a").value(new BigInteger("123456789")).build();
    assertEquals("{\"a\":123456789}", object.toCanonicalString());
  }


  @Test
  void valueBoolean() {
    CJObject object = StructureGenerator.newObject().key("a").value(false).build();
    assertEquals("{\"a\":false}", object.toCanonicalString());
  }


  @Test
  void valueCanonical() {
    CJObject object = StructureGenerator.newObject().key("a").value((Canonical) null).build();
    assertEquals("{\"a\":null}", object.toCanonicalString());
  }


  @Test
  void valueDouble() {
    CJObject object = StructureGenerator.newObject().key("a").value(1.2e-6).build();
    assertEquals("{\"a\":1.2E-6}", object.toCanonicalString());
  }


  @Test
  void valueInt() {
    CJObject object = StructureGenerator.newObject().key("a").value(1234).build();
    assertEquals("{\"a\":1234}", object.toCanonicalString());
  }


  @Test
  void valueLong() {
    CJObject object = StructureGenerator.newObject().key("a").value(1234567891234L).build();
    assertEquals("{\"a\":1234567891234}", object.toCanonicalString());
  }


  @Test
  void valueNull() {
    CJObject object = StructureGenerator.newObject().key("a").valueNull().build();
    assertEquals("{\"a\":null}", object.toCanonicalString());
  }


  @Test
  void valueString() {
    CJObject object = StructureGenerator.newObject().key("a").value("b").build();
    assertEquals("{\"a\":\"b\"}", object.toCanonicalString());
  }


  @Test
  void valueValue() {
    CJObject object = StructureGenerator.newObject().key("a").value(JsonValue.EMPTY_JSON_ARRAY).build();
    assertEquals("{\"a\":[]}", object.toCanonicalString());
  }

}
