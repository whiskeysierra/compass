package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

@Component
class ReplaceDimension {

    private final ValidationService validator;
    private final DimensionRepository repository;
    private final RelationService relationService;
    private final Locking locking;

    @Autowired
    ReplaceDimension(
            final ValidationService validator,
            final DimensionRepository repository,
            final RelationService relationService,
            final Locking locking) {
        this.validator = validator;
        this.repository = repository;
        this.relationService = relationService;
        this.locking = locking;
    }

    /**
     *
     * @param dimension the dimension to replace
     * @return true if dimension was created, false if an existing one was updated
     */
    @Transactional
    public boolean replace(final Dimension dimension) {

        final Locking.DimensionLock lock = locking.lock(dimension);
        @Nullable final Dimension current = lock.getDimension();

        if (current == null) {
            validateRelation(dimension);

            repository.create(dimension);
            return true;
        } else {
            if (changed(Dimension::getSchema, current, dimension)) {
                final List<Value> values = lock.getValues();
                validator.validate(dimension, values);
            }

            if (changed(Dimension::getRelation, current, dimension)) {
                validateRelation(dimension);
            }

            repository.update(dimension);
            return false;
        }
    }

    private void validateRelation(final Dimension dimension) {
        // TODO should result in 400 Bad Request
        try {
            relationService.read(dimension.getRelation());
        } catch (final NotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private <T> boolean changed(final Function<Dimension, T> function, final Dimension previous, final Dimension next) {
        return !function.apply(previous).equals(function.apply(next));
    }

}
