package org.zalando.compass.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.api.NotFoundException;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.event.DimensionDeleted;
import org.zalando.compass.domain.spi.repository.DimensionRepository;

import javax.annotation.Nullable;

import static org.zalando.compass.domain.api.BadArgumentException.checkArgument;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DeleteDimension {

    private final DimensionLocking locking;
    private final DimensionRepository repository;
    private final RevisionService revisionService;
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

        final Revision revision = revisionService.create(comment);
        publisher.publishEvent(new DimensionDeleted(dimension, revision));
    }

}
