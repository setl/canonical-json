package io.setl.json.primitive.cache;

import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * An interface that caches should implement.
 *
 * @author Simon Greatrix on 05/02/2020.
 */
public interface ICache<K, V> {

  @Nonnull
  V get(K key, Function<K, V> creator);

}
