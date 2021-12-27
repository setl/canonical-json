package io.setl.json.primitive.cache;

import java.util.function.Function;
import javax.annotation.Nonnull;

import io.setl.json.primitive.CJString;

/**
 * A specialised cache for String values. It differs from a regular cache in that it will only cache short Strings. The thinking here is that it is only worth
 * caching keys, enumerations and identifiers.
 *
 * @author Simon Greatrix on 27/12/2021.
 */
public class StringLruCache extends SimpleLruCache<String, CJString> {

  private final int maxLength;


  /**
   * New instance.
   *
   * @param maxSize the maximum number of entries in the cache.
   */
  public StringLruCache(int maxSize) {
    // A 512-bit SHA takes 88 characters when Base-64 encoded. Such an identifier might also have some sort of meta-data. From this we pick a default maximum
    // length of 100 characters.
    this(maxSize, Integer.getInteger(CacheManager.class.getPackageName() + "." + CacheType.STRINGS.getPropertyName() + ".maxLength", 100));
  }


  /**
   * New instance.
   *
   * @param maxSize   the maximum number of entries in the cache.
   * @param maxLength the maximum length of string to cache.
   */
  public StringLruCache(int maxSize, int maxLength) {
    super(maxSize);
    this.maxLength = maxLength;
  }


  @Nonnull
  @Override
  public CJString get(String key, Function<String, CJString> creator) {
    if (key.length() <= maxLength) {
      return super.get(key, creator);
    }
    return creator.apply(key);
  }

}
