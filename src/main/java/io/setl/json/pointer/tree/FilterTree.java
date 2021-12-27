package io.setl.json.pointer.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import io.setl.json.pointer.PathElement;

/**
 * A tree used to check if a set of pointers covers all entries of a JSON structure.
 *
 * @author Simon Greatrix on 17/02/2020.
 */
class FilterTree implements Filter {

  /**
   * Invoke the appropriate filter method on a child value.
   *
   * @param f the filter to apply
   * @param v the value to filter
   *
   * @return true if everything matched
   */
  private static boolean descend(Filter f, JsonValue v) {
    if (f == null || v == null) {
      return false;
    }
    switch (v.getValueType()) {
      case OBJECT:
        return f.containsAll((JsonObject) v);
      case ARRAY:
        return f.containsAll((JsonArray) v);
      default:
        return f.allowValue();
    }
  }


  private final Map<String, Filter> descendants = new HashMap<>();


  public void add(PathElement element) {
    String key = element.getKey();
    PathElement child = element.getChild();
    if (child == null) {
      descendants.put(key, FilterAccept.ACCEPT_ALL);
      return;
    }

    Filter tree = descendants.computeIfAbsent(key, k -> new FilterTree());
    tree.add(child);
  }


  @Override
  public boolean allowValue() {
    return false;
  }


  public boolean containsAll(JsonObject object) {
    for (Entry<String, JsonValue> e : object.entrySet()) {
      Filter filter = descendants.get(e.getKey());
      if (!descend(filter, e.getValue())) {
        // something was not matched
        return false;
      }
    }

    // everything matched
    return true;
  }


  public boolean containsAll(JsonArray array) {
    Filter wildcard = descendants.getOrDefault("-", FilterDeny.DENY);
    int index = -1;
    for (JsonValue v : array) {
      index++;

      // test the wildcard first
      boolean f = descend(wildcard, v);

      // next try element specific filter
      if (!f && !descend(descendants.get(Integer.toString(index)), v)) {
        return false;
      }
    }

    // everything matched
    return true;
  }

}
