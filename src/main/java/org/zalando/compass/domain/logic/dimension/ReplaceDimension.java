package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.LockService;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReplaceDimension {

    private final ValidationService validator;
    private final DimensionRepository repository;
    private final RelationService relationService;
    private final ValueService valueService;
    private final LockService lock;

    // TODO break cyclic dependencies
    @Autowired
    ReplaceDimension(
            final ValidationService validator,
            final DimensionRepository repository,
            final RelationService relationService,
            @Lazy final ValueService valueService,
            final LockService lock) {
        this.validator = validator;
        this.repository = repository;
        this.relationService = relationService;
        this.valueService = valueService;
        this.lock = lock;
    }

    /**
     *
     * @param dimension the dimension to replace
     * @return true if dimension was created, false if an existing one was updated
     */
    @Transactional
    public boolean replace(final Dimension dimension) {
        verifyRelationExists(dimension);

        @Nullable final Dimension current = repository.lock(dimension.getId()).orElse(null);

        if (current == null) {
            repository.create(dimension);
            return true;
        } else {
            // TODO detect changes and validate accordingly
            lock.onUpdate(dimension);

            validateDimensionValuesIfNecessary(current, dimension);
            repository.update(dimension);
            return false;
        }
    }

    private void verifyRelationExists(final Dimension dimension) {
        // TODO should result in 400 Bad Request
        try {
            relationService.read(dimension.getRelation());
        } catch (final NotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void validateDimensionValuesIfNecessary(final Dimension current, final Dimension next) {
        if (current.getSchema().equals(next.getSchema())) {
            return;
        }

        final List<Value> values = valueService.readAllByDimension(current.getId());
        validator.validate(next, values);
    }

}
