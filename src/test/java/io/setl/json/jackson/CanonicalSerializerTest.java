package io.setl.json.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.setl.json.CJArray;
import io.setl.json.CJObject;
import io.setl.json.Canonical;
import io.setl.json.jackson.objects.Car;
import io.setl.json.jackson.objects.Fleet;
import io.setl.json.jackson.objects.Truck;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class CanonicalSerializerTest {

  Fleet fleet = new Fleet();


  @BeforeEach
  public void before() {
    Car car1 = new Car("Ford", "Mondeo", 5, 120);
    car1.setMetadata(Canonical.create("META"));
    Car car2 = new Car("Mercedes-Benz", "S500", 5, 250.0);
    CJObject object = new CJObject();
    object.put("A", 123);
    car2.setMetadata(object);

    Truck truck1 = new Truck("Isuzu", "NQR", 7500.0);
    CJArray array = new CJArray();
    array.add(1);
    array.add("B");
    truck1.setMetadata(array);
    Truck truck2 = new Truck("BMW", "X6", 6000.0);
    truck2.setDocuments(object);
    fleet.add(car1);
    fleet.add(truck1);
    fleet.add(car2);
    fleet.add(truck2);
  }


  @Test
  public void serialize() throws IOException {
    ObjectMapper mapper = new ObjectMapper(new CanonicalFactory());
    mapper.registerModule(new JsonModule());

    String json = mapper.writeValueAsString(fleet);
    // Warning, if you refactor the code, the class names in this will break.
    assertEquals(
        "{\"vehicles\":["
            + "[\"io.setl.json.jackson.objects.Car\","
            + "{\"make\":\"Ford\",\"metadata\":\"META\",\"model\":\"Mondeo\",\"seatingCapacity\":5,\"topSpeed\":120}],"
            + "[\"io.setl.json.jackson.objects.Truck\","
            + "{\"documents\":null,\"make\":\"Isuzu\",\"metadata\":[1,\"B\"],\"model\":\"NQR\",\"payloadCapacity\":7500}],"
            + "[\"io.setl.json.jackson.objects.Car\","
            + "{\"make\":\"Mercedes-Benz\",\"metadata\":{\"A\":123},\"model\":\"S500\",\"seatingCapacity\":5,\"topSpeed\":250}],"
            + "[\"io.setl.json.jackson.objects.Truck\","
            + "{\"documents\":{\"A\":123},\"make\":\"BMW\",\"metadata\":null,\"model\":\"X6\",\"payloadCapacity\":6000}]]}",
        json
    );

    Fleet copy = mapper.readValue(json, Fleet.class);
    assertEquals(fleet, copy);
  }

}