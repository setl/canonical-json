# Canonical JSON

Implementation of the [Canonical JSON
format](https://web.archive.org/web/20191120120802/http://gibson042.github.io/canonicaljson-spec/).

The implementation conforms to [JSR 374: Java API for JSON Processing
1.1](https://javadoc.io/static/javax.json/javax.json-api/1.1.4/index.html?overview-summary.html).

The implementation can be used with
[Jackson](https://github.com/FasterXML/jackson). It exports a Jackson Module
which adds serializers and deserializers for the java.json.JsonValue types.

The implementation can be used with [Spring](https://spring.io/). If the
"setl.json.enabled" property is unset or true, it will register a Web MVC
Configurer to allow the input and output of JsonValue instances, and the
generation of Canonical JSON in responses.
