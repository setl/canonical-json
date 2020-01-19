package io.setl.json.io;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.InputStreamReader;
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
  public JReader createReader(Reader reader) {
    return new JReader(reader);
  }


  @Override
  public JReader createReader(InputStream in) {
    return createReader(in, UTF_8);
  }


  @Override
  public JReader createReader(InputStream in, Charset charset) {
    return new JReader(new InputStreamReader(in, charset));
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    return Collections.emptyMap();
  }
}
