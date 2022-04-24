package io.setl.json.io;

import io.setl.json.Canonical;

/**
 * The safe generator builds a copy of the structure and then passes it to a trusted generator.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
class SafeGenerator extends InMemoryGenerator<SafeGenerator> {

  private final TrustedGenerator target;


  SafeGenerator(Formatter formatter) {
    target = new TrustedGenerator(formatter);
  }


  @Override
  public void close() {
    try {
      super.close();
    } finally {
      target.close();
    }
  }


  protected void closeWith(Canonical canonical) {
    if (canonical != null) {
      target.write(canonical);
    }
  }


  @Override
  public void flush() {
    // This is pretty much always a no-op, unless you want to flush anything written to the output prior to starting the generator. During generation nothing
    // is written as it is prepared in-memory.
    if (isInRoot()) {
      target.flush();
    }
  }

}
