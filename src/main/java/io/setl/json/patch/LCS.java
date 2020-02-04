package io.setl.json.patch;

/** Details of a common sequence. */
class LCS {

  /** Length of LCS. */
  int length;

  /** Location of LCS's start in new. */
  int positionNew;

  /** Location of LCS's start in old. */
  int positionOld;


  /** Create an empty common sequence. */
  LCS() {
    positionOld = 0;
    positionNew = 0;
    length = 0;
  }


  /**
   * Create a new common sequence of length one at the given position.
   *
   * @param posOld position in "old" sequence
   * @param posNew position in "new" sequence
   */
  LCS(int posOld, int posNew) {
    positionOld = posOld;
    positionNew = posNew;
    length = 1;
  }


  /**
   * Update this common sequence to be the longest of this sequence and
   * the provided sequence.
   *
   * @param other the other sequence (may be null)
   */
  void best(LCS other) {
    if (other == null) {
      return;
    }
    if (other.length > length) {
      positionOld = other.positionOld;
      positionNew = other.positionNew;
      length = other.length;
    }
  }


  @Override
  public String toString() {
    return "LCS[" + positionOld + ", " + positionNew + ", " + length + "]";
  }
}
