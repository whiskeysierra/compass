package org.zalando.compass.library;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class FilterParser {

    private final ObjectMapper mapper;
    private final MapJoiner joiner = Joiner.on("\n").withKeyValueSeparator(": ");
    private final TypeReference<Map<String, JsonNode>> type = new TypeReference<Map<String, JsonNode>>() {
    };

    @Autowired
    public FilterParser(@Qualifier("yaml") final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @SneakyThrows
    public Map<String, JsonNode> parse(final Map<String, String> filter) {
        return filter.isEmpty() ?
                Collections.emptyMap() :
                mapper.readValue(joiner.join(filter), type);
    }

}
