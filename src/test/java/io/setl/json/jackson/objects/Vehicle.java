package io.setl.json.jackson.objects;

import java.util.Objects;
import jakarta.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
@JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_ARRAY)
@JsonSubTypes({@Type(Car.class), @Type(Truck.class)})
public abstract class Vehicle {

  private String make;

  private JsonValue metadata;

  private String model;


  protected Vehicle(String make, String model) {
    this.make = make;
    this.model = model;
  }


  protected Vehicle() {
    // do nothing
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Vehicle)) {
      return false;
    }

    Vehicle vehicle = (Vehicle) o;

    if (!make.equals(vehicle.make)) {
      return false;
    }
    if (!Objects.equals(metadata, vehicle.metadata)) {
      return false;
    }
    return model.equals(vehicle.model);
  }


  public String getMake() {
    return make;
  }


  public JsonValue getMetadata() {
    return metadata;
  }


  public String getModel() {
    return model;
  }


  @Override
  public int hashCode() {
    int result = make.hashCode();
    result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
    result = 31 * result + model.hashCode();
    return result;
  }


  public void setMake(String make) {
    this.make = make;
  }


  public void setMetadata(JsonValue metadata) {
    this.metadata = metadata;
  }


  public void setModel(String model) {
    this.model = model;
  }

}
