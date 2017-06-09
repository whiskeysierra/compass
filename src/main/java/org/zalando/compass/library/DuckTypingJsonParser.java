package org.zalando.compass.library;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.gag.annotation.remark.Hack;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * A parser that takes a raw {@code Map<String, String>} usually passed as a query string, e.g.
 * {@code country=DE&age=32&active=true} and converts it to a {@code Map<String, JsonNode>}. It does so by guessing
 * the type based on the appearance of the value, e.g. if a value looks like a boolean it will become a
 * {@link com.fasterxml.jackson.databind.node.BooleanNode}.
 *
 * In order to prevent incorrect typing, e.g. the value {@code true} should stay a string, it needs to be wrapped in
 * double quotes: {@code "true"}.
 */
@Component
public class DuckTypingJsonParser {

    private final ObjectMapper mapper;
    private final MapJoiner joiner = Joiner.on("\n").withKeyValueSeparator(": ");
    private final TypeReference<Map<String, JsonNode>> type = new TypeReference<Map<String, JsonNode>>() {
    };

    @Autowired
    public DuckTypingJsonParser(@Qualifier("yaml") final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @SneakyThrows
    public Map<String, JsonNode> parse(final Map<String, String> filter) {
        return filter.isEmpty() ?
                Collections.emptyMap() :
                mapper.readValue(renderAsYaml(filter), type);
    }

    @Hack
    private String renderAsYaml(final Map<String, String> map) {
        return joiner.join(map);
    }

}
