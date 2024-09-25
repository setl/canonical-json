package io.setl.json.io;

import java.io.IOException;
import java.io.Reader;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParsingException;

/**
 * Input for parsing.
 *
 * @author Simon Greatrix on 10/01/2020.
 */
public class Input {

  private final MutableLocation location = new MutableLocation();

  private final Reader reader;

  private boolean seenEOF = false;

  private int unread = -2;


  /**
   * New instance.
   *
   * @param reader the reader
   */
  public Input(Reader reader) {
    this.reader = reader;
  }


  /**
   * Close the reader.
   *
   * @throws JsonParsingException if an IOException occurs
   */
  public void close() {
    try {
      reader.close();
    } catch (IOException e) {
      throw new JsonParsingException("I/O failure", e, getLocation());
    }
  }


  /**
   * Get the location in the input.
   *
   * @return the location
   */
  public JsonLocation getLocation() {
    return new Location(location);
  }


  /**
   * Read the next character.
   *
   * @return the next character, or -1 on end of stream.
   *
   * @throws JsonParsingException if an IOException occurs
   */
  public int read() {
    int r;
    if (unread != -2) {
      r = unread;
      unread = -2;
      return r;
    }
    if (seenEOF) {
      return -1;
    }
    try {
      r = reader.read();
    } catch (IOException e) {
      throw new JsonParsingException("I/O failure", e, getLocation());
    }
    location.update(r);

    if (r == -1) {
      seenEOF = true;
    }
    return r;
  }


  /**
   * Unread the given value. The next call to read() will return it.
   *
   * @param r the value to unread
   */
  public void unread(int r) {
    if (r != -1) {
      seenEOF = false;
    }
    unread = r;
  }

}
