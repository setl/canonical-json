package io.setl.json.patch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import io.setl.json.patch.key.ArrayKey;
import io.setl.json.patch.key.Key;
import io.setl.json.patch.key.ObjectKey;
import io.setl.json.primitive.PString;

/**
 * An entry in an array, or some descendant of an array.
 */
public class Entry {

  /**
   * A pseudo-value used to indicate where an new array starts in a list of entries.
   */
  public static final JsonValue START_ARRAY = new JsonValue() {
    @Override
    public boolean equals(Object obj) {
      return this == obj;
    }


    @Override
    public ValueType getValueType() {
      return ValueType.ARRAY;
    }


    @Override
    public int hashCode() {
      return 1528950780; // hash of "START_ARRAY"
    }


    @Override
    public String toString() {
      return "START_ARRAY";
    }
  };

  /**
   * A pseudo-value used to indicate where an new object starts in a list of entries.
   */
  public static final JsonValue START_OBJECT = new JsonValue() {
    @Override
    public boolean equals(Object obj) {
      return this == obj;
    }


    @Override
    public ValueType getValueType() {
      return ValueType.OBJECT;
    }


    @Override
    public int hashCode() {
      return 538630620; // hash of "START_OBJECT"
    }


    @Override
    public String toString() {
      return "START_OBJECT";
    }

  };


  /**
   * Append a suitable entry to the list, including a structure start marker if needed.
   *
   * @param list  the list
   * @param key   the key
   * @param value the value
   */
  private static void append(ArrayList<Entry> list, Key key, JsonValue value) {
    switch (value.getValueType()) {
      case OBJECT:
        list.add(new Entry(key, START_OBJECT));
        convert(list, key, (JsonObject) value);
        break;
      case ARRAY:
        list.add(new Entry(key, START_ARRAY));
        convert(list, key, (JsonArray) value);
        break;
      default:
        list.add(new Entry(key, value));
        break;
    }
  }


  /**
   * Convert the elements of an array into a list of entries.
   *
   * @param list  the list
   * @param key   the key to the array
   * @param array the array
   */
  private static void convert(ArrayList<Entry> list, Key key, JsonArray array) {
    int s = array.size();
    for (int i = 0; i < s; i++) {
      JsonValue jv = array.get(i);
      Key child = new ArrayKey(key, i);
      append(list, child, jv);
    }
  }


  /**
   * Convert the members of an object into a list of entries.
   *
   * @param list   the list
   * @param key    the key to the object
   * @param object the object
   */
  private static void convert(ArrayList<Entry> list, Key key, JsonObject object) {
    for (Map.Entry<String, JsonValue> e : object.entrySet()) {
      Key child = new ObjectKey(key, e.getKey());
      append(list, child, e.getValue());
    }
  }


  /**
   * Convert a JsonArray into a list of entries.
   *
   * @param array the array
   *
   * @return the list
   */
  public static List<Entry> convert(JsonArray array) {
    ArrayList<Entry> list = new ArrayList<>(array.size());
    convert(list, null, array);
    return list;
  }


  private final Key key;
  private final JsonValue value;


  public Entry(Key key, JsonValue value) {
    this.key = key;
    this.value = value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Entry)) {
      return false;
    }

    Entry entry = (Entry) o;

    return key.equals(entry.key) && value.equals(entry.value);
  }


  public Key getKey() {
    return key;
  }


  public JsonValue getValue() {
    return value;
  }


  @Override
  public int hashCode() {
    return key.hashCode() * 31 + value.hashCode();
  }


  @Override
  public String toString() {
    return PString.format(key.toString()) + " : " + value.toString();
  }

}
