package io.setl.json.patch.ops;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonPatch.Operation;
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

  /** The default digest algorithm used to calculate digests of canonical JSON (SHA-512/256). */
  public static final String DEFAULT_DIGEST = "SHA-512/256";


  /**
   * Calculate the digest of the canonical representation of a JsonValue, using the specified algorithm.
   *
   * @param algorithm the algorithm. If null or empty, the default algorithm is used.
   * @param jsonValue the value
   *
   * @return the digest
   *
   * @throws JsonException if the algorithm is invalid
   */
  public static byte[] digest(String algorithm, JsonValue jsonValue) {
    if (algorithm == null || algorithm.isEmpty()) {
      algorithm = DEFAULT_DIGEST;
    }
    MessageDigest hash;
    try {
      hash = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new JsonException("Invalid digest algorithm: \"" + algorithm + "\"", e);
    }

    String canonical = Primitive.cast(jsonValue).toString();
    return hash.digest(canonical.getBytes(UTF_8));
  }


  private final String digest;
  private final Boolean isPresent;
  private final JsonValue value;


  /**
   * New instance. At least one of <code>value</code> or <code>digest</code> must be specified.
   *
   * @param path      the path to test
   * @param value     the value to check against
   * @param digest    the digest to check against
   * @param isPresent If true, the value must be present. If false, the value must be absent. If null, the value can be either
   */
  @JsonCreator
  public Test(
      @JsonProperty("path") String path,
      @JsonProperty("value") JsonValue value,
      @JsonProperty("digest") String digest,
      @JsonProperty("isPresent") Boolean isPresent
  ) {
    super(path);
    this.value = value;
    this.digest = digest;
    this.isPresent = isPresent;
    checkConfig();
  }


  /**
   * Create a new instance from its JSON representation.
   *
   * @param object representation of the test
   */
  public Test(JObject object) {
    super(object);
    this.value = object.optJsonValue("value");
    this.digest = object.optString("digest");
    this.isPresent = object.optBoolean("isPresent");
    checkConfig();
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    JsonValue jsonValue = pointer.optValue(target);
    if (isPresent != null) {
      if (isPresent) {
        if (jsonValue != null) {
          return target;
        }
        throw new JsonException("Required value was not present at " + getPath());
      }
      if (jsonValue == null) {
        return target;
      }
      throw new JsonException("Value was present when required to be absent at " + getPath());
    }

    // by value and by digest tests require the value to be present
    if (jsonValue == null) {
      throw new NoSuchValueException(getPath());
    }
    if (value != null && !value.equals(jsonValue)) {
      throw new NoSuchValueException(getPath());
    }
    if (digest != null) {
      checkDigest(jsonValue);
    }
    return target;
  }


  private void checkConfig() {
    int c = (((value != null) ? 1 : 0) + ((digest != null) ? 1 : 0) + ((isPresent != null) ? 1 : 0));
    if (c != 1) {
      throw new IllegalArgumentException("Test case must specify exactly one of 'value', 'digest', or 'isPresent'");
    }
  }


  private void checkDigest(JsonValue jsonValue) {
    int p = digest.indexOf('=');
    String algorithm;
    byte[] expected;
    if (p == -1) {
      algorithm = DEFAULT_DIGEST;
      expected = Base64.getUrlDecoder().decode(digest);
    } else {
      algorithm = digest.substring(0, p);
      expected = Base64.getUrlDecoder().decode(digest.substring(p + 1));
    }

    byte[] actual = digest(algorithm, jsonValue);
    if (!MessageDigest.isEqual(expected, actual)) {
      throw new NoSuchValueException(getPath());
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Test)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    Test test = (Test) o;
    return Objects.equals(digest, test.digest) && Objects.equals(value, test.value);
  }


  public String getDigest() {
    return digest;
  }


  @Override
  public Operation getOperation() {
    return Operation.TEST;
  }


  public JsonValue getValue() {
    return value;
  }


  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (digest != null ? digest.hashCode() : 0);
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }


  @Override
  public JsonObject toJsonObject() {
    JsonObjectBuilder builder = new JObjectBuilder()
        .add("op", getOperation().operationName())
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
