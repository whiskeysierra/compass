package org.zalando.compass.domain.logic.dimension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RevisionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionLock;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;

import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;
import static org.zalando.compass.domain.model.Revision.Type.DELETE;

@Slf4j
@Component
class DeleteDimension {

    private final Locking locking;
    private final RevisionService revisionService;
    private final DimensionRepository repository;

    @Autowired
    DeleteDimension(final Locking locking, final RevisionService revisionService,
            final DimensionRepository repository) {
        this.revisionService = revisionService;
        this.repository = repository;
        this.locking = locking;
    }

    void delete(final String id) {
        final DimensionLock lock = locking.lockDimensions(id);

        @Nullable final Dimension dimension = lock.getDimension();

        if (dimension == null) {
            throw new NotFoundException();
        }

        checkArgument(lock.getValues().isEmpty(), "Dimension [%s] is still in use", id);

        // TODO expect comment
        final Revision revision = revisionService.create(DELETE, "..");

        repository.delete(dimension, revision);
        log.info("Deleted dimension [{}]", id);
    }

}
