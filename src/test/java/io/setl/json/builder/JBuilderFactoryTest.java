package io.setl.json.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import io.setl.json.JArray;
import io.setl.json.JObject;
import io.setl.json.Primitive;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.junit.Test;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JBuilderFactoryTest {

  JBuilderFactory factory = new JBuilderFactory();


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
    JObject object = new JObject();
    object.put("A", 1);
    JArray array = new JArray();
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
    assertEquals(object, ((JObject) output.get(0)).unwrap());
  }


  @Test
  public void testCreateObjectBuilder() {
    JsonObject jsonObject = new JObject();
    jsonObject.put("A", Primitive.create("B"));
    JsonObjectBuilder builder = factory.createObjectBuilder(jsonObject);
    assertNotNull(builder);
    builder.add("B","C");
    JsonObject jo = builder.build();
    assertEquals(2,jo.size());
    jo.remove("B");
    assertEquals(jsonObject,jo);
  }


  @Test
  public void testCreateObjectBuilder1() {
    JsonObjectBuilder builder = factory.createObjectBuilder(Map.of("A",1,"B",true,"C", Collections.emptyList()));
    JsonObject object = builder.build();
    assertEquals("{\"A\":1,\"B\":true,\"C\":[]}",object.toString());
  }
}