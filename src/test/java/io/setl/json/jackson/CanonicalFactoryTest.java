package io.setl.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonpCharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

/**
 * @author Simon Greatrix on 03/01/2020.
 */
public class CanonicalFactoryTest {

  CanonicalFactory instance = new CanonicalFactory();


  @Test
  public void canHandleBinaryNatively() {
    assertFalse(instance.canHandleBinaryNatively());
  }


  @Test(expected = UnsupportedOperationException.class)
  public void createGeneratorNotUtf8() throws IOException {
    instance.createGenerator(new File("test"), JsonEncoding.UTF16_BE);
  }


  @Test(expected = UnsupportedOperationException.class)
  public void createGeneratorNotUtf8_2() throws IOException {
    instance.createGenerator(new ByteArrayOutputStream(), JsonEncoding.UTF16_BE);
  }


  @Test
  public void createGeneratorUtf8_1() throws IOException {
    // We just assume the generator works
    assertNotNull(instance.createGenerator(new ByteArrayOutputStream(), JsonEncoding.UTF8));
  }


  @Test
  public void createGeneratorUtf8_2() throws IOException {
    File file = File.createTempFile("delete_me.", ".json");
    JsonGenerator jsonGenerator = instance.createGenerator(file, JsonEncoding.UTF8);

    // We just assume the generator works
    assertNotNull(jsonGenerator);

    jsonGenerator.close();
    file.delete();
  }


  @Test
  public void disable() {
    instance.disable(Feature.AUTO_CLOSE_TARGET);
  }


  @SuppressWarnings("deprecation")
  @Test(expected = UnsupportedOperationException.class)
  public void disable_1() {
    instance.disable(Feature.QUOTE_FIELD_NAMES);
  }


  @SuppressWarnings("deprecation")
  @Test(expected = UnsupportedOperationException.class)
  public void disable_2() {
    instance.disable(Feature.QUOTE_NON_NUMERIC_NUMBERS);
  }


  @Test
  public void doTest() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper(new CanonicalFactory());
    Random random = new Random(0x7e57ab1e);
    Pojo root = new Pojo(random, true);

    String json = objectMapper.writeValueAsString(root);
    assertEquals(
        "{\"count\":24,\"data\":[5.156311201658089E-1,8.997613044339822E-1,8.898458711493542E-1],"
            + "\"sibling\":{\"count\":39,\"data\":[2.6323510263178707E-1,6.379952106951381E-1,9.824964487440964E-1],"
            + "\"sibling\":null,\"text\":\"14z05unwoqriq\"},\"text\":\"-fxc8p6ycqbui\"}",
        json
    );
  }


  @SuppressWarnings("deprecation")
  @Test(expected = UnsupportedOperationException.class)
  public void enable_1() {
    instance.enable(Feature.ESCAPE_NON_ASCII);
  }


  @Test(expected = UnsupportedOperationException.class)
  public void enable_2() {
    instance.enable(Feature.WRITE_BIGDECIMAL_AS_PLAIN);
  }


  @Test(expected = UnsupportedOperationException.class)
  @SuppressWarnings("deprecation")
  public void enable_3() {
    instance.enable(Feature.WRITE_NUMBERS_AS_STRINGS);
  }


  @Test
  public void enable_4() {
    instance.enable(Feature.AUTO_CLOSE_TARGET);
  }


  @Test
  public void getFormatName() {
    assertEquals("JSON", instance.getFormatName());
  }


  public void requiresPropertyOrdering() {
    assertTrue(instance.requiresPropertyOrdering());
  }


  @Test(expected = UnsupportedOperationException.class)
  public void setCharacterEscapes() {
    instance.setCharacterEscapes(new JsonpCharacterEscapes());
  }

}
