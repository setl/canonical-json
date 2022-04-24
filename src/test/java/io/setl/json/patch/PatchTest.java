package io.setl.json.patch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.setl.json.jackson.JsonModule;
import io.setl.json.primitive.CJString;

/**
 * @author Simon Greatrix on 11/02/2020.
 */
public class PatchTest {

  Patch patch;


  @BeforeEach
  public void createPatch() {
    PatchBuilder builder = new PatchBuilder();
    builder.add("/a/b/c", 1);
    builder.copy("/a/b/ex1", "/a/b/ex2");
    builder.digest("/a/b/c", "SHA-256", CJString.create("Hello, World!"));
    builder.move("/a/x", "/a/y");
    builder.remove("/c/d");
    builder.replace("/x", "/y");
    builder.test("/a/b/d1", true);

    patch = (Patch) builder.build();
  }


  @Test
  public void getOperations() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JsonModule());
    String json = mapper.writeValueAsString(patch);
    Patch copy = mapper.readValue(json, Patch.class);
    assertEquals(patch, copy);
  }


  @Test
  public void testEquals() {
    Patch copy = new Patch(patch.toJsonArray());
    assertEquals(patch, copy);
    assertEquals(patch.hashCode(), copy.hashCode());
  }


  @Test
  public void testToString() {
    assertEquals(patch.toString(), patch.toJsonArray().toString());
  }

}