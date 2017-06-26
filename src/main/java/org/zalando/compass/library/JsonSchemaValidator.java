package org.zalando.compass.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.JsonType;
import com.networknt.schema.JsonValidator;
import com.networknt.schema.TypeFactory;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Component;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;
import org.zalando.problem.spring.web.advice.validation.Violation;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.io.Resources.getResource;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Component
public class JsonSchemaValidator {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final JsonSchemaFactory factory = new JsonSchemaFactory(mapper);
    private final LoadingCache<String, JsonSchema> schemas = CacheBuilder.newBuilder().build(new SchemaLoader(mapper, factory));

    public JsonSchemaValidator() throws IOException {
        // needed because field assignment throws IOException
    }

    public void check(final Collection<JsonType> types, final JsonNode schema) {
        final JsonType type = TypeFactory.getSchemaNodeType(schema.path("type"));

        // TODO support more complex type definitions here, e.g. union types (i.e. arrays)
        if (!types.contains(type)) {
            throw new ConstraintViolationProblem(BAD_REQUEST, Collections.singletonList(
                    new Violation("$.schema", String.format("'%s' is not among supported types: %s", type, types))
            ));
        }
    }

    public void check(final String name, final JsonNode node) {
        throwIfNotEmpty(validate(name, node));
    }

    public List<Violation> validate(final String name, final JsonNode node) {
        return validate(schemas.getUnchecked(name), node);
    }

    public List<Violation> validate(final JsonNode schema, final JsonNode node, final String... path) {
        return validate(factory.getSchema(schema), node, path);
    }

    private List<Violation> validate(final JsonValidator validator, final JsonNode node, final String... path) {
        final Set<ValidationMessage> messages = validator.validate(node, node, join(path));

        return messages.stream()
                .sorted(comparing(ValidationMessage::getPath).thenComparing(ValidationMessage::getMessage))
                .map(message -> new Violation(message.getPath(), message.getMessage()))
                .collect(toList());
    }

    private String join(final String... properties) {
        return stream(properties).collect(collectingAndThen(joining("."),
                result -> result.isEmpty() ? "$" : "$." + result));
    }

    public void throwIfNotEmpty(final List<Violation> violations) {
        if (!violations.isEmpty()) {
            throw new ConstraintViolationProblem(BAD_REQUEST, violations);
        }
    }

    private static final class SchemaLoader extends CacheLoader<String, JsonSchema> {

        private static final Set<String> filter =
                ImmutableSet.of("example", "deprecated", "readOnly", "x-extensible-enum");

        private final JsonSchemaFactory factory;
        private final JsonNode definitions;

        public SchemaLoader(final ObjectMapper mapper, final JsonSchemaFactory factory) throws IOException {
            this.factory = factory;
            this.definitions = filter(mapper.readTree(getResource("api/api.yaml")));
        }

        private static JsonNode filter(final JsonNode node) {
            if (node.isObject()) {
                node.fields().forEachRemaining(field -> filter(field.getKey(), field.getValue()));
            } else if (node.isArray()) {
                node.elements().forEachRemaining(SchemaLoader::filter);
            }
            return node;
        }

        private static JsonNode filter(final String key, final JsonNode node) {
            if (node.isObject() && key.equals("properties")) {
                node.fields().forEachRemaining(field -> {
                    final ObjectNode value = (ObjectNode) field.getValue();
                    value.without(filter);
                });
                return node;
            }
            return filter(node);
        }

        @Override
        public JsonSchema load(final String name) throws Exception {
            final ObjectNode schema = definitions.deepCopy();
            schema.put("$ref", "#/definitions/" + name);
            return factory.getSchema(schema);
        }

    }

}
