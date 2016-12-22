package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.library.JsonSchemaValidator;

import java.util.Collection;
import java.util.Set;

import static java.util.Collections.singleton;

@Service
class SchemaValidator {

    private final JsonSchemaValidator validator;

    @Autowired
    public SchemaValidator(final JsonSchemaValidator validator) {
        this.validator = validator;
    }

    public void validate(final Dimension dimension, final Collection<Value> values) {
        final Set<Dimension> dimensions = singleton(dimension);
        values.forEach(value -> validate(dimensions, value));
    }

    public void validate(final Collection<Dimension> dimensions, final Value value) {
        for (final Dimension dimension : dimensions) {
            final JsonNode schema = dimension.getSchema();
            final JsonNode node = value.getDimensions().get(dimension.getId());
            validator.validate(schema, node, "dimensions", dimension.getId());
        }
    }

    public void validate(final Key key, final Collection<Value> values) {
        values.forEach(value -> validate(key, value));
    }

    public void validate(final Key key, final Value value) {
        final JsonNode schema = key.getSchema();
        final JsonNode node = value.getValue();
        validator.validate(schema, node, "value");
    }

}
