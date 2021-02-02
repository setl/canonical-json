package io.setl.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator.Feature;
import com.fasterxml.jackson.dataformat.smile.SmileParser;
import org.junit.Test;
import org.mockito.Mockito;

import io.setl.json.builder.ArrayBuilder;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.exception.JsonIOException;

/**
 * @author Simon Greatrix on 31/01/2020.
 */
public class JacksonReaderTest {

  @Test
  public void test1() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    JsonObject object = new ObjectBuilder()
        .add("a", 1)
        .add("b", new ArrayBuilder().add("x").add("y").add("z"))
        .addNull("c")
        .add("d", new ObjectBuilder().add("e", true).add("f", false))
        .add("g", 1000000000000L)
        .add("h", BigInteger.ONE.shiftLeft(70))
        .add("i", new BigDecimal("1.234"))
        .build();

    jacksonGenerator.generate(object);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonReader jacksonReader = new JacksonReader(smileParser);
    JsonValue output = jacksonReader.read();
    jacksonReader.close();

    assertEquals(object.toString(), output.toString());
  }


  @Test(expected = JsonIOException.class)
  public void test10() throws IOException {
    JsonParser mock = Mockito.mock(JsonParser.class);
    Mockito.doThrow(new IOException()).when(mock).nextToken();
    JacksonReader parser = new JacksonReader(mock);
    parser.read();
  }


  @Test(expected = JsonIOException.class)
  public void test11() throws IOException {
    JsonParser mock = Mockito.mock(JsonParser.class);
    Mockito.doThrow(new IOException()).when(mock).nextToken();
    JacksonReader parser = new JacksonReader(mock);
    parser.readArray();
  }


  @Test(expected = JsonIOException.class)
  public void test12() throws IOException {
    JsonParser mock = Mockito.mock(JsonParser.class);
    Mockito.doThrow(new IOException()).when(mock).nextToken();
    JacksonReader parser = new JacksonReader(mock);
    parser.readObject();
  }


  @Test(expected = JsonParsingException.class)
  public void test14() throws IOException {
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileParser smileParser = smileFactory.createParser(new byte[0]);
    JacksonReader jacksonReader = new JacksonReader(smileParser);
    jacksonReader.readValue();
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
    JacksonReader jacksonReader = new JacksonReader(smileParser);
    JsonValue output = jacksonReader.readValue();
    assertEquals(JsonValue.NULL, output);

    try {
      // should not be able to read two values
      jacksonReader.readValue();
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

    JsonObject object = new ObjectBuilder()
        .add("a", 1)
        .add("b", new ArrayBuilder().add("x").add("y").add("z"))
        .addNull("c")
        .add("d", new ObjectBuilder().add("e", true).add("f", false))
        .add("g", 1000000000000L)
        .add("h", BigInteger.ONE.shiftLeft(70))
        .add("i", new BigDecimal("1.234"))
        .build();

    jacksonGenerator.generate(object);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonReader jacksonReader = new JacksonReader(smileParser);
    JsonObject output = jacksonReader.readObject();
    jacksonReader.close();

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
    JacksonReader jacksonReader = new JacksonReader(smileParser);
    jacksonReader.readObject();
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
    JacksonReader jacksonReader = new JacksonReader(smileParser);
    jacksonReader.readArray();
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
    JacksonReader jacksonReader = new JacksonReader(smileParser);
    jacksonReader.read();
  }


  @Test
  public void test7() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    JsonArray array = new ArrayBuilder().add("x").add("y").add("z")
        .add(new ObjectBuilder().add("e", true).add("f", false))
        .add(new ArrayBuilder().add(1).add(1.234).add(true))
        .build();

    jacksonGenerator.generate(array);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonReader jacksonReader = new JacksonReader(smileParser);
    JsonStructure output = jacksonReader.read();
    jacksonReader.close();

    assertEquals(array.toString(), output.toString());
  }


  @Test
  public void test8() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SmileFactory smileFactory = new SmileFactory();
    smileFactory.enable(Feature.CHECK_SHARED_STRING_VALUES);
    SmileGenerator smileGenerator = smileFactory.createGenerator(outputStream);
    JacksonGenerator jacksonGenerator = new JacksonGenerator(smileGenerator);

    JsonArray array = new ArrayBuilder().add("x").add("y").add("z")
        .add(new ObjectBuilder().add("e", true).add("f", false))
        .add(new ArrayBuilder().add(1).add(1.234).add(true))
        .build();

    jacksonGenerator.generate(array);
    jacksonGenerator.close();

    SmileParser smileParser = smileFactory.createParser(outputStream.toByteArray());
    JacksonReader jacksonReader = new JacksonReader(smileParser);
    JsonStructure output = jacksonReader.readArray();
    jacksonReader.close();

    assertEquals(array.toString(), output.toString());
  }


  @Test(expected = JsonIOException.class)
  public void test9() throws IOException {
    JsonParser mock = Mockito.mock(JsonParser.class);
    Mockito.doThrow(new IOException()).when(mock).nextToken();
    JacksonReader parser = new JacksonReader(mock);
    parser.readValue();
  }

}
