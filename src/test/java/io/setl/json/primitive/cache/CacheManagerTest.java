package io.setl.json.primitive.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 05/02/2020.
 */
public class CacheManagerTest {

  @Test
  public void numberCache() {
    assertNotNull(CacheManager.numberCache());
  }


  @Test
  public void parseCache() {
    assertNotNull(CacheManager.keyCache());
  }


  @Test
  public void setNumberCache() {
    CacheManager.setNumberCache(null);
    assertNotNull(CacheManager.numberCache());
  }


  @Test
  public void setParseCache() {
    CacheManager.setKeyCache(null);
    assertNotNull(CacheManager.keyCache());
  }


  @Test
  public void setStringCache() {
    CacheManager.setStringCache(null);
    assertNotNull(CacheManager.stringCache());
  }


  @Test
  public void setValueCache() {
    CacheManager.setValueCache(null);
    assertNotNull(CacheManager.valueCache());
  }

}
