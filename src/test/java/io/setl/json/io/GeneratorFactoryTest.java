package io.setl.json.io;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.setl.json.io.GeneratorFactory.TRUST_KEY_ORDER;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import jakarta.json.JsonException;
import jakarta.json.stream.JsonGenerator;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 28/01/2020.
 */
public class GeneratorFactoryTest {

  GeneratorFactory factory = new GeneratorFactory(null);


  @Test
  public void createGenerator() {
    JsonGenerator generator = factory.createGenerator(Writer.nullWriter());
    assertTrue(generator instanceof SafeGenerator);

    factory = new GeneratorFactory(Map.of(TRUST_KEY_ORDER, "true"));
    generator = factory.createGenerator(Writer.nullWriter());
    assertTrue(generator instanceof TrustedGenerator);
  }


  @Test
  public void getConfigInUse() {
    Map<String, ?> map = factory.getConfigInUse();
    assertFalse((Boolean) map.get(TRUST_KEY_ORDER));

    factory = new GeneratorFactory(Map.of(TRUST_KEY_ORDER, "true"));
    map = factory.getConfigInUse();
    assertTrue((Boolean) map.get(TRUST_KEY_ORDER));

    factory = new GeneratorFactory(Map.of(TRUST_KEY_ORDER, true));
    map = factory.getConfigInUse();
    assertTrue((Boolean) map.get(TRUST_KEY_ORDER));

    factory = new GeneratorFactory(Map.of(TRUST_KEY_ORDER, "false"));
    map = factory.getConfigInUse();
    assertFalse((Boolean) map.get(TRUST_KEY_ORDER));
  }


  @Test
  public void testCreateGenerator() {
    JsonGenerator generator = factory.createGenerator(OutputStream.nullOutputStream());
    assertTrue(generator instanceof SafeGenerator);
  }


  @Test
  public void testCreateGenerator1() {
    JsonGenerator generator = factory.createGenerator(OutputStream.nullOutputStream(), UTF_8);
    assertTrue(generator instanceof SafeGenerator);
  }


  @Test
  public void testCreateGenerator2() {
    JsonException exception = assertThrows(JsonException.class, () -> factory.createGenerator(OutputStream.nullOutputStream(), ISO_8859_1));
    assertEquals("Canonical JSON must be in UTF-8", exception.getMessage());
  }

}
