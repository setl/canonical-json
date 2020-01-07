package io.setl.json.jackson.objects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.setl.json.JValue;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
@JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_ARRAY)
@JsonSubTypes({@Type(Car.class), @Type(Truck.class)})
public abstract class Vehicle {

  private String make;

  private JValue metadata;

  private String model;


  protected Vehicle(String make, String model) {
    this.make = make;
    this.model = model;
  }


  protected Vehicle() {
    // do nothing
  }


  public String getMake() {
    return make;
  }


  @JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_OBJECT)
  public JValue getMetadata() {
    return metadata;
  }


  public String getModel() {
    return model;
  }


  public void setMake(String make) {
    this.make = make;
  }


  public void setMetadata(JValue metadata) {
    this.metadata = metadata;
  }


  public void setModel(String model) {
    this.model = model;
  }
}