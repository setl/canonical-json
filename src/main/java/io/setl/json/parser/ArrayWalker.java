package io.setl.json.parser;

import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import javax.json.JsonArray;
import javax.json.JsonValue;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
class ArrayWalker extends WalkingParser {

  private final JsonArray array;


  ArrayWalker(WalkingParser parent, JsonArray array) {
    super(parent, array.size(), Event.END_ARRAY);
    this.array = array;
  }


  @Override
  protected boolean checkNextImpl() {
    index++;
    return index < size;
  }


  @Override
  public void close() {
    index = size;
    super.close();
  }


  @Override
  protected Event fetchNextImpl() {
    return eventForType(array.get(index));
  }


  @Override
  public Stream<JsonValue> getArrayStream() {
    if (index > 0) {
      throw new IllegalStateException("Not at start of array, but at " + index);
    }
    BaseIterator<JsonValue> iterator = new BaseIterator<>() {
      @Override
      protected boolean checkNext() {
        return ArrayWalker.this.checkNext();
      }


      @Override
      protected JsonValue fetchNext() {
        if (index >= size) {
          throw new NoSuchElementException();
        }
        return array.get(index);
      }
    };
    return iterator.asStream();
  }


  @Override
  public Stream<Entry<String, JsonValue>> getObjectStream() {
    throw new IllegalStateException("Not in a JSON Object");
  }


  @Override
  public JsonValue getValue() {
    if (index < 0) {
      throw new IllegalStateException("Next has not been called");
    }
    if (index >= size) {
      throw new NoSuchElementException();
    }
    return array.get(index);
  }


  @Override
  public void skipArray() {
    close();
  }


  @Override
  public void skipObject() {
    // not in an object, so do nothing
  }
}
