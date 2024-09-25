package io.setl.json;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.function.Function;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParsingException;

import org.junit.jupiter.api.Test;

import io.setl.json.io.ReaderFactory;

/**
 * Test the expected output from unit tests defined at https://github.com/gibson042/canonicaljson-spec
 *
 * @author Simon
 */
public class TestCanonical {

  private byte[] loadBytes(String resource) throws IOException {
    try (
        InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(resource);
        ByteArrayOutputStream output = new ByteArrayOutputStream()
    ) {
      byte[] transfer = new byte[8192];
      int r;
      while ((r = input.read(transfer)) != -1) {
        output.write(transfer, 0, r);
      }
      return output.toByteArray();
    }
  }


  private Canonical loadJson(String resource, Function<Reader, Canonical> parser) throws IOException {
    try (
        InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(resource);
        Reader reader = new InputStreamReader(input, UTF_8)
    ) {
      return parser.apply(reader);
    }
  }


  private void testParse(Function<Reader, Canonical> parser) throws IOException {
    Canonical p = loadJson("expected.json", parser);
    CJArray array = p.getValueSafe(CJArray.class);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    for (JsonValue p2 : array) {
      String f = Canonical.getValue(String.class, p2);

      Canonical canonical;
      try {
        canonical = loadJson(f + "input.json", parser);
      } catch (IOException ioe) {
        throw new AssertionError("FAIL processing " + f + " : " + ioe.getMessage(), ioe);
      } catch (Error err) {
        throw new AssertionError("CRASH processing " + f, err);
      } catch (RuntimeException re) {
        if (re instanceof JsonParsingException) {
          System.out.println("location: " + ((JsonParsingException) re).getLocation());
        }
        throw new AssertionError("ABEND processing " + f, re);
      }
      output.reset();
      canonical.writeTo(output);

      // HACK! Every expected.json file has a terminal NL character as that is how they are in the original repository. The NL character is not a correct part
      // of the output.
      byte[] expected = loadBytes(f + "expected.json");
      for (int i = 2; i >= 1; i--) {
        byte e = expected[expected.length - i];
        if (e == 10 || e == 13) {
          output.write(e);
        }
      }

      if (!Arrays.equals(expected, output.toByteArray())) {
        System.out.println(f);
        System.out.println(new String(expected, UTF_8));
        System.out.println(new String(output.toByteArray(), UTF_8));
        assertEquals(new String(expected, UTF_8), new String(output.toByteArray(), UTF_8));
        fail(f);
      }
    }
  }


  @Test
  public void testStream() throws IOException {
    testParse(r -> {
      JsonReader jr = new ReaderFactory().createReader(r);
      return (Canonical) jr.readValue();
    });
  }

}
