package com.pippsford.json;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Apply the unit tests defined at: https://github.com/nst/JSONTestSuite
 * 
 * @author Simon
 */
public class TestParsing {

  private static String PATH = "test_parsing/";


  private Primitive loadResource(String resource) throws IOException {
    try (InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(PATH + resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
      return Parser.parse(reader);
    }
  }


  @Test
  public void testParse() throws IOException {
    Primitive p = loadResource("all_files.json");
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

      if( f.startsWith("y_") ) {
        // Required to parse successfully
        if( thrown == null ) {
          System.out.println("PASS: " + f);
        } else {
          System.out.println("FAIL: " + f);
          System.out.println("Required to parse successfully, but failed with message: " + thrown.getMessage());
          fail(f);
        }
      }

      if( f.startsWith("n_") ) {
        // Required to parse successfully
        if( thrown != null ) {
          System.out.println("PASS: " + f + " : " + thrown.getMessage());
        } else {
          System.out.println("FAIL: " + f);
          System.out.println("Required to reject invalid input, but parsed OK");
          fail(f);
        }
      }
      if( f.startsWith("i_") ) {
        if( thrown == null ) {
          System.out.println("INFO: " + f + " parsed OK");
        } else {
          System.out.println("INFO: " + f + " failed with message: " + thrown.getMessage());
        }
      }
    }
  }

}
