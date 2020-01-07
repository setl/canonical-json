package io.setl.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the expected output from unit tests defined at https://github.com/gibson042/canonicaljson-spec
 *
 * @author Simon
 */
public class TestCanonical {

  private byte[] loadBytes(String resource) throws IOException {
    try (InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(resource); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      byte[] transfer = new byte[8192];
      int r;
      while ((r = input.read(transfer)) != -1) {
        output.write(transfer, 0, r);
      }
      return output.toByteArray();
    }
  }


  private Primitive loadJson(String resource) throws IOException {
    try (
        InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
    ) {
      return Parser.parse(reader);
    }
  }


  @Test
  public void testParse() throws IOException {
    Primitive p = loadJson("expected.json");
    JsonArray array = p.getValueSafe(JsonArray.class);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    for (Primitive p2 : array) {
      String f = p2.getValueSafe(String.class);

      Primitive primitive;
      try {
        primitive = loadJson(f + "input.json");
      } catch (IOException ioe) {
        throw new AssertionError("FAIL processing " + f + " : " + ioe.getMessage(), ioe);
      } catch (Error err) {
        throw new AssertionError("CRASH processing " + f, err);
      } catch (RuntimeException re) {
        throw new AssertionError("ABEND processing " + f, re);
      }
      output.reset();
      Canonical.stream(output, primitive);

      // HACK! Every expected.json file has a terminal NL character as that is how they are in the original repository. The NL character is not a correct part
      // of the output.
      output.write(10);

      byte[] expected = loadBytes(f + "expected.json");
      Assert.assertArrayEquals(expected, output.toByteArray());
    }
  }
}
