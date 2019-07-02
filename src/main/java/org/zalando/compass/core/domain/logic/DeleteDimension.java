package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.spi.repository.DimensionRepository;
import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.kernel.domain.model.event.DimensionDeleted;

import javax.annotation.Nullable;

import static org.zalando.compass.core.domain.api.BadArgumentException.checkArgument;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DeleteDimension {

    private final DimensionLocking locking;
    private final DimensionRepository repository;
    private final ApplicationEventPublisher publisher;

    void delete(final String id, @Nullable final String comment) {
        final DimensionLock lock = locking.lock(id);

        @Nullable final Dimension dimension = lock.getDimension();

        if (dimension == null) {
            throw new NotFoundException();
        }

        checkArgument(lock.getValues().isEmpty(), "Dimension [%s] is still in use", id);

        repository.delete(dimension);
        log.info("Deleted dimension [{}]", id);

        publisher.publishEvent(new DimensionDeleted(dimension, comment));
    }

}
