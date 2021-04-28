package io.setl.json.primitive.cache;

/**
 * Interface cache factories should implement.
 *
 * @author Simon Greatrix on 05/02/2020.
 */
public interface ICacheFactory {

  <K, V> ICache<K, V> create(int maxSize);

}
