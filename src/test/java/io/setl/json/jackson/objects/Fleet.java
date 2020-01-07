package io.setl.json.jackson.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class Fleet {

  private List<Vehicle> vehicles = new ArrayList<>();


  public void add(Vehicle v) {
    vehicles.add(v);
  }


  public List<Vehicle> getVehicles() {
    return vehicles;
  }


  public void setVehicles(List<Vehicle> vehicles) {
    this.vehicles = vehicles;
  }
}
