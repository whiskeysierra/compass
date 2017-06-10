package org.zalando.compass.domain.logic.value;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.List;

@Component
class CreateValue {

    private final ValidationService validator;
    private final ValueRepository valueRepository;
    private final DimensionService dimensionService;
    private final KeyService keyService;

    @Autowired
    CreateValue(
            final ValidationService validator,
            final ValueRepository valueRepository,
            final DimensionService dimensionService,
            final KeyService keyService) {
        this.validator = validator;
        this.valueRepository = valueRepository;
        this.dimensionService = dimensionService;
        this.keyService = keyService;
    }

    @Transactional
    public void create(final Value value) {
        // TODO lock key
        // TODO lock dimensions (in order)

        validateDimensions(value);
        validateValue(value);

        valueRepository.create(value);
    }

    private void validateDimensions(final Value value) {
        final ImmutableSet<String> used = value.getDimensions().keySet();
        final List<Dimension> dimensions = dimensionService.readAll(used);

        validator.validate(dimensions, value);
    }

    private void validateValue(final Value value) {
        final Key key = keyService.read(value.getKey());
        validator.validate(key, value);
    }

}
