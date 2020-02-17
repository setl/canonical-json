package io.setl.json.patch;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import io.setl.json.jackson.JsonModule;
import io.setl.json.primitive.PString;

/**
 * @author Simon Greatrix on 11/02/2020.
 */
public class JPatchTest {

  JPatch patch;


  @Before
  public void createPatch() {
    JPatchBuilder builder = new JPatchBuilder();
    builder.add("/a/b/c", 1);
    builder.copy("/a/b/ex1", "/a/b/ex2");
    builder.digest("/a/b/c", "SHA-256", PString.create("Hello, World!"));
    builder.move("/a/x", "/a/y");
    builder.remove("/c/d");
    builder.replace("/x", "/y");
    builder.test("/a/b/d1", true);

    patch = (JPatch) builder.build();
  }


  @Test
  public void getOperations() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JsonModule());
    String json = mapper.writeValueAsString(patch);
    System.out.println(json);
    JPatch copy = mapper.readValue(json, JPatch.class);
    assertEquals(patch, copy);
  }


  @Test
  public void testEquals() {
    JPatch copy = new JPatch(patch.toJsonArray());
    assertEquals(patch, copy);
    assertEquals(patch.hashCode(), copy.hashCode());
  }


  @Test
  public void testToString() {
    assertEquals(patch.toString(), patch.toJsonArray().toString());
  }

}