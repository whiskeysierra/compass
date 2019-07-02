package org.zalando.compass.library;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.model.Dimension;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.CharMatcher.whitespace;
import static com.google.common.collect.Maps.transformValues;
import static org.zalando.compass.library.Maps.transform;
import static org.zalando.fauxpas.FauxPas.throwingFunction;

@Component
public class Querying {

    private final ObjectMapper mapper;

    @Autowired
    Querying(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ImmutableMap<String, JsonNode> read(final Map<String, String> query) {
        return parse(filter(query));
    }

    private Map<String, String> filter(final Map<String, String> query) {
        final Map<String, String> copy = new LinkedHashMap<>(query);

        // TODO this should be done when decoding the cursor?! What about initially?
        copy.remove("cursor");
        copy.remove("embed");
        copy.remove("fields");
        copy.remove("filter");
        copy.remove("key");
        copy.remove("limit");
        copy.remove("offset");
        copy.remove("q");
        copy.remove("query");
        copy.remove("revision");
        copy.remove("revisions");
        copy.remove("sort");

        copy.keySet().removeIf(name -> name.startsWith("_"));

        return copy;
    }

    private ImmutableSortedMap<String, JsonNode> parse(final Map<String, String> filter) {
        return ImmutableSortedMap.copyOf(transformValues(filter,
                throwingFunction(this::fromJson)::apply));
    }

    private JsonNode fromJson(@Nullable final String value) throws IOException {
        if (value == null || whitespace().matchesAllOf(value)) {
            return MissingNode.getInstance();
        } else {
            try {
                return mapper.readTree(value);
            } catch (final JsonParseException e) {
                try {
                    return mapper.readTree("\"" + value + "\"");
                } catch (final JsonParseException ignored) {
                    throw e;
                }
            }
        }
    }

    public ImmutableSortedMap<String, String> write(@Nullable final Map<Dimension, JsonNode> filter) {
        if (filter == null) {
            return ImmutableSortedMap.of();
        }

        return ImmutableSortedMap.copyOf(transform(filter,
                Dimension::getId, throwingFunction(this::toText)));
    }

    private String toText(final JsonNode node) throws JsonProcessingException {
        return node.isValueNode() ? node.asText() : mapper.writeValueAsString(node);
    }

}
