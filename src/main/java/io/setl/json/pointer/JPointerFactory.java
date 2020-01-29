package io.setl.json.pointer;

import javax.json.JsonPointer;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class JPointerFactory {

  /**
   * Create a pointer from the provided path.
   *
   * @param path the path
   *
   * @return the pointer
   */
  public static JsonPointer create(String path) {
    if (path.equals("")) {
      return EmptyPointer.INSTANCE;
    }
    if (path.charAt(0) != '/') {
      throw new IllegalArgumentException("Pointer must start with '/'");
    }

    // first the terminal element
    int pos = path.lastIndexOf('/');
    String txt = unescape(path.substring(pos + 1));

    PathElement element;
    if (txt.equals("-")) {
      element = new ExtraTerminal(path);
    } else if (isNumber(txt)) {
      element = new ArrayTerminal(path, txt);
    } else {
      element = new ObjectTerminal(path, txt);
    }
    path = path.substring(0, pos);

    while (true) {
      pos = path.lastIndexOf('/');
      if (pos == -1) {
        break;
      }
      txt = unescape(path.substring(pos + 1));
      if (isNumber(txt)) {
        element = new ArrayPath(path, txt, element);
      } else {
        element = new ObjectPath(path, txt, element);
      }
      path = path.substring(0, pos);
    }

    return new JPointer(path,element);
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


  private JPointerFactory() {
    // do nothing
  }

}
