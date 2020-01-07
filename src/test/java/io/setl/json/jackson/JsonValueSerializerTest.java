package io.setl.json.jackson;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.setl.json.JsonArray;
import io.setl.json.JsonObject;
import io.setl.json.Primitive;
import io.setl.json.jackson.objects.Car;
import io.setl.json.jackson.objects.Fleet;
import io.setl.json.jackson.objects.Truck;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon Greatrix on 2020-01-07.
 */
public class JsonValueSerializerTest {

  Fleet fleet = new Fleet();


  @Before
  public void before() {
    Car car1 = new Car("Ford", "Mondeo", 5, 120);
    car1.setMetadata(Primitive.create("META"));
    Car car2 = new Car("Mercedes-Benz", "S500", 5, 250.0);
    JsonObject object = new JsonObject();
    object.put("A", 123);
    car2.setMetadata(object);

    Truck truck1 = new Truck("Isuzu", "NQR", 7500.0);
    JsonArray array = new JsonArray();
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
  public void serialize() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper(new CanonicalFactory());
    String json = mapper.writeValueAsString(fleet);
    // Warning, if you refactor the code, the class names in this will break.
    assertEquals(
        "{\"vehicles\":[[\"io.setl.json.jackson.objects.Car\",{\"make\":\"Ford\",\"metadata\":{\"io.setl.json.Primitive\":\"META\"},"
            + "\"model\":\"Mondeo\",\"seatingCapacity\":5,\"topSpeed\":120}],[\"io.setl.json.jackson.objects.Truck\",{\"documents\":null,"
            + "\"make\":\"Isuzu\",\"metadata\":{\"io.setl.json.JsonArray\":[1,\"B\"]},\"model\":\"NQR\",\"payloadCapacity\":7500}],"
            + "[\"io.setl.json.jackson.objects.Car\",{\"make\":\"Mercedes-Benz\",\"metadata\":{\"io.setl.json.JsonObject\":{\"A\":123}},\"model\":\"S500\","
            + "\"seatingCapacity\":5,\"topSpeed\":250}],[\"io.setl.json.jackson.objects.Truck\",{\"documents\":{\"A\":123},\"make\":\"BMW\",\"metadata\":null,"
            + "\"model\":\"X6\",\"payloadCapacity\":6000}]]}",
        json
    );
  }
}