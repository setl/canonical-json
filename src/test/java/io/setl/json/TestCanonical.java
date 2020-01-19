package io.setl.json;

import io.setl.json.io.JReaderFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
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


  private Primitive loadJson(String resource, Function<Reader, Primitive> parser) throws IOException {
    try (
        InputStream input = TestParsing.class.getClassLoader().getResourceAsStream(resource);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
    ) {
      return parser.apply(reader);
    }
  }


  private void testParse(Function<Reader, Primitive> parser) throws IOException {
    Primitive p = loadJson("expected.json", parser);
    JArray array = p.getValueSafe(JArray.class);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    for (Primitive p2 : array.primitives()) {
      String f = p2.getValueSafe(String.class);

      Primitive primitive;
      try {
        primitive = loadJson(f + "input.json", parser);
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
      Canonical.stream(output, primitive);

      // HACK! Every expected.json file has a terminal NL character as that is how they are in the original repository. The NL character is not a correct part
      // of the output.
      output.write(10);

      byte[] expected = loadBytes(f + "expected.json");
      Assert.assertArrayEquals(expected, output.toByteArray());
    }
  }



  @Test
  public void testStream() throws IOException {
    testParse(r -> {
      JsonReader jr = new JReaderFactory().createReader(r);
      return (Primitive) jr.readValue();
    });
  }

}
