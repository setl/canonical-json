package io.setl.json;

import static org.junit.Assert.fail;

import io.setl.json.parser.JParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;
import org.junit.Test;

/**
 * Apply the unit tests defined at: https://github.com/nst/JSONTestSuite
 *
 * @author Simon
 */
public class TestParsing {

  private static String PATH = "test_parsing/";


  private Primitive loadResource(String resource) throws IOException {
    try (
        InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(PATH + resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
    ) {
      return Parser.parse(reader);
    }
  }


  private void loadStream(String resource) throws IOException {
    try (
        InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(PATH + resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
    ) {
      JParser parser = new JParser(reader);
      if (!parser.hasNext()) {
        throw new JsonParsingException("Input was expected", null);
      }
      while (parser.hasNext()) {
        parser.next();
      }
    }
  }


  @Test
  public void testParse() throws IOException {
    Primitive p = loadResource("all_files.json");
    JArray array = p.getValueSafe(JArray.class);
    for (JsonValue jv : array) {
      Primitive p2 = (Primitive) jv;
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

      if (f.startsWith("y_")) {
        // Required to parse successfully
        if (thrown == null) {
          System.out.println("PASS: " + f);
        } else {
          System.out.println("FAIL: " + f);
          System.out.println("Required to parse successfully, but failed with message: " + thrown.getMessage());
          fail(f);
        }
      }

      if (f.startsWith("n_")) {
        // Required to parse successfully
        if (thrown != null) {
          System.out.println("PASS: " + f + " : " + thrown.getMessage());
        } else {
          System.out.println("FAIL: " + f);
          System.out.println("Required to reject invalid input, but parsed OK");
          fail(f);
        }
      }
      if (f.startsWith("i_")) {
        if (thrown == null) {
          System.out.println("INFO: " + f + " parsed OK");
        } else {
          System.out.println("INFO: " + f + " failed with message: " + thrown.getMessage());
        }
      }
    }
  }


  @Test
  public void testStream() throws IOException {
    Primitive p = loadResource("all_files.json");
    JArray array = p.getValueSafe(JArray.class);
    for (JsonValue jv : array) {
      Primitive p2 = (Primitive) jv;
      String f = p2.getValueSafe(String.class);
      JsonParsingException thrown = null;
      try {
        loadStream(f);
      } catch (JsonParsingException ioe) {
        thrown = ioe;
      } catch (Error err) {
        throw new AssertionError("CRASH processing " + f, err);
      } catch (RuntimeException re) {
        throw new AssertionError("ABEND processing " + f, re);
      }

      if (f.startsWith("y_")) {
        // Required to parse successfully
        if (thrown == null) {
          System.out.println("PASS: " + f);
        } else {
          System.out.println("FAIL: " + f);
          System.out.println("Required to parse successfully, but failed with message: " + thrown.getMessage() + " at " + thrown.getLocation());
          thrown.printStackTrace(System.out);
          fail(f);
        }
      }

      if (f.startsWith("n_")) {
        // Required to parse successfully
        if (thrown != null) {
          System.out.println("PASS: " + f + " : " + thrown.getMessage() + " at " + thrown.getLocation());
        } else {
          System.out.println("FAIL: " + f);
          System.out.println("Required to reject invalid input, but parsed OK");
          fail(f);
        }
      }
      if (f.startsWith("i_")) {
        if (thrown == null) {
          System.out.println("INFO: " + f + " parsed OK");
        } else {
          System.out.println("INFO: " + f + " failed with message: " + thrown.getMessage());
        }
      }
    }
  }

}
