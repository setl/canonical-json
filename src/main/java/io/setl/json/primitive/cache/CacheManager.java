package io.setl.json.primitive.cache;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import io.setl.json.primitive.CJString;
import io.setl.json.primitive.numbers.CJNumber;

/**
 * Caching of common immutable values. This limits the number of objects created as similar JSON documents are parsed as shared keys and values are reused.
 *
 * @author Simon Greatrix on 05/02/2020.
 */
public class CacheManager {

  private static ICache<String, String> myKeyCache;

  private static ICache<String, CJNumber> myNumberCache;

  private static ICache<String, CJString> myStringCache;

  private static ICache<Number, CJNumber> myValueCache;


  private static <K, V> ICache<K, V> createCache(String name) {
    int maxSize = Integer.getInteger(CacheManager.class.getPackageName() + "." + name + ".maxSize", 1_000);
    String cacheFactory = System.getProperty(
        CacheManager.class.getPackageName() + "." + name + ".factory",
        SimpleLruCacheFactory.class.getName()
    );

    if (maxSize <= 0) {
      return new NoCache<>();
    }

    ICacheFactory factory = new SimpleLruCacheFactory();
    try {
      Class<?> cl = Class.forName(cacheFactory);
      Class<? extends ICacheFactory> cl2 = cl.asSubclass(ICacheFactory.class);
      Constructor<? extends ICacheFactory> constructor = cl2.getConstructor();
      factory = constructor.newInstance();
    } catch (ClassNotFoundException | ClassCastException | NoSuchMethodException
        | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      Logger logger = System.getLogger(CacheManager.class.getName());
      logger.log(Level.ERROR, "Cannot create a cache factory from " + cacheFactory, e);
    }
    return factory.create(maxSize);
  }


  /**
   * Cache of object keys to their primary representation. This prevents the creation of duplicate strings for fields.
   *
   * @return the cache
   */
  public static ICache<String, String> keyCache() {
    return myKeyCache;
  }


  /**
   * Cache of JSON input to the corresponding numeric canonical.
   *
   * @return the cache.
   */
  public static ICache<String, CJNumber> numberCache() {
    return myNumberCache;
  }


  /**
   * Set the cache that maps object keys to a fixed representation.
   *
   * @param newCache the new cache (or null for no caching)
   */
  public static void setKeyCache(ICache<String, String> newCache) {
    myKeyCache = Objects.requireNonNullElseGet(newCache, NoCache::new);
  }


  /**
   * Set the cache that maps the textual representation of numerical values to a fixed representation.
   *
   * @param newNumberCache the new cache (or null for no caching)
   */
  public static void setNumberCache(ICache<String, CJNumber> newNumberCache) {
    myNumberCache = Objects.requireNonNullElseGet(newNumberCache, NoCache::new);
  }


  /**
   * Set the cache that maps string values to a fixed representation.
   *
   * @param newCache the new cache (or null for no caching)
   */
  public static void setStringCache(ICache<String, CJString> newCache) {
    myStringCache = Objects.requireNonNullElseGet(newCache, NoCache::new);
  }


  /**
   * Set the cache that maps numeric values to a fixed representation.
   *
   * @param newCache the new cache (or null for no caching)
   */
  public static void setValueCache(ICache<Number, CJNumber> newCache) {
    myValueCache = Objects.requireNonNullElseGet(newCache, NoCache::new);
  }


  /**
   * Cache of JSON input to the corresponding text canonical.
   *
   * @return the cache.
   */
  public static ICache<String, CJString> stringCache() {
    return myStringCache;
  }


  /**
   * Cache of numbers to their encapsulated JSON representation. A representation is almost always a PNumber, but it could be a PString for a non-finite value.
   *
   * @return the cache.
   */
  public static ICache<Number, CJNumber> valueCache() {
    return myValueCache;
  }


  static {
    myNumberCache = createCache("numbers");
    myKeyCache = createCache("keys");
    myStringCache = createCache("strings");
    myValueCache = createCache("values");
  }
}
