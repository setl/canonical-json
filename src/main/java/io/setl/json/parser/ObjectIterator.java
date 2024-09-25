package io.setl.json.parser;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.function.Supplier;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;

/**
 * Iterator over a JSON Object.
 *
 * @author Simon Greatrix on 24/01/2020.
 */
class ObjectIterator extends BaseIterator<Entry<String, JsonValue>> {

  /** Events that are not allowed after a KEY_NAME. */
  private static final EnumSet<Event> BAD_EVENTS = EnumSet.of(
      Event.KEY_NAME,
      Event.END_ARRAY,
      Event.END_OBJECT
  );

  private final StructureTag myTag;

  private final JsonParser parser;

  private final Supplier<StructureTag> tags;

  private String keyName;

  private JsonValue next;


  public ObjectIterator(Supplier<StructureTag> tags, JsonParser parser) {
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
    if (event == Event.END_OBJECT) {
      return false;
    }
    if (event != Event.KEY_NAME) {
      throw new IllegalStateException("Iterator has lost synchronization with parser");
    }
    keyName = parser.getString();
    if (!parser.hasNext()) {
      throw new IllegalStateException("Parser had no value in object after key name");
    }
    event = parser.next();
    if (BAD_EVENTS.contains(event)) {
      throw new IllegalStateException("Parser returned " + event + " after key-name");
    }
    next = parser.getValue();
    return true;
  }


  @Override
  protected Entry<String, JsonValue> fetchNext() {
    String name = keyName;
    JsonValue jsonValue = next;
    next = null;
    keyName = null;
    return new SimpleImmutableEntry<>(name, jsonValue);
  }

}
