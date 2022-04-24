package io.setl.json.patch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import org.junit.jupiter.api.Test;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.Canonical;
import io.setl.json.primitive.CJFalse;
import io.setl.json.primitive.CJNull;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.CJTrue;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * @author Simon Greatrix on 10/02/2020.
 */
public class PatchFactoryTest {

  private static final int MAX_DEPTH = 5;

  private static Random random = new Random(0x7e57ab1e);



  static class PatchSet<T extends JsonStructure> {

    T after;

    T before;

    JsonPatch patch;

  }


  private static JsonArray createArray(int depth) {
    JsonArray array = new CJArray();
    int size = random.nextInt(16);
    for (int i = 0; i < size; i++) {
      array.add(createValue(depth));
    }
    return array;
  }


  private static JsonObject createObject(int depth) {
    JsonObject object = new CJObject();
    int size = random.nextInt(16);
    for (int i = 0; i < size; i++) {
      String key = Character.toString('a' + i);
      JsonValue value = createValue(depth);
      object.put(key, value);
    }
    return object;
  }


  private static JsonValue createValue(int depth) {
    JsonValue value = null;
    while (value == null) {
      int type = random.nextInt(8);
      switch (type) {
        case 0:
          if (depth < MAX_DEPTH) {
            value = createObject(depth + 1);
          }
          break;
        case 1:
          if (depth < MAX_DEPTH) {
            value = createArray(depth + 1);
          }
          break;
        case 2:
          value = CJNull.NULL;
          break;
        case 3:
        case 4:
          value = CJString.create(Integer.toString(random.nextInt(), 36));
          break;
        case 5:
        case 6:
          value = CJNumber.create(random.nextInt());
          break;
        default:
          value = random.nextBoolean() ? CJTrue.TRUE : CJFalse.FALSE;
          break;
      }
    }
    return value;
  }


  static PatchSet<JsonObject> makeObjectSet() {
    PatchSet<JsonObject> set = new PatchSet<>();
    set.before = createObject(0);
    set.after = Canonical.cast(set.before).copy().asJsonObject();
    for (int j = 0; j < 10; j++) {
      mutateObject(0, set.after);
    }
    set.patch = PatchFactory.create(set.before, set.after);
    return set;
  }


  private static void mutateArray(int depth, JsonArray array) {
    if (array.isEmpty()) {
      array.add(createValue(depth + 1));
    }
    int op = random.nextInt(4);
    switch (op) {
      case 0: // remove
        array.remove(random.nextInt(array.size()));
        break;
      case 1: // add
        array.add(random.nextInt(array.size() + 1), createValue(depth + 1));
        break;
      default: {
        // mutate
        int i = random.nextInt(array.size());
        JsonValue current = array.get(i);
        if (current instanceof JsonArray) {
          mutateArray(depth + 1, (JsonArray) current);
        } else if (current instanceof JsonObject) {
          mutateObject(depth + 1, (JsonObject) current);
        } else {
          array.set(i, createValue(depth + 1));
        }
      }
    }
  }


  private static void mutateObject(int depth, JsonObject object) {
    int op = random.nextInt(4);
    if (object.isEmpty()) {
      String k = Integer.toString(random.nextInt(512), 36);
      object.put(k, createValue(depth + 1));
      return;
    }

    List<String> keys = new ArrayList<>(object.keySet());
    String k = keys.get(random.nextInt(keys.size()));
    switch (op) {
      case 0: // remove
        object.remove(k);
        break;
      case 1: // add
        k = Integer.toString(random.nextInt(512), 36);
        object.put(k, createValue(depth + 1));
        break;
      default: {
        // mutate
        JsonValue current = object.get(k);
        if (current instanceof JsonArray) {
          mutateArray(depth + 1, (JsonArray) current);
        } else if (current instanceof JsonObject) {
          mutateObject(depth + 1, (JsonObject) current);
        } else {
          object.put(k, createValue(depth + 1));
        }
      }
    }

  }


  @Test
  public void testDefaultsOnArrays() {
    for (int i = 0; i < 1000; i++) {
      JsonArray source = createArray(0);
      JsonArray target = Canonical.cast(source).copy().asJsonArray();
      for (int j = 0; j < 10; j++) {
        mutateArray(0, target);
      }

      JsonPatch patch = PatchFactory.create(source, target);

      JsonArray applied = patch.apply(source);
      assertEquals(target, applied);
    }
  }


  @Test
  public void testDefaultsOnObjects() {
    for (int i = 0; i < 1000; i++) {
      JsonObject source = createObject(0);
      JsonObject target = Canonical.cast(source).copy().asJsonObject();
      for (int j = 0; j < 10; j++) {
        mutateObject(0, target);
      }

      JsonPatch patch = PatchFactory.create(source, target);

      JsonObject applied = patch.apply(source);
      assertEquals(target, applied);
    }
  }


  @Test
  public void testWithTestsAndDigests() {
    for (int i = 0; i < 1000; i++) {
      JsonArray source = createArray(0);
      JsonArray target = Canonical.cast(source).copy().asJsonArray();
      for (int j = 0; j < 10; j++) {
        mutateArray(0, target);
      }

      JsonPatch patch = PatchFactory.create(source, target, EnumSet.of(DiffFeatures.EMIT_DIGEST, DiffFeatures.EMIT_TESTS));

      JsonArray applied = patch.apply(source);
      assertEquals(target, applied);
    }
  }

}
