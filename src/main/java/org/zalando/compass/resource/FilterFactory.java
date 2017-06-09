package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.library.DuckTypingJsonParser;
import org.zalando.compass.library.QueryFilter;

import java.util.Map;

@Component
class FilterFactory {

    private final QueryFilter filter = new QueryFilter(Keywords.RESERVED);
    private final DuckTypingJsonParser parser;

    @Autowired
    FilterFactory(final DuckTypingJsonParser parser) {
        this.parser = parser;
    }

    Map<String, JsonNode> create(final Map<String, String> query) {
        return filter.filter(parser.parse(query));
    }

}
