package io.setl.json.io;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import jakarta.json.JsonConfig;
import jakarta.json.JsonConfig.KeyStrategy;
import jakarta.json.JsonReaderFactory;

/**
 * A factory for creating JSON readers.
 *
 * @author Simon Greatrix on 10/01/2020.
 */
public class ReaderFactory implements JsonReaderFactory {

  private final Map<String, ?> config;

  private final KeyStrategy keyStrategy;


  /** New instance. */
  public ReaderFactory() {
    config = Map.of(JsonConfig.KEY_STRATEGY, KeyStrategy.LAST);
    keyStrategy = KeyStrategy.LAST;
  }


  /**
   * New instance. Note the only configuration option is the key strategy.
   *
   * @param config the reader configuration
   */
  public ReaderFactory(Map<String, ?> config) {
    Object val = (config != null) ? config.get(JsonConfig.KEY_STRATEGY) : null;
    if (val == null) {
      keyStrategy = KeyStrategy.LAST;
    } else {
      if (val instanceof KeyStrategy) {
        keyStrategy = (KeyStrategy) val;
      } else {
        String t = String.valueOf(val).toUpperCase(Locale.ROOT);
        KeyStrategy strategy = KeyStrategy.LAST;
        try {
          strategy = KeyStrategy.valueOf(t);
        } catch (IllegalArgumentException e) {
          // ignore
        }
        keyStrategy = strategy;
      }
    }

    this.config = Map.of(JsonConfig.KEY_STRATEGY, keyStrategy);
  }


  @Override
  public CJReader createReader(Reader reader) {
    return new CJReader(reader, keyStrategy);
  }


  @Override
  public CJReader createReader(InputStream in) {
    return createReader(in, UTF_8);
  }


  @Override
  public CJReader createReader(InputStream in, Charset charset) {
    return new CJReader(new InputStreamReader(in, charset), keyStrategy);
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    return config;
  }

}
