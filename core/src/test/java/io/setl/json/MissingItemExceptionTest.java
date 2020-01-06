package io.setl.json;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MissingItemExceptionTest {

  JsonObject object;
  JsonArray array;
  
  @Before
  public void setUp() throws Exception {
    object = new JsonObject();
    object.put("array",new JsonArray());
    object.put("boolean",true);
    object.put("null");
    object.put("string","text");
    object.put("number",123);
    object.put("object",new JsonObject());
    
    array = new JsonArray();
    array.addAll(object.values());
  }


  @Test
  public void testObject() {
    MissingItemException e = null;
    try {
      object.getStringSafe("missing");
      fail();
    } catch ( MissingItemException e2 ) {
      e = e2;
    }
    
    assertEquals(-1,e.getIndex());
    assertEquals("missing", e.getKey());
    assertEquals(Type.STRING, e.getExpected());
  }

  @Test
  public void testArray() {
    MissingItemException e = null;
    try {
      array.getStringSafe(-5);
      fail();
    } catch ( MissingItemException e2 ) {
      e = e2;
    }
    
    assertEquals(-5,e.getIndex());
    assertNull(e.getKey());
    assertEquals(Type.STRING, e.getExpected());
  }

}
