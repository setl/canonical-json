package io.setl.json.parser;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Common iterator functionality.
 *
 * @author Simon Greatrix on 15/01/2020.
 */
abstract class BaseIterator<T> implements Iterator<T> {

  boolean hasNextCalled = false;

  boolean nextExists = false;


  /**
   * Get this iterator as a stream.
   *
   * @return this as a stream
   */
  Stream<T> asStream() {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, 0), false);
  }


  /**
   * Check if a next value exists. This will only be called once prior to each call of fetchNext().
   *
   * @return true if a next value exists.
   */
  protected abstract boolean checkNext();

  /**
   * Fetch the next value. This will only be called after checkNext() has been called.
   *
   * @return the next value
   */
  protected abstract T fetchNext();


  @Override
  public boolean hasNext() {
    if (hasNextCalled) {
      return nextExists;
    }
    hasNextCalled = true;
    nextExists = checkNext();
    return nextExists;
  }


  @Override
  public T next() {
    if (hasNext()) {
      hasNextCalled = false;
      return fetchNext();
    }
    throw new NoSuchElementException();
  }

}
