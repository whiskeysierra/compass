package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.BadArgumentException;
import org.zalando.compass.core.domain.api.EntityAlreadyExistsException;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.api.RelationService;
import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.kernel.domain.model.Value;
import org.zalando.compass.kernel.domain.model.event.DimensionCreated;
import org.zalando.compass.kernel.domain.model.event.DimensionReplaced;
import org.zalando.compass.core.domain.spi.repository.DimensionRepository;
import org.zalando.compass.core.domain.spi.validation.ValidationService;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.core.domain.logic.Changed.changed;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReplaceDimension {

    private final DimensionLocking locking;
    private final RelationService relationService;
    private final ValidationService validator;
    private final DimensionRepository repository;
    private final RevisionService revisionService;

    // TODO how can we be spring independent?
    private final ApplicationEventPublisher publisher;

    /**
     *
     * @param dimension the dimension to replace
     * @param comment the revision comment
     * @return true if dimension was created, false if an existing one was updated
     */
    boolean replace(final Dimension dimension, @Nullable final String comment) {
        final DimensionLock lock = locking.lock(dimension.getId());
        @Nullable final Dimension current = lock.getDimension();
        final List<Value> values = lock.getValues();

        final Revision revision = revisionService.create(comment);

        if (current == null) {
            create(dimension);

            publisher.publishEvent(new DimensionReplaced(null, dimension, revision));
            return true;
        } else {
            if (changed(Dimension::getSchema, current, dimension)) {
                validator.check(dimension, values);
            }

            if (changed(Dimension::getRelation, current, dimension)) {
                validateRelation(dimension);
            }

            repository.update(dimension);
            log.info("Updated dimension [{}]", dimension);

            publisher.publishEvent(new DimensionReplaced(current, dimension, revision));
            return false;
        }
    }

    void create(final Dimension dimension, @Nullable final String comment) {
        final DimensionLock lock = locking.lock(dimension.getId());
        @Nullable final Dimension current = lock.getDimension();

        final Revision revision = revisionService.create(comment);

        if (current == null) {
            create(dimension);
            publisher.publishEvent(new DimensionCreated(dimension, revision));
        } else {
            throw new EntityAlreadyExistsException("Dimension " + dimension.getId() + " already exists");
        }
    }

    private void create(final Dimension dimension) {
        validateRelation(dimension);
        repository.create(dimension);
        log.info("Created dimension [{}]", dimension);
    }

    private void validateRelation(final Dimension dimension) {
        try {
            relationService.read(dimension.getRelation());
        } catch (final NotFoundException e) {
            throw new BadArgumentException(e);
        }
    }

}
