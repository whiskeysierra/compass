package org.zalando.compass.infrastructure.validation;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class JsonSchemaValidationService implements ValidationService {

    private final JsonSchemaValidator validator;

    @Override
    public void check(final Dimension dimension, final Collection<Value> values) {
        check(singleton(dimension), values);
    }

    @Override
    public void check(final Collection<Dimension> dimensions, final Collection<Value> values) {
        throwIfNotEmpty(validate(dimensions, values));
    }

    private List<Violation> validate(final Collection<Dimension> dimensions, final Collection<Value> values) {
        return values.stream()
                .flatMap(value -> validate(dimensions, value).stream())
                .collect(toList());
    }

    @Override
    public void check(final Collection<Dimension> dimensions, final Value value) {
        throwIfNotEmpty(validate(dimensions, value));
    }

    private List<Violation> validate(final Collection<Dimension> dimensions, final Value value) {
        return dimensions.stream()
                .flatMap(dimension -> validate(dimension, value).stream())
                .collect(toList());
    }

    private List<Violation> validate(final Dimension dimension, final Value value) {
        @Nullable final JsonNode node = value.getDimensions().get(dimension.getId());

        if (node == null) {
            return emptyList();
        }

        final JsonNode schema = dimension.getSchema();
        return validator.validate(schema, node);
    }

    @Override
    public void check(final Key key, final Collection<Value> values) {
        throwIfNotEmpty(values.stream()
                .flatMap(value -> validate(key, value).stream())
                .collect(toList()));
    }

    @Override
    public void check(final Key key, final Value value) {
        throwIfNotEmpty(validate(key, value));
    }

    private List<Violation> validate(final Key key, final Value value) {
        final JsonNode schema = key.getSchema();
        final JsonNode node = value.getValue();
        return validator.validate(schema, node);
    }

    private void throwIfNotEmpty(final List<Violation> violations) {
        if (!violations.isEmpty()) {
            throw new ConstraintViolationProblem(BAD_REQUEST, violations);
        }
    }

}
