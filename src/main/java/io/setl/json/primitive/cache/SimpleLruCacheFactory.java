package io.setl.json.primitive.cache;

/**
 * A factory that creates simple LRU caches.
 *
 * @author Simon Greatrix on 05/02/2020.
 */
public class SimpleLruCacheFactory implements ICacheFactory {

  /** New instance. */
  public SimpleLruCacheFactory() {
    // do nothing
  }


  @Override
  public <K, V> ICache<K, V> create(CacheType type, int maxSize) {
    if (type == CacheType.STRINGS) {
      @SuppressWarnings("unchecked")
      ICache<K, V> cache = (ICache<K, V>) new StringLruCache(maxSize);
      return cache;
    }

    return new SimpleLruCache<>(maxSize);
  }

}
