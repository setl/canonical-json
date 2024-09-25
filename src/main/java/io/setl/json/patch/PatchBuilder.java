package io.setl.json.patch;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import jakarta.json.JsonArray;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonValue;

import io.setl.json.patch.ops.Add;
import io.setl.json.patch.ops.Copy;
import io.setl.json.patch.ops.Move;
import io.setl.json.patch.ops.Remove;
import io.setl.json.patch.ops.Replace;
import io.setl.json.patch.ops.Test;
import io.setl.json.primitive.CJFalse;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.CJTrue;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * Builder for JSON patches.
 *
 * @author Simon Greatrix on 28/01/2020.
 */
public class PatchBuilder implements JsonPatchBuilder, Iterable<PatchOperation> {

  private final List<PatchOperation> operationList = new ArrayList<>();


  /** New instance. */
  public PatchBuilder() {
    // do nothing
  }


  /**
   * New instance.
   *
   * @param operations the JSON representation of the patch operations
   */
  public PatchBuilder(JsonArray operations) {
    operationList.addAll(PatchOperation.convert(operations));
  }


  @Override
  public JsonPatchBuilder add(String path, JsonValue value) {
    operationList.add(new Add(path, value));
    return this;
  }


  @Override
  public JsonPatchBuilder add(String path, String value) {
    operationList.add(new Add(path, CJString.create(value)));
    return this;
  }


  @Override
  public JsonPatchBuilder add(String path, int value) {
    operationList.add(new Add(path, CJNumber.create(value)));
    return this;
  }


  @Override
  public JsonPatchBuilder add(String path, boolean value) {
    operationList.add(new Add(path, value ? CJTrue.TRUE : CJFalse.FALSE));
    return this;
  }


  /**
   * Add an operation to this patch.
   *
   * @param index     where to add the operation
   * @param operation the operation to add
   */
  public void addOperation(int index, PatchOperation operation) {
    operationList.add(index, operation);
  }


  @Override
  public JsonPatch build() {
    return new Patch(operationList);
  }


  @Override
  public JsonPatchBuilder copy(String path, String from) {
    operationList.add(new Copy(path, from));
    return this;
  }


  /**
   * Create a "test" operation that validates against the SHA-512/256 digest of the canonical representation of the value.
   *
   * @param path  the path to test
   * @param value the expected value
   *
   * @return the builder
   */
  public JsonPatchBuilder digest(String path, JsonValue value) {
    return digest(path, Test.DEFAULT_DIGEST, value);
  }


  /**
   * Create a "test" operation that validates against the digest of the canonical representation of the value.
   *
   * @param path      the path to test
   * @param algorithm the digest algorithm
   * @param value     the expected value
   *
   * @return the builder
   */
  public JsonPatchBuilder digest(String path, String algorithm, JsonValue value) {
    byte[] hash = Test.digest(algorithm, value);
    operationList.add(new Test(path, null, algorithm + "=" + Base64.getUrlEncoder().encodeToString(hash), null));
    return this;
  }


  /**
   * Get the operation at the specified index.
   *
   * @param index the index
   *
   * @return the operation
   */
  public PatchOperation getOperation(int index) {
    return operationList.get(index);
  }


  @Nonnull
  @Override
  public Iterator<PatchOperation> iterator() {
    return operationList.iterator();
  }


  @Override
  public JsonPatchBuilder move(String path, String from) {
    operationList.add(new Move(path, from));
    return this;
  }


  @Override
  public JsonPatchBuilder remove(String path) {
    operationList.add(new Remove(path));
    return this;
  }


  /**
   * Remove the operation at the specified index.
   *
   * @param index the index
   */
  public void removeOperation(int index) {
    operationList.remove(index);
  }


  @Override
  public JsonPatchBuilder replace(String path, JsonValue value) {
    operationList.add(new Replace(path, value));
    return this;
  }


  @Override
  public JsonPatchBuilder replace(String path, String value) {
    operationList.add(new Replace(path, CJString.create(value)));
    return this;
  }


  @Override
  public JsonPatchBuilder replace(String path, int value) {
    operationList.add(new Replace(path, CJNumber.create(value)));
    return this;
  }


  @Override
  public JsonPatchBuilder replace(String path, boolean value) {
    operationList.add(new Replace(path, value ? CJTrue.TRUE : CJFalse.FALSE));
    return this;
  }


  /**
   * Set the operation at the specified index.
   *
   * @param index     the index
   * @param operation the operation
   */
  public void setOperation(int index, PatchOperation operation) {
    operationList.set(index, operation);
  }


  /**
   * Get the number of operations in this patch.
   *
   * @return the number of operations
   */
  public int size() {
    return operationList.size();
  }


  @Override
  public JsonPatchBuilder test(String path, JsonValue value) {
    operationList.add(new Test(path, value, null, null));
    return this;
  }


  @Override
  public JsonPatchBuilder test(String path, String value) {
    operationList.add(new Test(path, CJString.create(value), null, null));
    return this;
  }


  @Override
  public JsonPatchBuilder test(String path, int value) {
    operationList.add(new Test(path, CJNumber.create(value), null, null));
    return this;
  }


  @Override
  public JsonPatchBuilder test(String path, boolean value) {
    operationList.add(new Test(path, value ? CJTrue.TRUE : CJFalse.FALSE, null, null));
    return this;
  }

}
