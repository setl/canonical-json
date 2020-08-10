package io.setl.json.primitive.cache;

import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * A cache that does no caching.
 *
 * @author Simon Greatrix on 05/02/2020.
 */
public class NoCache<K, V> implements ICache<K, V> {

  @Nonnull
  @Override
  public V get(K key, Function<K, V> creator) {
    return creator.apply(key);
  }

}
