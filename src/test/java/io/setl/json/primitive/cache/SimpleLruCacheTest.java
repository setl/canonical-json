package io.setl.json.primitive.cache;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Test;

/**
 * @author Simon Greatrix on 05/02/2020.
 */
public class SimpleLruCacheTest {

  @Test
  public void test() {
    ICache<String, String> cache = new SimpleLruCacheFactory().create(CacheType.KEYS,3);
    cache.get("a", Function.identity());
    cache.get("b", Function.identity());
    cache.get("c", Function.identity());
    assertEquals("b", cache.get("b", k -> ""));
    cache.get("d", Function.identity());

    // "a" should have expired
    assertEquals("", cache.get("a", k -> ""));
  }

}