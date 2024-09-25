package io.setl.json.primitive.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * A very simple LRU cache based on LinkedHashMap. The cache can only be accessed by one thread at a time.
 *
 * @author Simon Greatrix on 05/02/2020.
 */
public class SimpleLruCache<K, V> implements ICache<K, V> {

  private static class Cache<K, V> extends LinkedHashMap<K, V> {

    private final int maxSize;


    public Cache(int maxSize) {
      this.maxSize = maxSize;
    }


    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
      return size() > maxSize;
    }

  }



  private final Map<K, V> myCache;


  /**
   * New instance.
   *
   * @param maxSize number of items to hold in the cache
   */
  public SimpleLruCache(int maxSize) {
    myCache = new Cache<>(maxSize);
  }


  @Nonnull
  @Override
  public V get(K key, Function<K, V> creator) {
    synchronized (myCache) {
      return myCache.computeIfAbsent(key, creator);
    }
  }

}
