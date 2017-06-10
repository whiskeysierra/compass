package org.zalando.compass.domain.logic.value;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.logic.LockService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueId;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReplaceValue {

    private final ValidationService validator;
    private final ValueRepository repository;
    private final DimensionService dimensionService;
    private final KeyService keyService;
    private final LockService lock;

    // TODO break cyclic dependencies
    @Autowired
    ReplaceValue(
            final ValidationService validator,
            final ValueRepository repository,
            @Lazy final DimensionService dimensionService,
            @Lazy final KeyService keyService,
            final LockService lock) {
        this.validator = validator;
        this.repository = repository;
        this.dimensionService = dimensionService;
        this.keyService = keyService;
        this.lock = lock;
    }

    @Transactional
    public boolean replace(final String key, final Value value) {
        lock.onReplace(key, value);

        final ValueId id = new ValueId(key, value.getDimensions());
        @Nullable final Value current = repository.lock(id).orElse(null);

        validateDimensions(value);
        validateValue(key, value);

        if (current == null) {
            repository.create(key, value);
            return true;
        } else {
            repository.update(key, value);
            return false;
        }
    }

    private void validateDimensions(final Value value) {
        final ImmutableSet<String> dimensionIds = value.getDimensions().keySet();
        final List<Dimension> dimensions = dimensionService.readAll(dimensionIds);

        validator.validate(dimensions, value);
    }

    private void validateValue(final String keyId, final Value value) {
        final Key key = keyService.read(keyId);
        validator.validate(key, value);
    }

}
