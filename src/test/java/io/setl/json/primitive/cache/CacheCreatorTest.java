package io.setl.json.primitive.cache;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @author Simon Greatrix on 05/02/2020.
 */
public class CacheCreatorTest {

  @Test
  public void numberCache() {
    assertNotNull(CacheCreator.numberCache());
  }


  @Test
  public void parseCache() {
    assertNotNull(CacheCreator.keyCache());
  }


  @Test
  public void setNumberCache() {
    CacheCreator.setNumberCache(null);
    assertNotNull(CacheCreator.numberCache());
  }


  @Test
  public void setParseCache() {
    CacheCreator.setKeyCache(null);
    assertNotNull(CacheCreator.keyCache());
  }


  @Test
  public void setStringCache() {
    CacheCreator.setStringCache(null);
    assertNotNull(CacheCreator.stringCache());
  }


  @Test
  public void setValueCache() {
    CacheCreator.setValueCache(null);
    assertNotNull(CacheCreator.valueCache());
  }
}