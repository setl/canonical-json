# Canonical JSON

Implementation of the [Canonical JSON format](https://web.archive.org/web/20191120120802/http://gibson042.github.io/canonicaljson-spec/).

The implementation is in three parts.

## Document model
The core classes, Primitive, JsonObject and JsonArray can be used to create any
JSON data structure. The `writeTo` and `toString` methods on these three classes
generate Canonical JSON.

## Parser
There is a comprehensive JSON parser which will parse any correct JSON. It does
not require the input to be canonical.

## Jackson integration
By creating a Jackson `ObjectMapper` like this:

```
ObjectMapper mapper = new ObjectMapper(new CanonicalFactory());
```

Jackson will generate canonical JSON.