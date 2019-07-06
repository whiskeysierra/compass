package org.zalando.compass.core.infrastructure.validation;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.spi.validation.ValidationService;
import org.zalando.compass.library.Maps;
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
        final var dimensions = Maps.transform(value.getDimensions(), Dimension::getId);
        @Nullable final var node = dimensions.get(dimension.getId());

        if (node == null) {
            return emptyList();
        }

        final var schema = dimension.getSchema();
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
        final var schema = key.getSchema();
        final var node = value.getValue();
        return validator.validate(schema, node);
    }

    private void throwIfNotEmpty(final List<Violation> violations) {
        if (!violations.isEmpty()) {
            throw new ConstraintViolationProblem(BAD_REQUEST, violations);
        }
    }

}
