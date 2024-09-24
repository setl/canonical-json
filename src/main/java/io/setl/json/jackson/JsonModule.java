package io.setl.json.jackson;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A Jackson module to register the serializers and deserializers for javax.json types.
 *
 * @author Simon Greatrix on 12/02/2020.
 */
public class JsonModule extends SimpleModule {

  /** The version of the library. */
  public static final Version LIBRARY_VERSION = VersionUtil.parseVersion("1.0", "io.setl", "canonical-json");


  /**
   * New instance.
   */
  public JsonModule() {
    super("Canonical-JSON", LIBRARY_VERSION);

    addDeserializer(JsonObject.class, new JsonObjectDeserializer());
    addDeserializer(JsonArray.class, new JsonArrayDeserializer());
    addDeserializer(JsonStructure.class, new JsonStructureDeserializer());
    addDeserializer(JsonValue.class, new JsonValueDeserializer());
    addDeserializer(JsonPatch.class, new JsonPatchDeserializer());
  }

}
