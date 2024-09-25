package io.setl.json.builder;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import org.junit.jupiter.api.Test;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.Canonical;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class BuilderFactoryTest {

  BuilderFactory factory = new BuilderFactory();


  @Test
  public void createArrayBuilder() {
    JsonArrayBuilder builder = factory.createArrayBuilder();
    assertNotNull(builder);
    assertTrue(builder.build().isEmpty());
  }


  @Test
  public void createObjectBuilder() {
    JsonObjectBuilder builder = factory.createObjectBuilder();
    assertNotNull(builder);
    assertTrue(builder.build().isEmpty());
  }


  @Test
  public void getConfigInUse() {
    assertTrue(factory.getConfigInUse().isEmpty());
  }


  @Test
  public void testCreateArrayBuilder() {
    CJObject object = new CJObject();
    object.put("A", 1);
    CJArray array = new CJArray();
    array.add(object);
    JsonArrayBuilder builder = factory.createArrayBuilder(array);
    assertNotNull(builder);
    JsonArray output = builder.build();
    assertEquals(1, output.size());
    assertNotSame(object, output.get(0));
    assertEquals(object, output.get(0));
  }


  @Test
  public void testCreateArrayBuilder1() {
    HashMap<String, Object> object = new HashMap<>();
    object.put("A", 1);
    ArrayList<Object> array = new ArrayList<>();
    array.add(object);
    JsonArrayBuilder builder = factory.createArrayBuilder(array);
    assertNotNull(builder);
    JsonArray output = builder.build();
    assertEquals(1, output.size());
    assertNotSame(object, output.get(0));
  }


  @Test
  public void testCreateObjectBuilder() {
    JsonObject jsonObject = new CJObject();
    jsonObject.put("A", Canonical.create("B"));
    JsonObjectBuilder builder = factory.createObjectBuilder(jsonObject);
    assertNotNull(builder);
    builder.add("B", "C");
    JsonObject jo = builder.build();
    assertEquals(2, jo.size());
    jo.remove("B");
    assertEquals(jsonObject, jo);
  }


  @Test
  public void testCreateObjectBuilder1() {
    JsonObjectBuilder builder = factory.createObjectBuilder(Map.of("A", 1, "B", true, "C", Collections.emptyList()));
    JsonObject object = builder.build();
    assertEquals("{\"A\":1,\"B\":true,\"C\":[]}", object.toString());
  }

}
