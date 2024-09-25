package io.setl.json.merge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.Test;

import io.setl.json.CJObject;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * @author Simon Greatrix on 31/01/2020.
 */
public class MergeDiffTest {

  @Test
  public void test1() {
    JsonValue in = CJNumber.create(1);
    JsonValue out = CJString.create("wibble");
    Merge merge = MergeDiff.create(in, out);
    JsonValue result = merge.apply(in);
    assertEquals(out, result);
  }


  @Test
  public void test2() {
    JsonValue in = new ObjectBuilder().add("a", 1).add("b", 2).add("c", 3).build();
    JsonValue out = new ObjectBuilder().add("a", 1).add("b", true).add("c", 3).build();
    Merge merge = MergeDiff.create(in, out);
    JsonValue result = merge.apply(in);
    assertEquals(out, result);
  }


  @Test
  public void test3() {
    JsonValue in = new ObjectBuilder().add("a", 1).add("b", 2).add("c", 3).build();
    JsonValue out = new ObjectBuilder().add("a", 1).add("b", true).build();
    Merge merge = MergeDiff.create(in, out);
    JsonValue result = merge.apply(in);
    assertEquals(out, result);
  }


  @Test
  public void test4() {
    JsonValue in = new ObjectBuilder()
        .add("a", 1.02)
        .add("b", new CJObject(Map.of("x", true, "y", false, "z", "wibble")))
        .add("c", 3).build();
    JsonValue out = new ObjectBuilder()
        .add("a", 1)
        .add("b", new CJObject(Map.of("x", true, "y", false, "z", "wibble", "!", "!")))
        .add("c", new CJObject(Map.of("x", true, "y", false))).build();
    Merge merge = MergeDiff.create(in, out);
    JsonValue result = merge.apply(in);
    assertEquals(out, result);
    assertEquals("{\"a\":1,\"b\":{\"!\":\"!\"},\"c\":{\"x\":true,\"y\":false}}", merge.toJsonValue().toString());
  }

}
