package io.setl.json.io;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

/**
 * @author Simon Greatrix on 10/01/2020.
 */
public class JReaderFactory implements JsonReaderFactory {

  @Override
  public JsonReader createReader(Reader reader) {
    return null;
  }


  @Override
  public JsonReader createReader(InputStream in) {
    return null;
  }


  @Override
  public JsonReader createReader(InputStream in, Charset charset) {
    return null;
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    return Collections.emptyMap();
  }
}
