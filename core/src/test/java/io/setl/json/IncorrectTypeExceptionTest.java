package io.setl.json;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IncorrectTypeExceptionTest {

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
    IncorrectTypeException e = null;
    try {
      object.getStringSafe("array");
      fail();
    } catch ( IncorrectTypeException e2 ) {
      e = e2;
    }
    
    assertEquals(-1,e.getIndex());
    assertEquals("array", e.getKey());
    assertEquals(Type.ARRAY, e.getActual());
    assertEquals(Type.STRING, e.getRequired());
  }

  @Test
  public void testArray() {
    IncorrectTypeException e = null;
    try {
      array.getStringSafe(0);
      fail();
    } catch ( IncorrectTypeException e2 ) {
      e = e2;
    }
    
    assertEquals(0,e.getIndex());
    assertNull(e.getKey());
    assertEquals(Type.ARRAY, e.getActual());
    assertEquals(Type.STRING, e.getRequired());
  }

}
