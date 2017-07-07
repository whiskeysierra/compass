package org.zalando.compass.domain.logic.dimension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RevisionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionLock;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.DimensionRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;

import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;
import static org.zalando.compass.domain.persistence.model.enums.RevisionType.DELETE;

@Slf4j
@Component
class DeleteDimension {

    private final Locking locking;
    private final DimensionRepository repository;
    private final RevisionService revisionService;
    private final DimensionRevisionRepository revisionRepository;

    @Autowired
    DeleteDimension(
            final Locking locking,
            final DimensionRepository repository,
            final RevisionService revisionService,
            final DimensionRevisionRepository revisionRepository) {
        this.locking = locking;
        this.repository = repository;
        this.revisionService = revisionService;
        this.revisionRepository = revisionRepository;
    }

    void delete(final String id) {
        final DimensionLock lock = locking.lockDimensions(id);

        @Nullable final Dimension dimension = lock.getDimension();

        if (dimension == null) {
            throw new NotFoundException();
        }

        checkArgument(lock.getValues().isEmpty(), "Dimension [%s] is still in use", id);

        repository.delete(dimension);
        log.info("Deleted dimension [{}]", id);

        // TODO expect comment
        final String comment = "..";
        final Revision rev = revisionService.create(comment).withType(DELETE);
        final DimensionRevision revision = dimension.toRevision(rev);
        revisionRepository.create(revision);
        log.info("Created dimension revision [{}]", revision);
    }

}
