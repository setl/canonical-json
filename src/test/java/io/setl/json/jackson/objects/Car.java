package io.setl.json.jackson.objects;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class Car extends Vehicle {

  private int seatingCapacity;

  private double topSpeed;


  /**
   * New instance.
   *
   * @param make            the make
   * @param model           the model
   * @param seatingCapacity the seating capacity
   * @param topSpeed        the car's top speed
   */
  public Car(String make, String model, int seatingCapacity, double topSpeed) {
    super(make, model);
    this.seatingCapacity = seatingCapacity;
    this.topSpeed = topSpeed;
  }


  public Car() {
    // do nothing
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Car)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    Car car = (Car) o;

    if (seatingCapacity != car.seatingCapacity) {
      return false;
    }
    return Double.compare(car.topSpeed, topSpeed) == 0;
  }


  public int getSeatingCapacity() {
    return seatingCapacity;
  }


  public double getTopSpeed() {
    return topSpeed;
  }


  @Override
  public int hashCode() {
    int result = super.hashCode();
    long temp;
    result = 31 * result + seatingCapacity;
    temp = Double.doubleToLongBits(topSpeed);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }


  public void setSeatingCapacity(int seatingCapacity) {
    this.seatingCapacity = seatingCapacity;
  }


  public void setTopSpeed(double topSpeed) {
    this.topSpeed = topSpeed;
  }

}