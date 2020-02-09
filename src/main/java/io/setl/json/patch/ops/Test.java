package io.setl.json.patch.ops;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.JObject;
import io.setl.json.Primitive;
import io.setl.json.builder.JObjectBuilder;
import io.setl.json.exception.NoSuchValueException;
import io.setl.json.patch.PatchOperation;

/**
 * @author Simon Greatrix on 06/02/2020.
 */
@JsonInclude(Include.NON_NULL)
public class Test extends PatchOperation {

  private final String digest;

  private final JsonValue value;


  @JsonCreator
  public Test(
      @JsonProperty("path") String path,
      @JsonProperty("value") JsonValue value,
      @JsonProperty("digest") String digest
  ) {
    super(path);
    this.value = value;
    this.digest = digest;
    if (value == null && digest == null) {
      throw new IllegalArgumentException("Test case must specify at value or digest");
    }
  }


  public Test(JObject object) {
    super(object);
    this.value = object.optJsonValue("value");
    this.digest = object.optString("digest");
    if (value == null && digest == null) {
      throw new IllegalArgumentException("Test case must specify at value or digest");
    }
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    JsonValue jsonValue = pointer.getValue(target);
    if (value != null && !value.equals(jsonValue)) {
      throw new NoSuchValueException(getPath());
    }
    if (digest != null) {
      checkDigest(jsonValue);
    }
    return target;
  }


  private void checkDigest(JsonValue jsonValue) {
    int p = digest.indexOf('=');
    String algorithm;
    byte[] expected;
    if (p == -1) {
      algorithm = "SHA-512/256";
      expected = Base64.getUrlDecoder().decode(digest);
    } else {
      algorithm = digest.substring(0, p);
      expected = Base64.getUrlDecoder().decode(digest.substring(p + 1));
    }

    MessageDigest hash;
    try {
      hash = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new JsonException("Invalid digest algorithm: \"" + algorithm + "\"", e);
    }

    String canonical = Primitive.cast(jsonValue).toString();
    byte[] actual = hash.digest(canonical.getBytes(UTF_8));
    if (!MessageDigest.isEqual(expected, actual)) {
      throw new NoSuchValueException(getPath());
    }
  }


  public String getDigest() {
    return digest;
  }


  @Override
  public String getOp() {
    return "test";
  }


  public JsonValue getValue() {
    return value;
  }


  @Override
  public JsonObject toJsonObject() {
    JsonObjectBuilder builder = new JObjectBuilder()
        .add("op", getOp())
        .add("path", getPath());

    if (value != null) {
      builder.add("value", getValue());
    }
    if (digest != null) {
      builder.add("digest", getDigest());
    }

    return builder.build();
  }

}
