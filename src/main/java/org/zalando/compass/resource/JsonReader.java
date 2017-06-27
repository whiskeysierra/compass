package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.library.JsonSchemaValidator;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;
import org.zalando.problem.spring.web.advice.validation.Violation;

import java.io.IOException;
import java.util.List;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Component
class JsonReader {

    private final JsonSchemaValidator validator;
    private final ObjectMapper mapper;

    @Autowired
    public JsonReader(final JsonSchemaValidator validator, final ObjectMapper mapper) {
        this.validator = validator;
        this.mapper = mapper;
    }

    public <T> T read(final JsonNode node, final Class<T> type) throws IOException {
        return read(type.getSimpleName(), node, type);
    }

    private <T> T read(final String name, final JsonNode node, final Class<T> type) throws IOException {
        validate(name, node);
        return mapper.treeToValue(node, type);
    }

    private void validate(final String name, final JsonNode node) {
        final List<Violation> violations = validator.validate(name, node);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationProblem(BAD_REQUEST, violations);
        }
    }

}
