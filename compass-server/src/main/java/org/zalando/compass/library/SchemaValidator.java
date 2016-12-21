package org.zalando.compass.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.io.Resources.getResource;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Component
class SchemaValidator {

    private final LoadingCache<String, JsonSchema> schemas;

    public SchemaValidator() {
        this.schemas = CacheBuilder.newBuilder().build(new SchemaLoader());
    }

    public void validate(final String name, final JsonNode node) {
        final JsonSchema schema = schemas.getUnchecked(name);
        final Set<ValidationMessage> messages = schema.validate(node);

        if (!messages.isEmpty()) {
            throw newProblem(messages);
        }
    }

    private static ThrowableProblem newProblem(final Set<ValidationMessage> messages) {
        return Problem.builder()
                .withType(URI.create("https://zalando.github.io/problem/constraint-violation"))
                .withStatus(BAD_REQUEST)
                .withTitle("Constraint Violation")
                .withDetail("Schema Validation Failed")
                .with("violations", messages.stream()
                        .sorted(comparing(ValidationMessage::getMessage))
                        .map(message -> ImmutableMap.of(
                                "field", message.getPath(),
                                "message", message.getMessage()))
                        .collect(toList()))
                .build();
    }

    private static final class SchemaLoader extends CacheLoader<String, JsonSchema> {

        private final Set<String> filter =
                ImmutableSet.of("example", "deprecated", "readOnly", "x-extensible-enum");

        private final JsonNode definitions;
        private final JsonSchemaFactory factory;

        public SchemaLoader() {
            try {
                final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                this.definitions = filter(mapper.readTree(getResource("api/api.yaml")));
                this.factory = new JsonSchemaFactory(mapper);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public JsonSchema load(final String name) throws Exception {
            final ObjectNode schema = definitions.deepCopy();
            schema.put("$ref", "#/definitions/" + name);
            return factory.getSchema(schema);
        }

        private JsonNode filter(final JsonNode node) {
            if (node.isObject()) {
                final Iterator<Entry<String, JsonNode>> iter = node.fields();
                while (iter.hasNext()) {
                    final Entry<String, JsonNode> elem = iter.next();
                    filter(elem.getKey(), elem.getValue());
                }
            } else if (node.isArray()) {
                final Iterator<JsonNode> iter = node.elements();
                while (iter.hasNext()) {
                    filter(iter.next());
                }
            }
            return node;
        }

        private JsonNode filter(final String key, final JsonNode node) {
            if (node.isObject() && key.equals("properties")) {
                final Iterator<Entry<String, JsonNode>> iter = node.fields();
                while (iter.hasNext()) {
                    final Entry<String, JsonNode> elem = iter.next();
                    if (elem.getValue().isObject()) {
                        final ObjectNode value = (ObjectNode) elem.getValue();
                        value.remove(filter);
                    }
                }
                return node;
            }
            return filter(node);
        }
    }

}
