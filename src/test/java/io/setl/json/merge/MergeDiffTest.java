package io.setl.json.merge;

import static org.junit.Assert.assertEquals;

import io.setl.json.JCanonicalObject;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.numbers.PNumber;
import java.util.Map;
import javax.json.JsonValue;
import org.junit.Test;

/**
 * @author Simon Greatrix on 31/01/2020.
 */
public class MergeDiffTest {

  @Test
  public void test1() {
    JsonValue in = PNumber.create(1);
    JsonValue out = PString.create("wibble");
    JMerge merge = MergeDiff.create(in, out);
    JsonValue result = merge.apply(in);
    assertEquals(out, result);
  }


  @Test
  public void test2() {
    JsonValue in = new JObjectBuilder().add("a", 1).add("b", 2).add("c", 3).build();
    JsonValue out = new JObjectBuilder().add("a", 1).add("b", true).add("c", 3).build();
    JMerge merge = MergeDiff.create(in, out);
    JsonValue result = merge.apply(in);
    assertEquals(out, result);
  }


  @Test
  public void test3() {
    JsonValue in = new JObjectBuilder().add("a", 1).add("b", 2).add("c", 3).build();
    JsonValue out = new JObjectBuilder().add("a", 1).add("b", true).build();
    JMerge merge = MergeDiff.create(in, out);
    JsonValue result = merge.apply(in);
    assertEquals(out, result);
  }


  @Test
  public void test4() {
    JsonValue in = new JObjectBuilder()
        .add("a", 1.02)
        .add("b", new JCanonicalObject(Map.of("x", true, "y", false, "z", "wibble")))
        .add("c", 3).build();
    JsonValue out = new JObjectBuilder()
        .add("a", 1)
        .add("b", new JCanonicalObject(Map.of("x", true, "y", false, "z", "wibble", "!","!")))
        .add("c", new JCanonicalObject(Map.of("x", true, "y", false))).build();
    JMerge merge = MergeDiff.create(in, out);
    JsonValue result = merge.apply(in);
    assertEquals(out, result);
    assertEquals("{\"a\":1,\"b\":{\"!\":\"!\"},\"c\":{\"x\":true,\"y\":false}}", merge.toJsonValue().toString());
  }
}