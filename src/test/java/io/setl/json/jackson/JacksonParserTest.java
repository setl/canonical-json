package io.setl.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator.Feature;
import com.fasterxml.jackson.dataformat.smile.SmileParser;
import io.setl.json.builder.JArrayBuilder;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.exception.JsonIOException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Simon Greatrix on 31/01/2020.
 */
public class JacksonParserTest {

  @Test
  public void test1() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    JsonObject object = new JObjectBuilder()
        .add("a", 1)
        .add("b", new JArrayBuilder().add("x").add("y").add("z"))
        .addNull("c")
        .add("d", new JObjectBuilder().add("e", true).add("f", false))
        .add("g", 1000000000000L)
        .add("h", BigInteger.ONE.shiftLeft(70))
        .add("i", new BigDecimal("1.234"))
        .build();

    jacksonGenerator.generate(object);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonParser jacksonParser = new JacksonParser(smileParser);
    JsonValue output = jacksonParser.read();
    jacksonParser.close();

    assertEquals(object.toString(), output.toString());
  }


  @Test(expected = JsonIOException.class)
  public void test10() throws IOException {
    JsonParser mock = Mockito.mock(JsonParser.class);
    Mockito.doThrow(new IOException()).when(mock).nextToken();
    JacksonParser parser = new JacksonParser(mock);
    parser.read();
  }


  @Test(expected = JsonIOException.class)
  public void test11() throws IOException {
    JsonParser mock = Mockito.mock(JsonParser.class);
    Mockito.doThrow(new IOException()).when(mock).nextToken();
    JacksonParser parser = new JacksonParser(mock);
    parser.readArray();
  }


  @Test(expected = JsonIOException.class)
  public void test12() throws IOException {
    JsonParser mock = Mockito.mock(JsonParser.class);
    Mockito.doThrow(new IOException()).when(mock).nextToken();
    JacksonParser parser = new JacksonParser(mock);
    parser.readObject();
  }

  @Test(expected = JsonParsingException.class)
  public void test14() throws IOException {
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileParser smileParser = smileFactory.createParser(new byte[0]);
    JacksonParser jacksonParser = new JacksonParser(smileParser);
    jacksonParser.readValue();
  }


  @Test
  public void test2() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);
    jacksonGenerator.generate(null);
    jacksonGenerator.generate(JsonValue.TRUE);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonParser jacksonParser = new JacksonParser(smileParser);
    JsonValue output = jacksonParser.readValue();
    assertEquals(JsonValue.NULL, output);

    try {
      // should not be able to read two values
      jacksonParser.readValue();
      fail();
    } catch (IllegalStateException e) {
      // correct
    }
  }


  @Test
  public void test3() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    JsonObject object = new JObjectBuilder()
        .add("a", 1)
        .add("b", new JArrayBuilder().add("x").add("y").add("z"))
        .addNull("c")
        .add("d", new JObjectBuilder().add("e", true).add("f", false))
        .add("g", 1000000000000L)
        .add("h", BigInteger.ONE.shiftLeft(70))
        .add("i", new BigDecimal("1.234"))
        .build();

    jacksonGenerator.generate(object);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonParser jacksonParser = new JacksonParser(smileParser);
    JsonObject output = jacksonParser.readObject();
    jacksonParser.close();

    assertEquals(object.toString(), output.toString());
  }


  @Test(expected = JsonParsingException.class)
  public void test4() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    jacksonGenerator.generate(JsonValue.TRUE);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonParser jacksonParser = new JacksonParser(smileParser);
    jacksonParser.readObject();
  }


  @Test(expected = JsonParsingException.class)
  public void test5() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    jacksonGenerator.generate(JsonValue.TRUE);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonParser jacksonParser = new JacksonParser(smileParser);
    jacksonParser.readArray();
  }


  @Test(expected = JsonParsingException.class)
  public void test6() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    jacksonGenerator.generate(JsonValue.TRUE);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonParser jacksonParser = new JacksonParser(smileParser);
    jacksonParser.read();
  }


  @Test
  public void test7() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    JsonArray array = new JArrayBuilder().add("x").add("y").add("z")
        .add(new JObjectBuilder().add("e", true).add("f", false))
        .add(new JArrayBuilder().add(1).add(1.234).add(true))
        .build();

    jacksonGenerator.generate(array);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonParser jacksonParser = new JacksonParser(smileParser);
    JsonStructure output = jacksonParser.read();
    jacksonParser.close();

    assertEquals(array.toString(), output.toString());
  }


  @Test
  public void test8() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    JsonArray array = new JArrayBuilder().add("x").add("y").add("z")
        .add(new JObjectBuilder().add("e", true).add("f", false))
        .add(new JArrayBuilder().add(1).add(1.234).add(true))
        .build();

    jacksonGenerator.generate(array);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonParser jacksonParser = new JacksonParser(smileParser);
    JsonStructure output = jacksonParser.readArray();
    jacksonParser.close();

    assertEquals(array.toString(), output.toString());
  }


  @Test(expected = JsonIOException.class)
  public void test9() throws IOException {
    JsonParser mock = Mockito.mock(JsonParser.class);
    Mockito.doThrow(new IOException()).when(mock).nextToken();
    JacksonParser parser = new JacksonParser(mock);
    parser.readValue();
  }
}