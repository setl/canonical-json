package io.setl.json.parser;

import java.util.EnumSet;
import java.util.function.Supplier;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;

/**
 * Iterate over JSON values as they are parsed.
 *
 * @author Simon Greatrix on 24/01/2020.
 */
class ValueIterator extends BaseIterator<JsonValue> {

  /** Should never see these events in the root context. */
  private static final EnumSet<Event> BAD_EVENTS = EnumSet.of(
      Event.END_ARRAY,
      Event.END_OBJECT,
      Event.KEY_NAME
  );

  private final StructureTag myTag;

  private final JsonParser parser;

  private final Supplier<StructureTag> tags;

  private JsonValue next;


  public ValueIterator(Supplier<StructureTag> tags, JsonParser parser) {
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
    if (BAD_EVENTS.contains(event)) {
      throw new IllegalStateException("Iterator has lost synchronization with the parser. Saw event " + event);
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
