package io.setl.json.jackson.objects;

import io.setl.json.JObject;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class Truck extends Vehicle {

  private JObject documents;

  private double payloadCapacity;


  public Truck(String make, String model, double payloadCapacity) {
    super(make, model);
    this.payloadCapacity = payloadCapacity;
  }


  public Truck() {
    // do nothing
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Truck)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    Truck truck = (Truck) o;

    if (Double.compare(truck.payloadCapacity, payloadCapacity) != 0) {
      return false;
    }
    return documents != null ? documents.equals(truck.documents) : truck.documents == null;
  }


  public JObject getDocuments() {
    return documents;
  }


  public double getPayloadCapacity() {
    return payloadCapacity;
  }


  @Override
  public int hashCode() {
    int result = super.hashCode();
    long temp;
    result = 31 * result + (documents != null ? documents.hashCode() : 0);
    temp = Double.doubleToLongBits(payloadCapacity);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }


  public void setDocuments(JObject documents) {
    this.documents = documents;
  }


  public void setPayloadCapacity(double payloadCapacity) {
    this.payloadCapacity = payloadCapacity;
  }

}
