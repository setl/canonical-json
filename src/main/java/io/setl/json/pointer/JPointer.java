package io.setl.json.pointer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import io.setl.json.JArray;
import io.setl.json.JObject;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class JPointer implements JsonExtendedPointer {

  /**
   * Perform the escaping of the '~' and '/' characters as required by the standard.
   *
   * @param key the key to escape
   *
   * @return the escape key (or the original key if no escaping was required)
   */
  public static String escapeKey(String key) {
    if (key.indexOf('~') == -1 && key.indexOf('/') == -1) {
      return key;
    }
    return key.replace("~", "~0").replace("/", "~1");
  }


  protected final String path;

  protected final PathElement root;


  public JPointer(String path, PathElement root) {
    this.path = path;
    this.root = root;
  }


  /**
   * New instance created from a specified PathElement.
   *
   * @param root the root of this pointer's path
   */
  public JPointer(PathElement root) {
    this.root = root;
    StringBuilder builder = new StringBuilder();
    root.buildPath(builder);
    path = builder.toString();
  }


  @Override
  public <T extends JsonStructure> T add(T target, JsonValue value) {
    if (target.getValueType() == ValueType.OBJECT) {
      root.add((JsonObject) target, value);
    } else {
      root.add((JsonArray) target, value);
    }
    return target;
  }


  @Override
  public boolean contains(JsonExtendedPointer other) {
    if (other instanceof EmptyPointer) {
      // Empty cannot be contained within this.
      return false;
    }
    return root.contains(((JPointer) other).root);
  }


  @Override
  public boolean containsValue(JsonStructure target) {
    if (target.getValueType() == ValueType.OBJECT) {
      return root.containsValue((JsonObject) target);
    }
    return root.containsValue((JsonArray) target);
  }


  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends JsonStructure> T copy(@Nonnull T source, @Nullable T target) {
    if (source instanceof JsonObject) {
      return (T) doCopy((JsonObject) source, (JsonObject) target);
    }
    return (T) doCopy((JsonArray) source, (JsonArray) target);
  }


  private JsonObject doCopy(JsonObject source, JsonObject target) {
    if (target == null) {
      target = new JObject();
    }
    root.copy(source, target);
    return target;
  }


  private JsonArray doCopy(JsonArray source, JsonArray target) {
    if (target == null) {
      target = new JArray();
    }
    root.copy(source, target);
    return target;
  }


  @Override
  public String getPath() {
    return path;
  }


  @Override
  public JsonValue getValue(JsonStructure target) {
    if (target.getValueType() == ValueType.OBJECT) {
      return root.getValue((JsonObject) target);
    }
    return root.getValue((JsonArray) target);
  }


  @Override
  public <T extends JsonStructure> T remove(T target) {
    if (target.getValueType() == ValueType.OBJECT) {
      root.remove((JsonObject) target);
    } else {
      root.remove((JsonArray) target);
    }
    return target;
  }


  @Override
  public <T extends JsonStructure> T replace(T target, JsonValue value) {
    if (target.getValueType() == ValueType.OBJECT) {
      root.replace((JsonObject) target, value);
    } else {
      root.replace((JsonArray) target, value);
    }
    return target;
  }

}
