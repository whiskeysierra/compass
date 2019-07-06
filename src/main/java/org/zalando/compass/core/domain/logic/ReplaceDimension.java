package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.EntityAlreadyExistsException;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.event.DimensionCreated;
import org.zalando.compass.core.domain.model.event.DimensionReplaced;
import org.zalando.compass.core.domain.spi.event.EventPublisher;
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
    private final ValidationService validator;
    private final DimensionRepository repository;
    private final EventPublisher publisher;

    /**
     *
     * @param dimension the dimension to replace
     * @param comment the revision comment
     * @return true if dimension was created, false if an existing one was updated
     */
    boolean replace(final Dimension dimension, @Nullable final String comment) {
        final var lock = locking.lock(dimension);
        @Nullable final var current = lock.getDimension();
        final var values = lock.getValues();

        if (current == null) {
            create(dimension);

            publisher.publish(new DimensionReplaced(null, dimension, comment));
            return true;
        } else {
            if (changed(Dimension::getSchema, current, dimension)) {
                validator.check(dimension, values);
            }

            repository.update(dimension);
            log.info("Updated dimension [{}]", dimension);

            publisher.publish(new DimensionReplaced(current, dimension, comment));
            return false;
        }
    }

    void create(final Dimension dimension, @Nullable final String comment) {
        final var lock = locking.lock(dimension);
        @Nullable final var current = lock.getDimension();

        if (current == null) {
            create(dimension);
            publisher.publish(new DimensionCreated(dimension, comment));
        } else {
            throw new EntityAlreadyExistsException("Dimension " + dimension.getId() + " already exists");
        }
    }

    private void create(final Dimension dimension) {
        repository.create(dimension);
        log.info("Created dimension [{}]", dimension);
    }

}
