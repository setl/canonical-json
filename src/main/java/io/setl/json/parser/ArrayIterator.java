package io.setl.json.parser;

import java.util.function.Supplier;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

/**
 * @author Simon Greatrix on 24/01/2020.
 */
class ArrayIterator extends BaseIterator<JsonValue> {

  private final StructureTag myTag;

  private final JsonParser parser;

  private final Supplier<StructureTag> tags;

  private JsonValue next;


  public ArrayIterator(Supplier<StructureTag> tags, JsonParser parser) {
    this.tags = tags;
    this.parser = parser;
    this.myTag = tags.get();
  }


  @Override
  protected boolean checkNext() {
    if (!myTag.equals(tags.get())) {
      return false;
    }
    if (!parser.hasNext()) {
      return false;
    }
    Event event = parser.next();
    if (event == Event.END_ARRAY) {
      return false;
    }
    next = parser.getValue();
    return true;
  }


  @Override
  protected JsonValue fetchNext() {
    JsonValue jsonValue = next;
    next = null;
    return jsonValue;
  }
}
