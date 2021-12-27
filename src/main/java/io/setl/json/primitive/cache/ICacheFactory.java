package io.setl.json.primitive.cache;

/**
 * Interface cache factories should implement.
 *
 * @author Simon Greatrix on 05/02/2020.
 */
public interface ICacheFactory {

  /**
   * Create a new cache of the required type.
   *
   * @param type    the cache type
   * @param maxSize the suggested maximum number of values to hold in the cache
   * @param <K>     the cache's key type
   * @param <V>     the cache's value type
   *
   * @return the new cache
   */
  <K, V> ICache<K, V> create(CacheType type, int maxSize);

}
