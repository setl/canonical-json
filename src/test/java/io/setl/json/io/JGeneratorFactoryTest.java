package io.setl.json.io;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.setl.json.io.JGeneratorFactory.TRUST_KEY_ORDER;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import javax.json.JsonException;
import javax.json.stream.JsonGenerator;

import org.junit.Test;

/**
 * @author Simon Greatrix on 28/01/2020.
 */
public class JGeneratorFactoryTest {

  JGeneratorFactory factory = new JGeneratorFactory(null);


  @Test
  public void createGenerator() {
    JsonGenerator generator = factory.createGenerator(Writer.nullWriter());
    assertTrue(generator instanceof JSafeGenerator);

    factory = new JGeneratorFactory(Map.of(TRUST_KEY_ORDER, "true"));
    generator = factory.createGenerator(Writer.nullWriter());
    assertTrue(generator instanceof JTrustedGenerator);
  }


  @Test
  public void getConfigInUse() {
    Map<String, ?> map = factory.getConfigInUse();
    assertFalse((Boolean) map.get(TRUST_KEY_ORDER));

    factory = new JGeneratorFactory(Map.of(TRUST_KEY_ORDER, "true"));
    map = factory.getConfigInUse();
    assertTrue((Boolean) map.get(TRUST_KEY_ORDER));

    factory = new JGeneratorFactory(Map.of(TRUST_KEY_ORDER, true));
    map = factory.getConfigInUse();
    assertTrue((Boolean) map.get(TRUST_KEY_ORDER));

    factory = new JGeneratorFactory(Map.of(TRUST_KEY_ORDER, "false"));
    map = factory.getConfigInUse();
    assertFalse((Boolean) map.get(TRUST_KEY_ORDER));
  }


  @Test
  public void testCreateGenerator() {
    JsonGenerator generator = factory.createGenerator(OutputStream.nullOutputStream());
    assertTrue(generator instanceof JSafeGenerator);
  }


  @Test
  public void testCreateGenerator1() {
    JsonGenerator generator = factory.createGenerator(OutputStream.nullOutputStream(), UTF_8);
    assertTrue(generator instanceof JSafeGenerator);
  }


  @Test(expected = JsonException.class)
  public void testCreateGenerator2() {
    factory.createGenerator(OutputStream.nullOutputStream(), ISO_8859_1);
  }

}
