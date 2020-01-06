package io.setl.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

/**
 * @author Simon Greatrix on 02/01/2020.
 */
public class PackageVersion implements Versioned {

  public static final Version VERSION = VersionUtil.parseVersion("1.0", "io.setl", "cj-jackson");


  @Override
  public Version version() {
    return VERSION;
  }

}
