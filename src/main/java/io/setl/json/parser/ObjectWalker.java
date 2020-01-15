package io.setl.json.parser;

import io.setl.json.primitive.PNull;
import io.setl.json.primitive.PString;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
class ObjectWalker extends WalkingParser {

  final JsonObject object;

  private final String[] keys;

  private boolean isKey = false;


  ObjectWalker(WalkingParser delegate, JsonObject object) {
    super(delegate, object.size(),Event.END_OBJECT);
    this.object = object;
    keys = object.keySet().toArray(new String[0]);
  }


  @Override
  protected boolean checkNextImpl() {
    if (isKey) {
      isKey = false;
      return true;
    }

    index++;
    isKey = true;
    return index < size;
  }


  @Override
  public void close() {
    index = size;
    isKey = true;
    super.close();
  }


  @Override
  protected Event fetchNextImpl() {
    if (isKey) {
      return Event.KEY_NAME;
    }
    return eventForType(object.get(keys[index]));
  }


  @Override
  public Stream<JsonValue> getArrayStream() {
    throw new IllegalStateException("Not in a JSON Array");
  }


  @Override
  public Stream<Entry<String, JsonValue>> getObjectStream() {
    if (index > 0 || isKey) {
      throw new IllegalStateException("Not at start of object, but at " + (index < size ? ("key \"" + keys[index] + "\"") : "end of object"));
    }
    BaseIterator<Entry<String, JsonValue>> iterator = new BaseIterator<>() {
      @Override
      protected boolean checkNext() {
        index++;
        isKey = true;
        return index < size;
      }


      @Override
      protected Entry<String, JsonValue> fetchNext() {
        if (index >= size) {
          throw new NoSuchElementException();
        }
        String k = keys[index];
        JsonValue jv = object.get(k);
        return new SimpleImmutableEntry<>(k, jv);
      }
    };
    return iterator.asStream();
  }


  @Override
  public JsonValue getValue() {
    if (index < 0) {
      throw new IllegalStateException("Next has not been called");
    }
    if (index >= size) {
      throw new NoSuchElementException();
    }
    if (isKey) {
      return new PString(keys[index]);
    }
    JsonValue jv = object.get(keys[index]);
    return (jv != null) ? jv : PNull.NULL;
  }


  @Override
  public void skipArray() {
    // not in array, do nothing
  }


  @Override
  public void skipObject() {
    close();
  }

}
