package io.setl.json.patch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.json.JsonArray;
import javax.json.JsonValue;

import io.setl.json.patch.key.Key;
import io.setl.json.patch.key.ObjectKey;

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
    int[] set1 = new int[10000];
    for (int i = 0; i < set1.length; i++) {
      set1[i] = random.nextInt(8);
    }
    ArrayList<Integer> list = new ArrayList<>();
    list.ensureCapacity(set1.length);
    for (int i : set1) {
      list.add(i);
    }
    int j = 0;
    while (j < list.size()) {
      int o = random.nextInt(3);
      switch (o) {
        case 0:
          list.remove(j);
          break;
        case 1:
          list.add(j, random.nextInt(8));
        case 2:
          list.set(j, random.nextInt(8));
      }
      j += 1 + random.nextInt(4);
    }

    int[] set2 = list.stream().mapToInt(Integer::intValue).toArray();

    ArrayDiff diff = new ArrayDiff(new JPatchBuilder(), new ObjectKey(null,""), null, null);
    diff.inInts = set1;
    diff.outInts = set2;

    long sum = 0;
    for(int i=0;i<10;i++) {
      long n0 = System.nanoTime();
      diff.evaluate(0, diff.inInts.length, 0, diff.outInts.length);
    //  diff.doQuadratic(0, diff.inInts.length, 0, diff.outInts.length);
      long n1 = System.nanoTime();
      sum += n1-n0;
      System.out.println(String.format("%,9d %9.3f",n1 - n0,(double) sum / (i+1)));
    }

  }


  private final JPatchBuilder builder;
  private final JsonArray input;
  private final JsonArray output;
  private final Key root;
  private int[] inInts;
  private int[] outInts;


  ArrayDiff(JPatchBuilder builder, Key root, JsonArray input, JsonArray output) {
    this.builder = builder;
    this.root = root;
    this.input = input;
    this.output = output;
  }


  private byte[][] doQuadratic(int inOffset, int inLength, int outOffset, int outLength) {
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
      String prefix = root.toString() + "/";
      for (int i = 0; i < inLength; i++) {
        int outPos = outOffset + i;
        builder.add(prefix + outPos, output.get(outPos));
      }
      return;
    }

    // perhaps everything is a remove?
    if (outLength == 0) {
      String prefix = root.toString() + "/";
      for (int i = 0; i < outLength; i++) {
        builder.remove(prefix + outOffset);
      }
      return;
    }

    if (inLength < SMALL_DIMENSION && outLength < SMALL_DIMENSION && (inLength * outLength) < SMALL_QUADRATIC) {
      byte[][] moves = doQuadratic(inOffset, inLength, outOffset, outLength);
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
    String prefix = root.toString() + "/";
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
