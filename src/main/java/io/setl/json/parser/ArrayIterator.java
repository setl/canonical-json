package io.setl.json.parser;

import java.util.function.Supplier;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;

/**
 * Iterator over the parsed elements of a JSON array.
 *
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
    myTag = tags.get();
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
