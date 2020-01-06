package io.setl.json;

import java.util.Objects;
import java.util.Random;

/**
 * A POJO for testing Jackson encoding and decoding.
 *
 * @author Simon Greatrix on 03/01/2020.
 */
public class Pojo {

  private int count;

  private double[] data;

  private Pojo sibling;

  private String text;


  public Pojo() {
    count = 10;
    text = "Hello, World!";
  }


  public Pojo(Random random, boolean hasChildren) {
    count = random.nextInt(100);
    text = Long.toString(random.nextLong(), 36);
    data = new double[3];
    for (int i = 0; i < 3; i++) {
      data[i] = random.nextDouble();
    }
    if (hasChildren) {
      sibling = new Pojo(random, false);
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Pojo)) {
      return false;
    }
    Pojo pojo = (Pojo) o;
    return count == pojo.count && Objects.equals(text, pojo.text);
  }


  public int getCount() {
    return count;
  }


  public double[] getData() {
    return data;
  }


  public Pojo getSibling() {
    return sibling;
  }


  public String getText() {
    return text;
  }


  @Override
  public int hashCode() {
    return count * 31 + Objects.hashCode(text);
  }


  public void setCount(int count) {
    this.count = count;
  }


  public void setData(double[] data) {
    this.data = data;
  }


  public void setSibling(Pojo sibling) {
    this.sibling = sibling;
  }


  public void setText(String text) {
    this.text = text;
  }
}
