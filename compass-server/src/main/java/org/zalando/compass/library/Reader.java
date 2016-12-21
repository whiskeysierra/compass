package org.zalando.compass.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Reader {

    private final SchemaValidator validator;
    private final ObjectMapper mapper;

    @Autowired
    public Reader(final SchemaValidator validator, final ObjectMapper mapper) {
        this.validator = validator;
        this.mapper = mapper;
    }

    public <T> T read(final JsonNode node, final Class<T> type) throws IOException {
        validator.validate(type.getSimpleName(), node);
        return mapper.treeToValue(node, type);
    }

}
