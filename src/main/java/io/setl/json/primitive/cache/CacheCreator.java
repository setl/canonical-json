package io.setl.json.primitive.cache;

import io.setl.json.Primitive;
import io.setl.json.primitive.PString;
import io.setl.json.primitive.numbers.PNumber;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Simon Greatrix on 05/02/2020.
 */
public class CacheCreator {

  private static ICache<String, String> myKeyCache;

  private static ICache<String, PNumber> myNumberCache;

  private static ICache<String, PString> myStringCache;

  private static ICache<Number, PNumber> myValueCache;


  private static <K, V> ICache<K, V> createCache(String name) {
    int maxSize = Integer.getInteger(CacheCreator.class.getPackageName() + "." + name + ".maxSize", 1_000);
    String cacheFactory = System.getProperty(
        CacheCreator.class.getPackageName() + "." + name + ".factory",
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
      Logger logger = System.getLogger(CacheCreator.class.getName());
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
   * Cache of JSON input to the corresponding numeric primitive.
   *
   * @return the cache.
   */
  public static ICache<String, PNumber> numberCache() {
    return myNumberCache;
  }


  public static void setKeyCache(ICache<String, String> newCache) {
    if (newCache != null) {
      myKeyCache = newCache;
    } else {
      myKeyCache = new NoCache<>();
    }
  }


  public static void setNumberCache(ICache<String, PNumber> newNumberCache) {
    if (newNumberCache != null) {
      myNumberCache = newNumberCache;
    } else {
      myNumberCache = new NoCache<>();
    }
  }


  public static void setStringCache(ICache<String, PString> newCache) {
    if (newCache != null) {
      myStringCache = newCache;
    } else {
      myStringCache = new NoCache<>();
    }
  }


  public static void setValueCache(ICache<Number, Primitive> newCache) {
    if (newCache != null) {
      myValueCache = newCache;
    } else {
      myValueCache = new NoCache<>();
    }
  }


  /**
   * Cache of JSON input to the corresponding text primitive.
   *
   * @return the cache.
   */
  public static ICache<String, PString> stringCache() {
    return myStringCache;
  }


  /**
   * Cache of numbers to their encapsulated JSON representation. A representation is almost always a PNumber, but it could be a PString for a non-finite value.
   *
   * @return the cache.
   */
  public static ICache<Number, Primitive> valueCache() {
    return myValueCache;
  }


  static {
    myNumberCache = createCache("numbers");
    myKeyCache = createCache("keys");
    myStringCache = createCache("strings");
    myValueCache = createCache("values");
  }
}
