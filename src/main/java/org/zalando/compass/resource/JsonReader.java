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

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
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
        validate(type.getSimpleName(), node);
        return mapper.treeToValue(node, type);
    }

    // TODO should this be in Querying?!
    public <T> T read(final String name, final String value, final Class<T> type) throws IOException {
        final JsonNode node = mapper.readTree(value);

        validate(name, node, translate(name));
        return mapper.treeToValue(node, type);
    }

    public String translate(final String name) {
        return UPPER_CAMEL.to(LOWER_UNDERSCORE, name);
    }

    private void validate(final String name, final JsonNode node, final String... path) {
        final List<Violation> violations = validator.validate(name, node, path);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationProblem(BAD_REQUEST, violations);
        }
    }

}
