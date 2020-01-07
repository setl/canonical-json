package io.setl.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.setl.json.Primitive;
import java.io.IOException;

/**
 * @author Simon Greatrix on 06/01/2020.
 */
public class PrimitiveSerializer extends JsonSerializer<Primitive> {

  public static final JavaType TYPE = TypeFactory.defaultInstance().constructType(Primitive.class);

  public static final PrimitiveSerializer INSTANCE = new PrimitiveSerializer();

  @Override
  public void serialize(Primitive value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

  }
}
