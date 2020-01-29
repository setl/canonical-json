package io.setl.json.merge;

import io.setl.json.JObject;
import io.setl.json.JType;
import io.setl.json.Primitive;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.json.JsonMergePatch;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

/**
 * @author Simon Greatrix on 28/01/2020.
 */
public class JMerge implements JsonMergePatch {

  static JsonObject stripNulls(JsonObject jsonObject) {
    JObject result = new JObject(jsonObject);
    Iterator<JsonValue> iterator = result.values().iterator();
    while( iterator.hasNext() ) {
      JsonValue jv = iterator.next();
      if( jv.getValueType()==ValueType.NULL ) {
        iterator.remove();
      }
      iterator.
    }

  }

  static Primitive mergePatch(Primitive target, Primitive patch) {
    if (patch.getType() != JType.OBJECT) {
      return patch.copy();
    }
    JObject patchObject = (JObject) patch;

    JObject output;
    if( target==null || target.getType() != JType.OBJECT ) {
      output = new JObject();
    } else {
      output = (JObject) target.copy();
    }

    for(Entry<String,JsonValue> entry : patchObject.entrySet()) {
      if( entry.getValue().getValueType()==ValueType.NULL ) {
        output.remove(entry.getKey());
      } else {
        output.put(entry.getKey(), mergePatch())
      }
    }
  }

  private final Primitive patch;


  JMerge(JsonValue patch) {
    this.patch = Primitive.cast(patch).copy();
  }


  /**
   * Implements the MergePatch function from RFC-7396.
   *
   * @param target the target to apply this patch to.
   *
   * @return the new value
   */
  @Override
  public JsonValue apply(JsonValue target) {
    if (patch.getValueType() != ValueType.OBJECT) {
      return patch.copy();
    }
    JObject patchObject = (JObject) patch;

    Primitive output;
    if( target.getValueType() != ValueType.OBJECT ) {
      output = new JObject();
    } else {
      output = Primitive.cast(target).copy();
    }

    for(Entry<String,JsonValue> entry : patchObject.entrySet()) {
      if( entry.getValue().getValueType()==ValueType.NULL ) {
        p
      }
    }
    // TODO : Implement me! simongreatrix 28/01/2020
    return output;
  }


  @Override
  public JsonValue toJsonValue() {
    return patch;
  }
}
