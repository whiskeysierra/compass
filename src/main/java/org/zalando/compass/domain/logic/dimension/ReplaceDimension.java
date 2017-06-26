package org.zalando.compass.domain.logic.dimension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.zalando.compass.domain.logic.BadArgumentException;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionLock;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.List;

import static org.zalando.compass.library.Changed.changed;

@Slf4j
@Validated
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
    boolean replace(@Valid final Dimension dimension) {
        final DimensionLock lock = locking.lockDimensions(dimension.getId());
        @Nullable final Dimension current = lock.getDimension();

        // TODO make sure this is transactional
        if (current == null) {
            validateRelation(dimension);

            repository.create(dimension);
            log.info("Created dimension [{}]", dimension);

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
            log.info("Updated dimension [{}]", dimension);

            return false;
        }
    }

    private void validateRelation(final Dimension dimension) {
        final Relation relation = readRelation(dimension);
        validator.check(dimension, relation);
    }

    private Relation readRelation(final Dimension dimension) {
        try {
            return relationService.read(dimension.getRelation());
        } catch (final NotFoundException e) {
            throw new BadArgumentException(e);
        }
    }

}
