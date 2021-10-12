package io.setl.json.io;

import javax.json.stream.JsonLocation;

/**
 * Implementation of the JsonLocation interface.
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
public class Location implements JsonLocation {

  public static final Location UNSET = new Location(-1, -1, -1);

  protected long columnNumber = 0L;

  protected long lineNumber = 1L;

  protected long streamOffset = 0L;


  protected Location() {
    // do nothing
  }


  /**
   * Create a location with the specified location. Note that -1 should be used for any unknown value.
   *
   * @param columnNumber the column number
   * @param lineNumber   the line number
   * @param streamOffset the stream offset
   */
  public Location(long columnNumber, long lineNumber, long streamOffset) {
    this.columnNumber = columnNumber;
    this.lineNumber = lineNumber;
    this.streamOffset = streamOffset;
  }


  /**
   * New instance. Create an immutable location from a mutable one.
   *
   * @param other the mutable location
   */
  public Location(MutableLocation other) {
    columnNumber = other.getColumnNumber();
    lineNumber = other.getLineNumber();
    streamOffset = other.getStreamOffset();
  }


  @Override
  public long getColumnNumber() {
    return columnNumber;
  }


  @Override
  public long getLineNumber() {
    return lineNumber;
  }


  @Override
  public long getStreamOffset() {
    return streamOffset;
  }


  @Override
  public String toString() {
    return String.format(
        "Location(columnNumber=%s, lineNumber=%s, streamOffset=%s)",
        columnNumber, lineNumber, streamOffset
    );
  }

}
