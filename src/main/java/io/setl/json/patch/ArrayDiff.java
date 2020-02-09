package io.setl.json.patch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.json.JsonArray;
import javax.json.JsonValue;

import io.setl.json.JArray;

/**
 * @author Simon Greatrix on 07/02/2020.
 */
class ArrayDiff {

  /** Can either remove the old value or add the new value. */
  private static final byte MOVE_CHOOSE = 1;

  /** The new value should be added. */
  private static final byte MOVE_NEW = 2;

  /** Both the old and new values should be retained. */
  private static final byte MOVE_NO_CHANGE = 3;

  /** The old value should be removed. */
  private static final byte MOVE_OLD = 4;

  /** Maximum size of a dimensions for the sub-problem to be considered "small". */
  private static final int SMALL_DIMENSION = 32_000;

  /** The size at which we use quadratic space to process the array differences. */
  private static final int SMALL_QUADRATIC = 250_000;


  public static void main(String[] args) {
    Random random = new Random();
    int[] set1 = new int[60000];
    for(int i=0;i<set1.length;i++) {
      set1[i] = random.nextInt(8);
    }
    ArrayList<Integer> list = new ArrayList<>();
    list.ensureCapacity(set1.length);
    for(int i : set1) {
      list.add(i);
    }
    int j=0;
    while( j<list.size() ) {
      int o = random.nextInt(3);
      switch( o ) {
        case 0: list.remove(j); break;
        case 1: list.add(j,random.nextInt(8));
        case 2: list.set(j,random.nextInt(8));
      }
      j+= 1 + random.nextInt(4);
    }

    int[] set2 = list.stream().mapToInt(Integer::intValue).toArray();

    ArrayDiff diff = new ArrayDiff(new JPatchBuilder(), "/", null, null);
    diff.inInts = set1;
    diff.outInts = set2;

    long n0 = System.nanoTime();
    int[] lens = diff.getLengthForward(0,set1.length,0,set2.length);
    long n1 = System.nanoTime();
    long t0 = n1-n0;
    System.out.println(lens[lens.length-1]);

    n0 = System.nanoTime();
    lens = diff.getLengthReverse(0,set1.length,0,set2.length);
    n1 = System.nanoTime();
    long t1 = n1-n0;
    System.out.println(lens[0]);

    n0 = System.nanoTime();
    int center = diff.inInts.length / 2;
    int[] forward = diff.getLengthForward(0, center, 0, diff.outInts.length);
    int[] reverse = diff.getLengthReverse(center, diff.inInts.length - center, 0, diff.outInts.length);
    int split = -1;
    int best = -1;
    for (int i = 0; i < diff.outInts.length; i++) {
      int l = forward[i] + reverse[i];
      if (l > best) {
        best = l;
        split = i;
      }
    }
    n1 = System.nanoTime();
    long t3 = n1-n0;
    System.out.println(best+" , "+split);
    System.out.println(t0+" , "+t1+" , "+t3);
  }


  private final JPatchBuilder builder;
  private final JsonArray input;
  private final JsonArray output;
  private final String prefix;
  private int[] inInts;
  private int[] outInts;


  ArrayDiff(JPatchBuilder builder, String prefix, JsonArray input, JsonArray output) {
    this.builder = builder;
    this.prefix = prefix;
    this.input = input;
    this.output = output;
  }


  private byte[][] doFullSmallQuadratic(int inOffset, int inLength, int outOffset, int outLength) {
    short[] lengthsPrevious = new short[inLength + 1];
    short[] lengthsCurrent = new short[inLength + 1];
    byte[][] moves = new byte[inLength][outLength];

    // Work out the moves.
    for (int i = outLength - 1; i >= 0; i--) {
      int outValue = outInts[outOffset + i];
      for (int j = inLength - 1; j >= 0; j--) {
        int inValue = inInts[inOffset + j];
        if (inValue == outValue) {
          lengthsCurrent[j] = (short) (1 + lengthsPrevious[j + 1]);
          moves[j][i] = MOVE_NO_CHANGE;
        } else {
          int d = lengthsCurrent[j + 1] - lengthsPrevious[j];
          if (d > 0) {
            lengthsCurrent[j] = lengthsCurrent[j + 1];
            moves[j][i] = MOVE_OLD;
          } else if (d == 0) {
            moves[j][i] = MOVE_CHOOSE;
            lengthsCurrent[j] = lengthsPrevious[j];
          } else {
            moves[j][i] = MOVE_NEW;
            lengthsCurrent[j] = lengthsPrevious[j];
          }
        }
      }

      short[] tmp = lengthsCurrent;
      lengthsCurrent = lengthsPrevious;
      lengthsPrevious = tmp;
    }

    return moves;
  }


  private byte[][] doSimpleSmallQuadratic(int inOffset, int inLength, int outOffset, int outLength) {
    short[][] commonLengths = new short[inLength + 1][outLength + 1];
    for (int i = 0; i <= inLength; i++) {
      commonLengths[i][outLength] = 1;
    }
    for (int i = 0; i <= outLength; i++) {
      commonLengths[inLength][i] = 1;
    }
    byte[][] moves = new byte[inLength][outLength];
    int stackSize = Math.max(16, Math.max(inLength, outLength) * 2);
    short[] xStack = new short[stackSize];
    short[] yStack = new short[stackSize];
    int stackPosition = 1;
    while (stackPosition > 0) {
      stackPosition--;
      short x = xStack[stackPosition];
      short y = yStack[stackPosition];

      if (inInts[inOffset + x] == outInts[outOffset + y]) {
        // need x+1,y+1
        short d = commonLengths[x + 1][y + 1];
        if (d == 0) {
          // re-enqueue this
          stackPosition++;

          // enqueue x+1,y+1
          xStack[stackPosition] = (short) (x + 1);
          yStack[stackPosition] = (short) (y + 1);
          stackPosition++;
        } else {
          commonLengths[x][y] = (short) (d + 1);
          moves[x][y] = MOVE_NO_CHANGE;
        }
      } else {
        // need x+1,y and x,y+1
        short d0 = commonLengths[x + 1][y];
        short d1 = commonLengths[x][y + 1];
        if (d0 > 0 && d1 > 0) {
          commonLengths[x][y] = (short) Math.max(d0, d1);
          Integer d = Integer.compare(d0, d1);
          if (d < 0) {
            moves[x][y] = MOVE_NEW;
          } else if (d == 0) {
            moves[x][y] = MOVE_CHOOSE;
          } else {
            moves[x][y] = MOVE_OLD;
          }
        } else {
          // re-enqueue this
          stackPosition++;

          // enqueue x+1,y if necessary
          if (d0 == 0) {
            xStack[stackPosition] = (short) (x + 1);
            yStack[stackPosition] = y;
            stackPosition++;
          }
          // enqueue x,y+1 if necessary
          if (d1 == 0) {
            xStack[stackPosition] = x;
            yStack[stackPosition] = (short) (y + 1);
            stackPosition++;
          }
        }
      }

      // grow stack if it is getting tight
      if ((stackPosition + 2) >= xStack.length) {
        int newSize = xStack.length * 2;
        short[] newStack = new short[newSize];
        System.arraycopy(xStack, 0, newStack, 0, stackPosition);
        xStack = newStack;

        newStack = new short[newSize];
        System.arraycopy(yStack, 0, newStack, 0, stackPosition);
        yStack = newStack;
      }
    }

    return moves;
  }


  private void evaluate(int inOffset, int inLength, int outOffset, int outLength) {
    // strip common start
    while (inLength > 0 && outLength > 0 && inInts[inOffset] == outInts[outOffset]) {
      inOffset++;
      inLength--;
      outOffset++;
      outLength--;
    }

    // strip common end
    while (inLength > 0 && outLength > 0 && inInts[inOffset + inLength - 1] == outInts[outOffset + outLength - 1]) {
      inLength--;
      outLength--;
    }

    // handle trivial cases...
    // perhaps everything is an add?
    if (inLength == 0) {
      for (int i = 0; i < inLength; i++) {
        int outPos = outOffset + i;
        builder.add(prefix + outPos, output.get(outPos));
      }
      return;
    }

    // perhaps everything is a remove?
    if (outLength == 0) {
      for (int i = 0; i < outLength; i++) {
        builder.remove(prefix + outOffset);
      }
      return;
    }

    if (inLength < SMALL_DIMENSION && outLength < SMALL_DIMENSION && (inLength * outLength) < SMALL_QUADRATIC) {
      byte[][] moves = doFullSmallQuadratic(inOffset, inLength, outOffset, outLength);
      makeOperations(inLength, outLength, moves);
      return;
    }

    // split the problem into two
    int center = inLength / 2;
    int[] forward = getLengthForward(inOffset, center, outOffset, outLength);
    int[] reverse = getLengthReverse(inOffset + center, inLength - center, outOffset, outLength);
    int split = -1;
    int best = -1;
    for (int i = 0; i < outLength; i++) {
      int l = forward[i] + reverse[i];
      if (l > best) {
        best = l;
        split = i;
      }
    }

    evaluate(inOffset, center, outOffset, split);
    evaluate(inOffset + center, inLength - center, outOffset + split, outLength - split);
  }


  private int[] getLengthForward(int inOffset, int inLength, int outOffset, int outLength) {
    int[] lengthsPrevious = new int[outLength];
    int[] lengthsCurrent = new int[outLength];

    // Work out the lengths.
    for (int i = 0; i < inLength; i++) {
      int inValue = inInts[inOffset + i];
      if (inValue == outInts[outOffset]) {
        lengthsCurrent[0] = 1;
      } else {
        lengthsCurrent[0] = lengthsPrevious[0];
      }

      for (int j = 1; j < outLength; j++) {
        int outValue = outInts[outOffset + j];
        if (inValue == outValue) {
          lengthsCurrent[j] = 1 + lengthsPrevious[j - 1];
        } else {
          int d = lengthsCurrent[j - 1] - lengthsPrevious[j];
          if (d > 0) {
            lengthsCurrent[j] = lengthsCurrent[j - 1];
          } else if (d == 0) {
            lengthsCurrent[j] = lengthsPrevious[j];
          } else {
            lengthsCurrent[j] = lengthsPrevious[j];
          }
        }
      }

      int[] tmp = lengthsCurrent;
      lengthsCurrent = lengthsPrevious;
      lengthsPrevious = tmp;
    }

    return lengthsPrevious;
  }


  private int[] getLengthReverse(int inOffset, int inLength, int outOffset, int outLength) {
    int[] lengthsPrevious = new int[outLength];
    int[] lengthsCurrent = new int[outLength];

    // Work out the length.
    for (int i = inLength - 1; i >= 0; i--) {
      int inValue = inInts[inOffset + i];

      int j = outLength - 1;
      if (outInts[outOffset + j] == inValue) {
        lengthsCurrent[j] = 1;
      } else {
        lengthsCurrent[j] = lengthsPrevious[j];
      }

      for (j = outLength - 2; j >= 0; j--) {
        int outValue = outInts[outOffset + j];
        if (inValue == outValue) {
          lengthsCurrent[j] = 1 + lengthsPrevious[j + 1];
        } else {
          int d = lengthsCurrent[j + 1] - lengthsPrevious[j];
          if (d > 0) {
            lengthsCurrent[j] = lengthsCurrent[j + 1];
          } else if (d == 0) {
            lengthsCurrent[j] = lengthsPrevious[j];
          } else {
            lengthsCurrent[j] = lengthsPrevious[j];
          }
        }
      }

      int[] tmp = lengthsCurrent;
      lengthsCurrent = lengthsPrevious;
      lengthsPrevious = tmp;
    }

    return lengthsPrevious;
  }


  private void makeOperations(int inLength, int outLength, byte[][] moves) {
    // Translate the moves into operations
    int inPos = 0;
    int outPos = 0;
    while (inPos < inLength && outPos < outLength) {
      switch (moves[inPos][outPos]) {
        case MOVE_CHOOSE:
          builder.replace(prefix + outPos, output.get(outPos));
          inPos++;
          outPos++;
          break;
        case MOVE_NEW:
          builder.add(prefix + outPos, output.get(outPos));
          outPos++;
          break;
        case MOVE_NO_CHANGE:
          inPos++;
          outPos++;
          break;
        case MOVE_OLD:
          builder.remove(prefix + outPos);
          inPos++;
          break;
        default:
          throw new IllegalStateException("Unknown move=" + moves[inPos][outPos]);
      }
    }
    while (inPos < inLength) {
      builder.remove(prefix + outPos);
      inPos++;
    }
    while (outPos < outLength) {
      builder.add(prefix + outPos, output.get(outPos));
      outPos++;
    }
  }


  /**
   * Map all the input values to integers so we can compare them quickly.
   */
  private void mapToInts() {
    HashMap<JsonValue, Integer> assigned = new HashMap<>();
    inInts = mapToInts(assigned, input);
    outInts = mapToInts(assigned, output);
  }


  /**
   * Do one of our array mappings.
   *
   * @param assigned the integer assignments
   * @param array    the array to map
   *
   * @return the mappings
   */
  private int[] mapToInts(HashMap<JsonValue, Integer> assigned, JsonArray array) {
    int[] mapping = new int[array.size()];
    for (int i = 0; i < mapping.length; i++) {
      mapping[i] = assigned.computeIfAbsent(array.get(i), jv -> assigned.size());
    }
    return mapping;
  }


  void process() {
    mapToInts();
    evaluate(0, inInts.length, 0, outInts.length);
  }

}
