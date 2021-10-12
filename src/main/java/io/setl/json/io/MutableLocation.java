package io.setl.json.io;

/**
 * Implementation of the JsonLocation interface.
 *
 * <p>The stream offset refers to the character position in the stream, not the byte position.</p>
 *
 * <p>The column and line numbers are provided as a best effort and may not match a visual representation of the data.</p>
 *
 * <p>Tabs are allowed as whitespace between JSON elements. Tabs increment the column count as if there were an 8 character tab-stop.</p>
 *
 * <p>JSON strings can contain many special Unicode characters, including zero-width characters, characters that combine with other characters, and characters
 * that cause the text direction to be reversed. The special meaning of these characters is ignored when calculating the column number.</p>
 *
 * <table>
 *   <caption>Examples of confusing locations</caption>
 *   <tr>
 *     <th>String</th>
 *     <th>Location of the 'X' (0058)</th>
 *     <th>UTF-16</th>
 *   </tr>
 *   <tr><td>"abcXf"</td><td>5</td><td>0022 0061 0062 0063 0058 0066 0022</td></tr>
 *   <tr><td>"​​cXf"</td><td>5</td><td>0022 200b 200b 0063 0058 0066 0022<br>The 'a' and 'b' have been replaced by zero-width spaces</td></tr>
 *   <tr><td>"àcXf"</td><td>5</td><td>0022 0061 0300 0063 0058 0066 0022<br>The accent on the 'a' is a separate character.</td></tr>
 * </table>
 *
 * @author Simon Greatrix on 10/01/2020.
 */
public class MutableLocation extends Location {

  private static final int TAB_STOP = 8;

  private boolean lastWasCR = false;


  public MutableLocation() {
    // do nothing
  }


  public void setColumnNumber(long columnNumber) {
    this.columnNumber = columnNumber;
  }


  public void setLineNumber(long lineNumber) {
    this.lineNumber = lineNumber;
  }


  public void setStreamOffset(long streamOffset) {
    this.streamOffset = streamOffset;
  }


  /**
   * Update this location from reading the given character from the stream.
   *
   * @param ch the character.
   */
  public void update(int ch) {
    if (ch >= ' ') {
      streamOffset++;
      columnNumber++;
      return;
    }

    if (ch == -1) {
      // EOF reached, so don't update the position.
      return;
    }

    // we read a character, so update the position
    streamOffset++;

    if (ch == '\t') {
      // 1..8 -> 9, 9..16 -> 17, 17..24 -> 25
      long diff = TAB_STOP - ((columnNumber - 1) % TAB_STOP);
      columnNumber += diff;
      return;
    }

    // Either \n or second part of \r\n
    if (ch == '\n') {
      columnNumber = 0;
      if (lastWasCR) {
        lastWasCR = false;
      } else {
        lineNumber++;
      }
      return;
    }

    if (ch == '\r') {
      columnNumber = 0;
      lineNumber++;
      lastWasCR = true;
      return;
    }

    // C0 character that is not \t, \r nor \n. We should never see these characters in JSON documents, so I guess an error message is imminent.
    lastWasCR = false;
    columnNumber++;
  }

}
