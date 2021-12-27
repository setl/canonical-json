package io.setl.json.primitive.cache;

/**
 * A factory that creates "no cache" instances.
 *
 * @author Simon Greatrix on 05/02/2020.
 */
public class NoCacheFactory implements ICacheFactory {

  @Override
  public <K, V> ICache<K, V> create(CacheType type, int maxSize) {
    return new NoCache<>();
  }

}
