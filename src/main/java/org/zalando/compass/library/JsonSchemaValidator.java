package org.zalando.compass.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.networknt.schema.Format;
import com.networknt.schema.JsonMetaSchema;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.JsonType;
import com.networknt.schema.JsonValidator;
import com.networknt.schema.TypeFactory;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Component;
import org.zalando.problem.spring.web.advice.validation.Violation;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Streams.stream;
import static com.google.common.io.Resources.getResource;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class JsonSchemaValidator {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final JsonSchemaFactory factory = JsonSchemaFactory.builder(JsonSchemaFactory.getInstance())
            .objectMapper(mapper).build();
    private final LoadingCache<String, JsonSchema> schemas = CacheBuilder.newBuilder().build(new SchemaLoader(mapper, factory));

    public JsonSchemaValidator() throws IOException {
        // needed because field assignment throws IOException
    }

    public List<Violation> check(final Set<JsonType> allowed, final JsonNode schema) {
        final Set<JsonType> types = deriveTypes(schema);
        final Set<JsonType> rejected = Sets.difference(types, allowed);

        if (rejected.isEmpty()) {
            return emptyList();
        }

        return singletonList(new Violation("$.schema", format("%s not among supported types: %s", rejected, allowed)));
    }

    private Set<JsonType> deriveTypes(final JsonNode schema) {
        final JsonNode node = schema.path("type");
        final JsonType type = TypeFactory.getSchemaNodeType(node);

        if (type == JsonType.UNION) {
            return stream(node.elements())
                    .map(TypeFactory::getSchemaNodeType)
                    .collect(toSet());
        } else {
            return singleton(type);
        }
    }

    public List<Violation> validate(final String name, final JsonNode node, final String... at) {
        return validate(schemas.getUnchecked(name), node, at);
    }

    public List<Violation> validate(final JsonNode schema, final JsonNode node, final String... at) {
        return validate(factory.getSchema(schema), node, at);
    }

    private List<Violation> validate(final JsonValidator validator, final JsonNode node, final String... at) {
        final Set<ValidationMessage> messages = validator.validate(node, node, join(at));

        return messages.stream()
                .sorted(comparing(ValidationMessage::getPath).thenComparing(ValidationMessage::getMessage))
                .map(message -> {
                    final String path = message.getPath();
                    final String pointer = path.replace('.', '/');
                    return new Violation(pointer, message.getMessage().replace(path, pointer));
                })
                .collect(toList());
    }

    private String join(final String... properties) {
        return stream(properties).collect(joining("/"));
    }

    private static final class SchemaLoader extends CacheLoader<String, JsonSchema> {

        private final JsonSchemaFactory factory;
        private final JsonNode definitions;

        public SchemaLoader(final ObjectMapper mapper, final JsonSchemaFactory factory) throws IOException {
            this.factory = factory;
            this.definitions = mapper.readTree(getResource("api/api.yaml"));

            final Set<String> ignored = Sets.newHashSet(definitions.fieldNames());
            ignored.remove("id");
            ignored.remove("parameters");
            ignored.remove("definitions");

            ObjectNode.class.cast(definitions).remove(ignored);
        }

        @Override
        public JsonSchema load(final String name) {
            final ObjectNode schema = definitions.deepCopy();
            final boolean isDefinition = schema.get("definitions").has(name);
            schema.put("$ref", isDefinition ? "#/definitions/" + name : "#/parameters/" + name);
            return factory.getSchema(schema);
        }

    }

}
