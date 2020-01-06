package io.setl.json;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Apply the malformed JSON tests defined at: https://github.com/gibson042/canonicaljson-spec
 * 
 * @author Simon
 */
public class TestMalformed {

  private static String PATH = "malformed/";


  private Primitive loadResource(String resource) throws IOException {
    try (InputStream input = TestMalformed.class.getClassLoader().getResourceAsStream(PATH + resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
      return Parser.parse(reader);
    }
  }


  @Test
  public void testParse() throws IOException {
    Primitive p = loadResource("all_input.json");
    JsonArray array = p.getValueSafe(JsonArray.class);
    for(Primitive p2:array) {
      String f = p2.getValueSafe(String.class);
      IOException thrown = null;
      try {
        loadResource(f);
      } catch (IOException ioe) {
        thrown = ioe;
      } catch (Error err) {
        throw new AssertionError("CRASH processing " + f, err);
      } catch (RuntimeException re) {
        throw new AssertionError("ABEND processing " + f, re);
      }
      
      // Required to parse unsuccessfully
      if( thrown != null ) {
        System.out.println("PASS: " + f + " : " + thrown.getMessage());
      } else {
        System.out.println("FAIL: " + f);
        System.out.println("Required to reject invalid input, but parsed OK");
        fail(f);
      }
    }
  }
}
