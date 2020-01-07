package io.setl.json.jackson.objects;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class Car extends Vehicle {

  private int seatingCapacity;

  private double topSpeed;


  public Car(String make, String model, int seatingCapacity, double topSpeed) {
    super(make, model);
    this.seatingCapacity = seatingCapacity;
    this.topSpeed = topSpeed;
  }


  public Car() {
    // do nothing
  }


  public int getSeatingCapacity() {
    return seatingCapacity;
  }


  public double getTopSpeed() {
    return topSpeed;
  }


  public void setSeatingCapacity(int seatingCapacity) {
    this.seatingCapacity = seatingCapacity;
  }


  public void setTopSpeed(double topSpeed) {
    this.topSpeed = topSpeed;
  }
}