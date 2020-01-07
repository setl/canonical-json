package io.setl.json.jackson.objects;

import io.setl.json.JsonObject;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class Truck extends Vehicle {

  private JsonObject documents;

  private double payloadCapacity;


  public Truck(String make, String model, double payloadCapacity) {
    super(make, model);
    this.payloadCapacity = payloadCapacity;
  }


  public Truck() {
    // do nothing
  }


  public JsonObject getDocuments() {
    return documents;
  }


  public double getPayloadCapacity() {
    return payloadCapacity;
  }


  public void setDocuments(JsonObject documents) {
    this.documents = documents;
  }


  public void setPayloadCapacity(double payloadCapacity) {
    this.payloadCapacity = payloadCapacity;
  }
}
