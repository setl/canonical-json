package io.setl.json.patch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.json.JsonArray;


/**
 * Utility code to create edit differences between two arrays.
 * <p>
 * // TODO SEE: http://www.mathcs.emory.edu/~cheung/Courses/323/Syllabus/DynProg/Docs/Hirschberg=Linear-space-LCS.pdf
 *
 * @author Simon Greatrix
 */

public class ArrayDiff1 {

//  /** Can either remove the old value or add the new value. */
//  private static final byte CHOOSE = 3;
//
//  /** At an edge, so must move along it. */
//  private static final byte EDGE = 4;
//
//  /** The new value should be added. */
//  private static final byte NEW = 2;
//
//  /** Both the old and new values should be retained. */
//  private static final byte NO_CHANGE = 0;
//
//  /** The old value should be removed. */
//  private static final byte OLD = 1;
//
//  /** The new data. */
//  private final JsonArray dataNew;
//
//  /** The old data. */
//  private final JsonArray dataOld;
//
//  /** The edits. */
//  private final ArrayList<Edit> edits = new ArrayList<>();
//
//
//  /**
//   * New instance.
//   *
//   * @param dataOld the old data
//   * @param dataNew the new data
//   */
//  public ArrayDiff1(JsonArray dataOld, JsonArray dataNew) {
//    this.dataOld = dataOld;
//    this.dataNew = dataNew;
//  }
//
//
//  /**
//   * Identify a sequence of edits that produces the new sequence from
//   * the old sequence. The sequence may not be optimal for long sequences.
//   *
//   * @param offOld the offset into the old data
//   * @param lenOld the length of the old data
//   * @param offNew the offset into the new data
//   * @param lenNew the length of the new data
//   */
//  private void createEdits(
//      int offOld, int lenOld,
//      int offNew, int lenNew
//  ) {
//    // handle simple cases
//    if (lenOld == 0) {
//      // must all be inserts
//      if (lenNew == 0) {
//        return;
//      }
//      edits.ensureCapacity(edits.size() + lenNew);
//      for (int j = 0; j < lenNew; j++) {
//        int p = offNew + j;
//        edits.add(new Edit(Change.ADD, p, dataNew.get(p)));
//      }
//      return;
//    }
//
//    // could be all deletes?
//    if (lenNew == 0) {
//      edits.ensureCapacity(edits.size() + lenOld);
//      for (int j = 0; j < lenOld; j++) {
//        edits.add(new Edit(Change.REMOVE, offNew, dataNew.get(offOld + j)));
//      }
//      return;
//    }
//
//    // if what is left is small, find the optimal edits
//    if (lenOld < 256 && lenNew < 256) {
//      createEditsShort(offOld, lenOld, offNew, lenNew);
//      return;
//    }
//
//    // Too big! Split on the best LCS and recurse.
//    LCS lcs = findLCS(offOld, lenOld, offNew, lenNew);
//    createEdits(offOld, lcs.positionOld - offOld, offNew, lcs.positionNew - offNew);
//    createEdits(
//        lcs.positionOld + lcs.length, offOld + lenOld - (lcs.positionOld + lcs.length),
//        lcs.positionNew + lcs.length, offNew + lenNew - (lcs.positionNew + lcs.length)
//    );
//  }
//
//
//  /**
//   * Identify an optimal sequence of edits that produces the new sequence from
//   * the old sequence. This algorithm requires <code>lenOld*lenNew</code>
//   * space, so should only be used when the lengths are reasonable.
//   *
//   * @param offOld start position in old data
//   * @param lenOld length of old data
//   * @param offNew start position in new data
//   * @param lenNew length of new data
//   */
//  private void createEditsShort(int offOld, int lenOld, int offNew, int lenNew) {
//    byte[][] moves = createMoveMatrix(offOld, lenOld, offNew, lenNew);
//
//    // work back across the move matrix to identify the longest common
//    // subsequence and hence the minimum number of edits required.
//    LinkedList<Edit> adds = new LinkedList<>();
//    LinkedList<Edit> dels = new LinkedList<>();
//    int i = lenOld - 1;
//    int j = lenNew - 1;
//    while ((i > -1) || (j > -1)) {
//      byte m = (i >= 0 && j >= 0) ? moves[i][j] : EDGE;
//
//      // if either, just pick one
//      if (m == CHOOSE) {
//        m = NEW;
//      }
//
//      if ((i > -1) && ((j == -1) || (m == OLD))) {
//        // edit removes an old value
//        dels.addFirst(new Edit(Change.REMOVE, i + offOld, dataOld.get(i + offOld)));
//        i--;
//      } else if ((j > -1) && ((i == -1) || (m == NEW))) {
//        // edit removes a new value
//        adds.addFirst(new Edit(Change.ADD, j + offNew, dataNew.get(j + offNew)));
//        j--;
//      } else {
//        // part of LCS, so no edit at all
//        i--;
//        j--;
//      }
//    }
//
//    // create combined edit list to return
//    edits.ensureCapacity(edits.size() + adds.size() + dels.size());
//
//    // delete locations are offset
//    int offset = 0;
//    while (!(adds.isEmpty() || dels.isEmpty())) {
//      Edit add = adds.getFirst();
//      Edit del = dels.getFirst();
//
//      int addLine = add.getIndex();
//      int delLine = del.getIndex() + offset;
//
//      if (addLine == delLine) {
//        // lines are equal so can use a set
//        edits.add(new Edit(Change.REPLACE, addLine, add.getValue()));
//        adds.removeFirst();
//        dels.removeFirst();
//      } else if (addLine < delLine) {
//        edits.add(adds.removeFirst());
//        offset++;
//      } else {
//        dels.removeFirst();
//        edits.add(new Edit(Change.REMOVE, delLine, del.getValue()));
//        offset--;
//      }
//    }
//
//    // append any remaining deletes
//    while (!dels.isEmpty()) {
//      Edit del = dels.removeFirst();
//      edits.add(new Edit(Change.REMOVE, del.getIndex() + offset, del.getValue()));
//      offset--;
//    }
//
//    // append any remaining adds
//    while (!adds.isEmpty()) {
//      edits.add(adds.removeFirst());
//    }
//  }
//
//
//  /**
//   * Create the move matrix from the LCS. This requires quadratic memory
//   * space, so should not be used on large arrays.
//   *
//   * @param offOld the starting position in the old array
//   * @param lenOld the amount of old data
//   * @param offNew the starting position in the new array
//   * @param lenNew the amount of new data
//   *
//   * @return the move matrix
//   */
//  private byte[][] createMoveMatrix(
//      int offOld, int lenOld, int offNew, int lenNew
//  ) {
//    int[][] lcs = new int[lenOld][];
//    byte[][] moves = new byte[lenOld][];
//    for (int i = 0; i < lenOld; i++) {
//      lcs[i] = new int[lenNew];
//      moves[i] = new byte[lenNew];
//    }
//
//    // As we removed the common start we know the first pair do not match.
//    lcs[0][0] = 0;
//    moves[0][0] = CHOOSE;
//
//    // Initialise first column.
//    for (int i = 1; i < lenOld; i++) {
//      int posOld = i + offOld;
//      if (Objects.equals(dataOld.get(posOld), dataNew.get(offNew))) {
//        lcs[i][0] = 1;
//        moves[i][0] = NO_CHANGE;
//      } else {
//        lcs[i][0] = lcs[i - 1][0];
//        moves[i][0] = OLD;
//      }
//    }
//
//    // Initialise the first row
//    for (int j = 1; j < lenNew; j++) {
//      int posNew = j + offNew;
//      if (Objects.equals(dataOld.get(offOld), dataNew.get(posNew))) {
//        lcs[0][j] = 1;
//        moves[0][j] = NO_CHANGE;
//      } else {
//        lcs[0][j] = lcs[0][j - 1];
//        moves[0][j] = NEW;
//      }
//    }
//
//    // work out the matrix that is used to product the LCS
//    for (int i = 1; i < lenOld; i++) {
//      int posOld = i + offOld;
//      for (int j = 1; j < lenNew; j++) {
//        int posNew = j + offNew;
//        if (Objects.equals(dataOld.get(posOld), dataNew.get(posNew))) {
//          // part of a common subsequence
//          lcs[i][j] = lcs[i - 1][j - 1] + 1;
//          moves[i][j] = NO_CHANGE;
//        } else {
//          // not part of a common subsequence
//          int lo = lcs[i - 1][j];
//          int ln = lcs[i][j - 1];
//          if (lo >= ln) {
//            lcs[i][j] = lo;
//            moves[i][j] = (lo == ln) ? CHOOSE : OLD;
//          } else {
//            lcs[i][j] = ln;
//            moves[i][j] = NEW;
//          }
//        }
//      }
//    }
//
//    return moves;
//  }
//
//
//  /**
//   * Find the LCS between two sequences. Unlike finding the move matrix, this
//   * uses linear memory space.
//   *
//   * @param offOld the starting position in the old array
//   * @param lenOld the amount of old data
//   * @param offNew the starting position in the new array
//   * @param lenNew the amount of new data
//   *
//   * @return the LCS
//   */
//  private LCS findLCS(int offOld, int lenOld, int offNew, int lenNew) {
//    LCS best = new LCS();
//
//    LCS[] curr = new LCS[lenOld];
//    LCS[] prev = new LCS[lenOld];
//
//    // Initialise first row. We are looking for anything in old that matches the first element of new.
//    for (int i = 1; i < lenOld; i++) {
//      int posOld = i + offOld;
//      if (Objects.equals(dataOld.get(posOld), dataNew.get(offNew))) {
//        prev[i] = new LCS(posOld, offNew);
//      }
//    }
//
//    for (int j = 1; j < lenNew; j++) {
//      int posNew = j + offNew;
//      if (Objects.equals(dataOld.get(offOld), dataNew.get(posNew))) {
//        curr[0] = new LCS(offOld, posNew);
//      } else {
//        curr[0] = null;
//      }
//      for (int i = 1; i < lenOld; i++) {
//        int posOld = i + offOld;
//        if (Objects.equals(dataOld.get(posOld), dataNew.get(posNew))) {
//          // part of a common subsequence
//          LCS lcs = prev[i - 1];
//          if (lcs == null) {
//            curr[i] = new LCS(posOld, posNew);
//          } else {
//            curr[i] = lcs;
//            lcs.length++;
//            best.best(lcs);
//          }
//        } else {
//          curr[i] = null;
//        }
//      }
//
//      LCS[] temp = prev;
//      prev = curr;
//      curr = temp;
//    }
//
//    return best;
//  }
//
//
//  /**
//   * Create a list of edits that create the new array from the old array.
//   *
//   * @return the list of edits
//   */
//  public List<Edit> getEdits() {
//    int lenOld = dataOld.size();
//    int lenNew = dataNew.size();
//
//    // Look for common starting sequence
//    int lenMin = Math.min(lenOld, lenNew);
//    int start = 0;
//    while (start < lenMin && Objects.equals(dataOld.get(start), dataNew.get(start))) {
//      start++;
//    }
//    // Any changes at all?
//    if (start == lenOld && start == lenNew) {
//      return Collections.emptyList();
//    }
//
//    // Have at least one addition or deletion. Look for common end sequence.
//    lenMin -= start;
//    int end = 0;
//    while (end < lenMin && Objects.equals(dataOld.get(lenOld - 1 - end), dataNew.get(lenNew - 1 - end))) {
//      end++;
//    }
//
//    int lenDiffOld = lenOld - (start + end);
//    int lenDiffNew = lenNew - (start + end);
//
//    // Do we have a single block of additions?
//    if (lenDiffOld == 0) {
//      edits.ensureCapacity(lenDiffNew);
//      for (int j = start; j < lenNew - end; j++) {
//        edits.add(new Edit(Change.ADD, j, dataNew.get(j)));
//      }
//      return edits;
//    }
//
//    // Do we have a single block of deletions?
//    if (lenDiffNew == 0) {
//      edits.ensureCapacity(lenDiffOld);
//      for (int j = start; j < lenOld - end; j++) {
//        edits.add(new Edit(Change.REMOVE, start, dataOld.get(j)));
//      }
//      return edits;
//    }
//
//    // Not a trivial change
//    createEdits(start, lenDiffOld, start, lenDiffNew);
//    return edits;
//  }

}
