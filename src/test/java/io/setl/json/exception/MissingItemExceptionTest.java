package io.setl.json.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.EnumSet;
import javax.json.JsonValue.ValueType;

import org.junit.Before;
import org.junit.Test;

import io.setl.json.JArray;
import io.setl.json.JObject;

public class MissingItemExceptionTest {

  JArray array;

  JObject object;


  @Before
  public void setUp() {
    object = new JObject();
    object.put("array", new JArray());
    object.put("boolean", true);
    object.put("null");
    object.put("string", "text");
    object.put("number", 123);
    object.put("object", new JObject());

    array = new JArray();
    array.addAll(object.values());
  }


  @Test
  public void testArray() {
    MissingItemException e = null;
    try {
      array.getString(-5);
      fail();
    } catch (MissingItemException e2) {
      e = e2;
    }

    assertEquals(-5, e.getIndex());
    assertNull(e.getKey());
    assertEquals(EnumSet.of(ValueType.STRING), e.getExpected());
  }


  @Test
  public void testObject() {
    MissingItemException e = null;
    try {
      object.getString("missing");
      fail();
    } catch (MissingItemException e2) {
      e = e2;
    }

    assertEquals(-1, e.getIndex());
    assertEquals("missing", e.getKey());
    assertEquals(EnumSet.of(ValueType.STRING), e.getExpected());
  }

}
