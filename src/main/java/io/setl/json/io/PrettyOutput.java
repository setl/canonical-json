package io.setl.json.io;

/**
 * An output wrapper used by the Pretty Formatter.
 *
 * @author Simon Greatrix on 20/11/2020.
 */
interface PrettyOutput extends Appendable {

  enum Special {
    SEPARATOR,
    START_ARRAY,
    START_OBJECT,
    END_ARRAY,
    END_OBJECT
  }

  /**
   * Append the character sequence to the output.
   *
   * @param csq the sequence
   *
   * @return the instance to append further data to, not necessarily this
   */
  PrettyOutput append(CharSequence csq);

  /**
   * Append part of the character array to the output.
   *
   * @param csq   the array
   * @param start the first character to append
   * @param end   The index of the character following the last character in the subsequence
   *
   * @return the instance to append further data to, not necessarily this
   */
  PrettyOutput append(char[] csq, int start, int end);

  /**
   * Append part of the character sequence to the output.
   *
   * @param csq   the sequence
   * @param start the first character to append
   * @param end   The index of the character following the last character in the subsequence
   *
   * @return the instance to append further data to, not necessarily this
   */
  PrettyOutput append(CharSequence csq, int start, int end);


  /**
   * Append the character to the output.
   *
   * @param c the character
   *
   * @return the instance to append further data to, not necessarily this
   */
  PrettyOutput append(char c);


  /**
   * Append a special symbol to the output.
   *
   * @param special the symbol
   *
   * @return the instance to append further data to, not necessarily this
   */
  PrettyOutput append(Special special);


  /**
   * Close the output.
   */
  void close();


  /**
   * Flush the output. This may cause a disruption in the formatting.
   *
   * @return the instance to append further data to, not necessarily this
   */
  PrettyOutput flush();

}
