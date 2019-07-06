package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.event.DimensionDeleted;
import org.zalando.compass.core.domain.spi.event.EventPublisher;
import org.zalando.compass.core.domain.spi.repository.DimensionRepository;

import javax.annotation.Nullable;

import static org.zalando.compass.core.domain.api.BadArgumentException.checkArgument;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DeleteDimension {

    private final DimensionLocking locking;
    private final DimensionRepository repository;
    private final EventPublisher publisher;

    void delete(final Dimension dimension, @Nullable final String comment) {
        final var lock = locking.lock(dimension);

        checkArgument(lock.getValues().isEmpty(), "Dimension [%s] is still in use", dimension);

        repository.delete(dimension);
        log.info("Deleted dimension [{}]", dimension);

        publisher.publish(new DimensionDeleted(dimension, comment));
    }

}
