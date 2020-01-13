package io.setl.json.builder;

import static org.junit.Assert.*;

import io.setl.json.JArray;
import io.setl.json.JObject;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
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
  public void testCreateArrayBuilder() {
    JObject object = new JObject();
    object.put("A",1);
    JArray array = new JArray();
    array.add(object);
    JsonArrayBuilder builder = factory.createArrayBuilder(array);
    assertNotNull(builder);
    JsonArray output = builder.build();
    assertEquals(1, output.size());
    assertNotSame(object,output.get(0));
    assertEquals(object,output.get(0));
  }


  @Test
  public void testCreateArrayBuilder1() {
    HashMap<String,Object> object = new HashMap<>();
    object.put("A",1);
    ArrayList<Object> array = new ArrayList<>();
    array.add(object);
    JsonArrayBuilder builder = factory.createArrayBuilder(array);
    assertNotNull(builder);
    JsonArray output = builder.build();
    assertEquals(1, output.size());
    assertNotSame(object,output.get(0));
    assertEquals(object,((JObject) output.get(0)).unwrap());
  }


  @Test
  public void createObjectBuilder() {
  }


  @Test
  public void testCreateObjectBuilder() {
  }


  @Test
  public void testCreateObjectBuilder1() {
  }


  @Test
  public void getConfigInUse() {
    assertTrue(factory.getConfigInUse().isEmpty());
  }
}