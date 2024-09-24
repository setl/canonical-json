package io.setl.json.io;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonReaderFactory;

/**
 * A factory for creating JSON readers.
 *
 * @author Simon Greatrix on 10/01/2020.
 */
public class ReaderFactory implements JsonReaderFactory {

  /** New instance. */
  public ReaderFactory() {
    // nothing to do
  }


  @Override
  public CJReader createReader(Reader reader) {
    return new CJReader(reader);
  }


  @Override
  public CJReader createReader(InputStream in) {
    return createReader(in, UTF_8);
  }


  @Override
  public CJReader createReader(InputStream in, Charset charset) {
    return new CJReader(new InputStreamReader(in, charset));
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    return Collections.emptyMap();
  }

}
