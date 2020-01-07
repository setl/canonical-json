package io.setl.json.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import io.setl.json.JsonArray;
import io.setl.json.JsonObject;
import io.setl.json.Type;
import io.setl.json.exception.MissingItemException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MissingItemExceptionTest {

  JsonArray array;

  JsonObject object;


  @Before
  public void setUp() throws Exception {
    object = new JsonObject();
    object.put("array", new JsonArray());
    object.put("boolean", true);
    object.put("null");
    object.put("string", "text");
    object.put("number", 123);
    object.put("object", new JsonObject());

    array = new JsonArray();
    array.addAll(object.values());
  }


  @Test
  public void testArray() {
    MissingItemException e = null;
    try {
      array.getStringSafe(-5);
      fail();
    } catch (MissingItemException e2) {
      e = e2;
    }

    assertEquals(-5, e.getIndex());
    assertNull(e.getKey());
    Assert.assertEquals(Type.STRING, e.getExpected());
  }


  @Test
  public void testObject() {
    MissingItemException e = null;
    try {
      object.getStringSafe("missing");
      fail();
    } catch (MissingItemException e2) {
      e = e2;
    }

    assertEquals(-1, e.getIndex());
    assertEquals("missing", e.getKey());
    assertEquals(Type.STRING, e.getExpected());
  }

}
