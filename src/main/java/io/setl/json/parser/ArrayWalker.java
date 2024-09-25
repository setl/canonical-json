package io.setl.json.parser;

import java.util.NoSuchElementException;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser.Event;

/**
 * Walk through the elements of an array.
 *
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
  protected Event fetchNextImpl() {
    return eventForType(array.get(index));
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
  JsonValue primaryObject() {
    return array;
  }

}
