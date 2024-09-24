package io.setl.json.pointer;

/**
 * Factory for creating pointers.
 *
 * @author Simon Greatrix on 27/01/2020.
 */
public class PointerFactory {

  /**
   * Create a pointer from the provided path.
   *
   * @param path the path
   *
   * @return the pointer
   */
  public static JsonExtendedPointer create(String path) {
    if (path.isEmpty()) {
      return EmptyPointer.INSTANCE;
    }
    if (path.charAt(0) != '/') {
      throw new IllegalArgumentException("Pointer must start with '/'");
    }

    // first the terminal element
    String remaining = path;
    int pos = remaining.lastIndexOf('/');
    String txt = unescape(remaining.substring(pos + 1));

    PathElement element;
    if (txt.equals("-")) {
      element = new ExtraTerminal(remaining);
    } else if (isNumber(txt)) {
      element = new ArrayTerminal(remaining, txt);
    } else {
      element = new ObjectTerminal(remaining, txt);
    }
    remaining = remaining.substring(0, pos);

    while (true) {
      pos = remaining.lastIndexOf('/');
      if (pos == -1) {
        break;
      }
      txt = unescape(remaining.substring(pos + 1));
      if (isNumber(txt)) {
        element = new ArrayPath(remaining, txt, element);
      } else {
        element = new ObjectPath(remaining, txt, element);
      }
      remaining = remaining.substring(0, pos);
    }

    return new Pointer(path, element);
  }


  private static boolean isNumber(String s) {
    if (s.isEmpty()) {
      return false;
    }
    for (int i = s.length() - 1; i >= 0; i--) {
      char ch = s.charAt(i);
      if (ch < '0' || '9' < ch) {
        return false;
      }
    }
    return true;
  }


  private static String unescape(String s) {
    return s.replace("~1", "/").replace("~0", "~");
  }


  private PointerFactory() {
    // do nothing
  }

}
