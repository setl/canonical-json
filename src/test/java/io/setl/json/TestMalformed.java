package io.setl.json;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.json.JsonArray;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

import org.junit.jupiter.api.Test;

import io.setl.json.io.ReaderFactory;

/**
 * Apply the malformed JSON tests defined at: https://github.com/gibson042/canonicaljson-spec
 *
 * @author Simon
 */
public class TestMalformed {

  public static boolean isDebug = false;


  private JsonValue loadResource(String resource) throws IOException {
    try (
        InputStream input = TestMalformed.class.getClassLoader().getResourceAsStream("malformed/" + resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
    ) {
      return new ReaderFactory().createReader(reader).readValue();
    }
  }


  @Test
  public void testParse() throws IOException {
    JsonArray array = (JsonArray) loadResource("all_input.json");
    for (String f : array.getValuesAs(JsonString::getString)) {
      JsonParsingException thrown = null;
      try {
        loadResource(f);
      } catch (JsonParsingException ioe) {
        thrown = ioe;
      } catch (Error err) {
        throw new AssertionError("CRASH processing " + f, err);
      } catch (RuntimeException re) {
        throw new AssertionError("ABEND processing " + f, re);
      }

      // Required to parse unsuccessfully
      if (thrown != null) {
        if (isDebug) {
          System.out.println("PASS: " + f + " : " + thrown.getMessage());
        }
      } else {
        if (isDebug) {
          System.out.println("FAIL: " + f);
          System.out.println("Required to reject invalid input, but parsed OK");
        }
        fail(f);
      }
    }
  }

}
