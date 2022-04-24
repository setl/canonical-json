package io.setl.json;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;

import org.junit.jupiter.api.Test;

import io.setl.json.io.Location;
import io.setl.json.io.ReaderFactory;
import io.setl.json.parser.Parser;
import io.setl.json.parser.ParserFactory;

/**
 * Apply the unit tests defined at: https://github.com/nst/JSONTestSuite
 *
 * @author Simon
 */
public class TestParsing {

  public static boolean isDebug = false;

  private static String PATH = "test_parsing/";



  interface Exec {

    void doIt(String r) throws IOException;

  }


  private JsonValue loadResource(String resource) throws IOException {
    ReaderFactory readerFactory = new ReaderFactory();
    try (
        InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(PATH + resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
    ) {
      JsonReader jsonReader = readerFactory.createReader(reader);
      JsonValue jsonValue = jsonReader.readValue();
      jsonReader.close();
      return jsonValue;
    }
  }


  private void loadStream(String resource) throws IOException {
    try (
        InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(PATH + resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
    ) {
      Parser parser = new Parser(reader);
      if (!parser.hasNext()) {
        throw new JsonParsingException("Input was expected", null);
      }
      while (parser.hasNext()) {
        parser.next();
      }
    }
  }


  private JsonValue parseResource(String resource) throws IOException {
    ParserFactory factory = new ParserFactory(null);
    try (
        InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(PATH + resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
    ) {
      JsonParser parser = factory.createParser(reader);
      if (!parser.hasNext()) {
        throw new JsonParsingException("No data found in document", Location.UNSET);
      }
      parser.next();
      JsonValue jsonValue = parser.getValue();
      assertFalse(parser.hasNext());
      return jsonValue;
    }
  }


  private void test(Exec exec) throws IOException {
    JsonArray array = (JsonArray) loadResource("all_files.json");
    for (JsonValue jv : array) {
      Canonical p2 = (Canonical) jv;
      String f = p2.getValueSafe(String.class);
      JsonParsingException thrown = null;
      try {
        exec.doIt(f);
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
          if (isDebug) {
            System.out.println("PASS: " + f);
          }
        } else {
          if (isDebug) {
            System.out.println("FAIL: " + f);
            System.out.println("Required to parse successfully, but failed with message: " + thrown.getMessage() + " at " + thrown.getLocation());
            thrown.printStackTrace(System.out);
          }
          fail(f);
        }
      }

      if (f.startsWith("n_")) {
        // Required to parse successfully
        if (thrown != null) {
          if (isDebug) {
            System.out.println("PASS: " + f + " : " + thrown.getMessage() + " at " + thrown.getLocation());
          }
        } else {
          if (isDebug) {
            System.out.println("FAIL: " + f);
            System.out.println("Required to reject invalid input, but parsed OK");
          }
          fail(f);
        }
      }
      if (f.startsWith("i_")) {
        if (thrown == null) {
          if (isDebug) {
            System.out.println("INFO: " + f + " parsed OK");
          }
        } else {
          if (isDebug) {
            System.out.println("INFO: " + f + " failed with message: " + thrown.getMessage());
          }
        }
      }
    }
  }


  @Test
  public void testParse() throws IOException {
    test(f -> parseResource(f));
  }


  @Test
  public void testReader() throws IOException {
    test(f -> loadResource(f));
  }


  @Test
  public void testStream() throws IOException {
    test(f -> loadStream(f));
  }

}
