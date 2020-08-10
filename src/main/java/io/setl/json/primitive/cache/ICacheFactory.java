package io.setl.json.primitive.cache;

/**
 * @author Simon Greatrix on 05/02/2020.
 */
public interface ICacheFactory {

  <K, V> ICache<K, V> create(int maxSize);

}
