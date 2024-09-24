package io.setl.json.patch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import org.apache.commons.collections4.ListUtils;

import io.setl.json.patch.key.ArrayKey;
import io.setl.json.patch.key.Key;
import io.setl.json.patch.key.ObjectKey;

/**
 * Factory for creating patches using a Diff algorithm.
 */
public final class PatchFactory {

  /**
   * Helper class to improve the speed of the comparison of items in arrays.
   */
  static class Item {

    final int hashCode;

    final JsonValue jsonValue;


    Item(JsonValue jsonValue) {
      this.jsonValue = jsonValue;
      hashCode = jsonValue.hashCode();
    }


    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (!(o instanceof Item)) {
        return false;
      }
      Item that = (Item) o;
      return hashCode == that.hashCode && jsonValue.equals(that.jsonValue);
    }


    @Override
    public int hashCode() {
      return hashCode;
    }

  }


  /**
   * Create a JSON Patch that transforms the source into the target.
   *
   * @param source the source JSON
   * @param target the target JSON
   *
   * @return the patch
   */
  public static JsonPatch create(JsonValue source, JsonValue target) {
    return create(source, target, Collections.emptySet());
  }


  /**
   * Create a patch that transforms the source into the target.
   *
   * @param source   the source JSON
   * @param target   the target JSON
   * @param features the features used in creating the patch.
   *
   * @return the patch
   */
  public static JsonPatch create(JsonValue source, JsonValue target, Set<DiffFeatures> features) {
    PatchFactory diff = new PatchFactory(features);
    diff.generateDiffs(source, target);
    return diff.patchBuilder.build();
  }


  /** The flags affecting this patch's creation. */
  private final EnumSet<DiffFeatures> features;

  /** The patch operations that make up the derived patch. */
  private final PatchBuilder patchBuilder = new PatchBuilder();


  private PatchFactory(Set<DiffFeatures> features) {
    this.features = (features == null || features.isEmpty()) ? EnumSet.noneOf(DiffFeatures.class) : EnumSet.copyOf(features);
  }


  private int addRemaining(Key path, JsonArray target, int pos, int targetIdx, int targetSize) {
    while (targetIdx < targetSize) {
      JsonValue jsonValue = target.get(targetIdx);
      String itemKey = new ArrayKey(path, pos).toString();
      patchBuilder.add(itemKey, jsonValue);
      pos++;
      targetIdx++;
    }
    return pos;
  }


  @SuppressWarnings({"java:S3776", "JavaNCSS", "CyclomaticComplexity"}) // Ignore cognitive complexity of LCS algorithm.
  private void compareArray(Key path, JsonArray source, JsonArray target) {
    List<Item> sourceItems = new ArrayList<>(source.size());
    for (JsonValue jsonValue : source) {
      sourceItems.add(new Item(jsonValue));
    }
    List<Item> targetItems = new ArrayList<>(target.size());
    for (JsonValue jsonValue : target) {
      targetItems.add(new Item(jsonValue));
    }
    List<Item> lcs = ListUtils.longestCommonSubsequence(sourceItems, targetItems);

    int srcIdx = 0;
    int targetIdx = 0;
    int lcsIdx = 0;
    int srcSize = source.size();
    int targetSize = target.size();
    int lcsSize = lcs.size();

    int pos = 0;
    while (lcsIdx < lcsSize) {
      Item lcsNode = lcs.get(lcsIdx);
      Item srcNode = sourceItems.get(srcIdx);
      Item targetNode = targetItems.get(targetIdx);

      if (lcsNode.equals(srcNode) && lcsNode.equals(targetNode)) {
        // These nodes are part of the LCS, simply step forward
        srcIdx++;
        targetIdx++;
        lcsIdx++;
        pos++;
      } else {
        if (lcsNode.equals(srcNode)) {
          // Source node is part of the LCS, but not target node is not, so this is an addition of the target node.
          String itemKey = new ArrayKey(path, pos).toString();
          patchBuilder.add(itemKey, targetNode.jsonValue);
          pos++;
          targetIdx++;
        } else if (lcsNode.equals(targetNode)) {
          // Target node is part of LCS, but source node is not, so this is a removal of the source node.
          String itemKey = new ArrayKey(path, pos).toString();
          if (features.contains(DiffFeatures.EMIT_TESTS)) {
            patchBuilder.test(itemKey, srcNode.jsonValue);
          }
          patchBuilder.remove(itemKey);
          srcIdx++;
        } else {
          Key itemKey = new ArrayKey(path, pos);
          //both are unequal to lcs node
          generateDiffs(itemKey, srcNode.jsonValue, targetNode.jsonValue);
          srcIdx++;
          targetIdx++;
          pos++;
        }
      }
    }

    while ((srcIdx < srcSize) && (targetIdx < targetSize)) {
      JsonValue srcNode = source.get(srcIdx);
      JsonValue targetNode = target.get(targetIdx);
      generateDiffs(new ArrayKey(path, pos), srcNode, targetNode);
      srcIdx++;
      targetIdx++;
      pos++;
    }
    pos = addRemaining(path, target, pos, targetIdx, targetSize);
    removeRemaining(path, pos, srcIdx, srcSize, source);
  }


  private void compareObjects(Key path, JsonObject source, JsonObject target) {
    TreeSet<String> allNames = new TreeSet<>(source.keySet());
    allNames.addAll(target.keySet());
    for (String name : allNames) {
      Key child = new ObjectKey(path, name);
      if (source.containsKey(name)) {
        if (target.containsKey(name)) {
          // in both source and target, so generate diffs
          generateDiffs(child, source.get(name), target.get(name));
        } else {
          // only in source, so remove
          String childPath = child.toString();
          if (features.contains(DiffFeatures.EMIT_TESTS)) {
            patchBuilder.test(childPath, source.get(name));
          }
          patchBuilder.remove(childPath);
        }
      } else {
        // Not in source so must be in target. Hence, this is an add
        patchBuilder.add(child.toString(), target.get(name));
      }
    }
  }


  private void generateDiffs(JsonValue source, JsonValue target) {
    if (features.contains(DiffFeatures.EMIT_DIGEST)) {
      patchBuilder.digest("", source);
    }

    generateDiffs(null, source, target);

    if (features.contains(DiffFeatures.EMIT_DIGEST)) {
      patchBuilder.digest("", target);
    }
  }


  private void generateDiffs(Key path, JsonValue source, JsonValue target) {
    if (source.equals(target)) {
      // nothing to do
      return;
    }

    ValueType sourceType = source.getValueType();
    ValueType targetType = target.getValueType();

    if (sourceType == ValueType.ARRAY && targetType == ValueType.ARRAY) {
      //both are arrays
      compareArray(path, (JsonArray) source, (JsonArray) target);
    } else if (sourceType == ValueType.OBJECT && targetType == ValueType.OBJECT) {
      //both are json
      compareObjects(path, (JsonObject) source, (JsonObject) target);
    } else {
      //can be replaced
      if (features.contains(DiffFeatures.EMIT_TESTS)) {
        patchBuilder.test(path.toString(), source);
      }
      patchBuilder.replace(path.toString(), target);
    }
  }


  private void removeRemaining(Key path, int pos, int srcIdx, int srcSize, JsonArray source) {
    String itemKey = new ArrayKey(path, pos).toString();
    while (srcIdx < srcSize) {
      if (features.contains(DiffFeatures.EMIT_TESTS)) {
        patchBuilder.test(itemKey, source.get(srcIdx));
      }
      patchBuilder.remove(itemKey);
      srcIdx++;
    }
  }

}
