package io.setl.json.patch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.json.JsonValue;

import org.junit.jupiter.api.Test;

import io.setl.json.patch.ops.Add;
import io.setl.json.primitive.CJString;

/**
 * @author Simon Greatrix on 11/02/2020.
 */
public class PatchBuilderTest {

  @Test
  public void test() {
    PatchBuilder builder = new PatchBuilder();
    builder.add("/a/b/c/1", JsonValue.NULL);
    builder.add("/a/b/c/2", "x");
    builder.add("/a/b/c/3", 1);
    builder.add("/a/b/c/4", true);
    builder.addOperation(0, new Add("/a/b/ops", JsonValue.EMPTY_JSON_ARRAY));
    builder.copy("/a/b/ex1", "/a/b/ex2");
    builder.digest("/a/b/c", "SHA-256", CJString.create("Hello, World!"));
    builder.move("/a/x", "/a/y");
    builder.remove("/c/d");
    builder.remove("/c/d");
    builder.removeOperation(builder.size() - 1);
    builder.replace("/x/1", "/y");
    builder.replace("/x/2", 5);
    builder.replace("/x/3", false);
    builder.replace("/x/4", JsonValue.EMPTY_JSON_OBJECT);
    builder.test("/a/b/d1", 10);
    builder.test("/a/b/d2", true);
    builder.test("/a/b/d3", "z");
    builder.test("/a/b/d4", JsonValue.EMPTY_JSON_OBJECT);

    assertEquals("[{\"op\":\"add\",\"path\":\"/a/b/ops\",\"value\":[]},"
        + "{\"op\":\"add\",\"path\":\"/a/b/c/1\",\"value\":null},"
        + "{\"op\":\"add\",\"path\":\"/a/b/c/2\",\"value\":\"x\"},"
        + "{\"op\":\"add\",\"path\":\"/a/b/c/3\",\"value\":1},"
        + "{\"op\":\"add\",\"path\":\"/a/b/c/4\",\"value\":true},"
        + "{\"from\":\"/a/b/ex2\",\"op\":\"copy\",\"path\":\"/a/b/ex1\"},"
        + "{\"digest\":\"SHA-256=zILrvPi2Clgh0cUccs15OA7OpH3jQ8yzsViTiis792Q=\",\"op\":\"test\",\"path\":\"/a/b/c\"},"
        + "{\"from\":\"/a/y\",\"op\":\"move\",\"path\":\"/a/x\"},"
        + "{\"op\":\"remove\",\"path\":\"/c/d\"},"
        + "{\"op\":\"replace\",\"path\":\"/x/1\",\"value\":\"/y\"},"
        + "{\"op\":\"replace\",\"path\":\"/x/2\",\"value\":5},"
        + "{\"op\":\"replace\",\"path\":\"/x/3\",\"value\":false},"
        + "{\"op\":\"replace\",\"path\":\"/x/4\",\"value\":{}},"
        + "{\"op\":\"test\",\"path\":\"/a/b/d1\",\"value\":10},"
        + "{\"op\":\"test\",\"path\":\"/a/b/d2\",\"value\":true},"
        + "{\"op\":\"test\",\"path\":\"/a/b/d3\",\"value\":\"z\"},"
        + "{\"op\":\"test\",\"path\":\"/a/b/d4\",\"value\":{}}]", builder.build().toString());
  }

}
