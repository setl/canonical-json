# Canonical JSON

Implementation of the [Canonical JSON format](https://web.archive.org/web/20191120120802/http://gibson042.github.io/canonicaljson-spec/).

The implementation conforms
to [JSR 374: Java API for JSON Processing 1.1](https://javadoc.io/static/javax.json/javax.json-api/1.1.4/index.html?overview-summary.html).

The implementation can be used with
[Jackson](https://github.com/FasterXML/jackson). It exports a Jackson Module which adds serializers and deserializers for the java.json.JsonValue types.

The implementation can be used with [Spring](https://spring.io/). If the
"setl.json.enabled" property is unset or true, it will register a Web MVC Configurer to allow the input and output of JsonValue instances, and the generation of
Canonical JSON in responses.

## Examples

### Getting an instance of the provider

The Java API for JSON processing may be used via the `javax.json.Json` class, or via an explicit provider.

```java
    // Load any provider from the class path. May not be the canonical JSON provider.
    JsonProvider provider=JsonProvider.provider();

    // Load the Canonical JSON provider specifically
    JsonProvider provider=new CanonicalJsonProvider();
```

The providers are thread safe and re-usable. It is recommended that the instance be kept for re-use.

### Loading a JSON Object from a file

UTF-8 encoding is assumed.

```java
    JsonObject myData;
    File file=new File("my-data.json");
    try(JsonReader reader=provider.createReader(new FileInputStream(file))) {
       myData=reader.readObject();
    }
```

Note that JsonReader instances can only read a single structure.

### Loading several JSON Objects from the same file

Json parsers normally share the restriction on readers that they can only read a single root structure, but this restriction can be turned off.

UTF-8 encoding is assumed.

```java
    JsonParserFactory factory=provider.createParserFactory(
      Map.of(ParserFactory.REQUIRE_SINGLE_ROOT,false)
    );

    JsonObject myData;
    JsonArray myArray;
    File file=new File("my-data.json");
    try(JsonParser parser=factory.createParser(new FileInputStream(file))) {
       myData=reader.getObject();
       myArray=reader.getArray();
    }
```

### Writing a JSON Object to a file

```java
    JsonObject myData = ...;
    File file = new File("my-data.json");
    try(JsonWriter writer = provider.createWriter(new FileOutputStream(file))) {
      writer.write(myData);
    }
```

### Calculating the Secure Digest of a JSON value

Generating secure digests is supported as part of the "patch" operations.

```java

    byte[] digest = Test.digest("SHA-512/256",myJsonValue);

```

### Generating canonical JSON with Jackson

```java
    ObjectMapper objectMapper = new ObjectMapper(new CanonicalFactory());
    String json = objectMapper.writeValueAsString(myPojo); 
```

### Generating `javax.json` from Jackson

```java
    JsonStructure structure = (JsonStructure) Convert.toJson(objectMapper.<JsonNode>valueToTree(object));
```

### Human friendly output

The canonical format has no additional whitespace and can be difficult to read. 

#### Setting the behaviour of `toString`

```java
    // All toString will produce "pretty" output
    CanonicalJsonProvider.setIsToPrettyString(true);
```

#### Pretty printing a single value

```java
    // myData is a map, collection, number, Boolean, or String
    Canonical.toPrettyString( Canonical.cast( myData ) );
```

### Manipulating JSON using the `java.json` API

The `CJObject` and `CJArray` classes can be created directly and offer an extended API for manipulating the data structure.

```java

  JsonObject json = provider.createObjectBuilder()
      .add("age",21)
      .add("species","cat")
      .build()
```