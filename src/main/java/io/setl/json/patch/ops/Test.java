package io.setl.json.patch.ops;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import jakarta.json.JsonException;
import jakarta.json.JsonPatch.Operation;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.json.CJObject;
import io.setl.json.Canonical;
import io.setl.json.builder.ObjectBuilder;
import io.setl.json.exception.IncorrectDigestException;
import io.setl.json.exception.IncorrectValueException;
import io.setl.json.patch.PatchOperation;
import io.setl.json.pointer.JsonExtendedPointer;
import io.setl.json.pointer.JsonExtendedPointer.ResultOfAdd;

/**
 * A "test" operation. The standard test is extended to allow verification of cryptographic digests.
 *
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

    String canonical = Canonical.toCanonicalString(Canonical.cast(jsonValue));
    return hash.digest(canonical.getBytes(UTF_8));
  }


  private final String digest;

  private final ResultOfAdd resultOfAdd;

  private final JsonValue value;


  /**
   * New instance. Exactly one of <code>value</code>, <code>digest</code>, or <code>resultOfAdd</code> must be specified.
   *
   * @param path        the path to test
   * @param value       the value to check against
   * @param digest      the digest to check against
   * @param resultOfAdd the required result of an add operation to this
   */
  @JsonCreator
  public Test(
      @JsonProperty("path") String path,
      @JsonProperty("value") JsonValue value,
      @JsonProperty("digest") String digest,
      @JsonProperty("resultOfAdd") ResultOfAdd resultOfAdd
  ) {
    super(path);
    this.value = value;
    this.digest = digest;
    this.resultOfAdd = resultOfAdd;
    checkConfig();
  }


  /**
   * New instance for a value comparison.
   *
   * @param pointer the path to test
   * @param value   the value it must equal
   */
  public Test(JsonExtendedPointer pointer, JsonValue value) {
    super(pointer);
    Objects.requireNonNull(value, "Test value must not be null");
    this.value = value;
    digest = null;
    resultOfAdd = null;
  }


  /**
   * New instance for a value comparison.
   *
   * @param pointer the path to test
   * @param digest  the digest the value must have
   */
  public Test(JsonExtendedPointer pointer, String digest) {
    super(pointer);
    Objects.requireNonNull(digest, "Test value must not be null");
    value = null;
    this.digest = digest;
    resultOfAdd = null;
  }


  /**
   * New instance for a presence check.
   *
   * @param pointer     the path to test
   * @param resultOfAdd Desired result of an add operation
   */
  public Test(JsonExtendedPointer pointer, ResultOfAdd resultOfAdd) {
    super(pointer);
    value = null;
    digest = null;
    this.resultOfAdd = resultOfAdd;
  }


  /**
   * Create a new instance from its JSON representation.
   *
   * @param object representation of the test
   */
  public Test(CJObject object) {
    super(object);
    value = object.optJsonValue("value");
    digest = object.optString("digest");
    String name = object.optString("resultOfAdd");
    if (name != null) {
      resultOfAdd = ResultOfAdd.valueOf(name);
    } else {
      resultOfAdd = null;
    }
    checkConfig();
  }


  @Override
  public <T extends JsonStructure> T apply(T target) {
    if (resultOfAdd != null) {
      ResultOfAdd actual = pointer.testAdd(target);
      if (actual != resultOfAdd) {
        throw new JsonException("Add will " + actual + ", required to " + resultOfAdd + " at " + getPath());
      }
      return target;
    }

    // by value and by digest tests require the value to be present
    JsonValue jsonValue = pointer.getValue(target);
    if (value != null && !value.equals(jsonValue)) {
      throw new IncorrectValueException("Test failed. Value at \"" + getPath() + "\" is not " + value);
    }
    if (digest != null) {
      checkDigest(jsonValue);
    }
    return target;
  }


  private void checkConfig() {
    int c = (((value != null) ? 1 : 0) + ((digest != null) ? 1 : 0) + ((resultOfAdd != null) ? 1 : 0));
    if (c != 1) {
      throw new IllegalArgumentException("Test case must specify exactly one of 'value', 'digest', or 'resultOfAdd'");
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
      throw new IncorrectDigestException("Test failed. Digest for " + getPath() + " is \"" + Base64.getUrlEncoder().encodeToString(actual) + "\".");
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


  /**
   * Get the expected digest of the value at the test path.
   *
   * @return the expected digest
   */
  public String getDigest() {
    return digest;
  }


  @Override
  public Operation getOperation() {
    return Operation.TEST;
  }


  /**
   * Get the expected result of an "add" operation on the test path.
   *
   * @return the expected result
   */
  public ResultOfAdd getResultOfAdd() {
    return resultOfAdd;
  }


  /**
   * Get the value to test against.
   *
   * @return the value
   */
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
  public CJObject toJsonObject() {
    ObjectBuilder builder = new ObjectBuilder()
        .add("op", getOperation().operationName())
        .add("path", getPath());

    if (value != null) {
      builder.add("value", getValue());
    }
    if (digest != null) {
      builder.add("digest", getDigest());
    }
    if (resultOfAdd != null) {
      builder.add("resultOfAdd", resultOfAdd.name());
    }

    return builder.build();
  }

}
