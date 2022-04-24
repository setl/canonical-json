package io.setl.json.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.EnumSet;
import javax.json.JsonValue.ValueType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.setl.json.CJArray;
import io.setl.json.CJObject;

public class IncorrectTypeExceptionTest {

  CJArray array;

  CJObject object;


  @BeforeEach
  public void setUp() {
    object = new CJObject();
    object.put("array", new CJArray());
    object.put("boolean", true);
    object.put("null");
    object.put("string", "text");
    object.put("number", 123);
    object.put("object", new CJObject());

    array = new CJArray();
    array.addAll(object.values());
  }


  @Test
  public void testArray() {
    IncorrectTypeException e = null;
    try {
      array.getString(0);
      fail();
    } catch (IncorrectTypeException e2) {
      e = e2;
    }

    assertEquals(0, e.getIndex());
    assertNull(e.getKey());
    assertEquals(ValueType.ARRAY, e.getActual());
    assertEquals(EnumSet.of(ValueType.STRING), e.getRequired());
  }


  @Test
  public void testObject() {
    IncorrectTypeException e = null;
    try {
      object.getString("array");
      fail();
    } catch (IncorrectTypeException e2) {
      e = e2;
    }

    assertEquals(-1, e.getIndex());
    assertEquals("array", e.getKey());
    assertEquals(ValueType.ARRAY, e.getActual());
    assertEquals(EnumSet.of(ValueType.STRING), e.getRequired());
  }

}
