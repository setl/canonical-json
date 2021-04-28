package io.setl.json.parser;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

/**
 * Factory for generating parsers.
 *
 * @author Simon Greatrix on 13/01/2020.
 */
public class ParserFactory implements JsonParserFactory {

  /** Should the parser expect a single root value in a stream, or multiple ones?. By default, the parser expects a single root. */
  public static final String REQUIRE_SINGLE_ROOT = "setl.json.parser.requireSingleRoot";

  private boolean singleRoot = true;


  /**
   * New instance.
   *
   * @param config Configuration - may be null or specify if a single root is required (which defaults to true).
   */
  public ParserFactory(Map<String, ?> config) {
    if (config != null && config.containsKey(REQUIRE_SINGLE_ROOT)) {
      singleRoot = Boolean.valueOf(String.valueOf(config.get(REQUIRE_SINGLE_ROOT)));
    }
  }


  @Override
  public JsonParser createParser(Reader reader) {
    Parser parser = new Parser(reader);
    parser.setRequireSingleRoot(singleRoot);
    return parser;
  }


  @Override
  public JsonParser createParser(InputStream in) {
    return createParser(in, UTF_8);
  }


  @Override
  public JsonParser createParser(InputStream in, Charset charset) {
    return createParser(new InputStreamReader(in, charset));
  }


  @Override
  public JsonParser createParser(JsonObject obj) {
    return new StructureParser(obj);
  }


  @Override
  public JsonParser createParser(JsonArray array) {
    return new StructureParser(array);
  }


  @Override
  public Map<String, ?> getConfigInUse() {
    TreeMap<String, Object> map = new TreeMap<>();
    map.put(REQUIRE_SINGLE_ROOT, singleRoot);
    return Collections.unmodifiableSortedMap(map);
  }

}
