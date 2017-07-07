package org.zalando.compass.domain.logic.key;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RevisionService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyLock;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.KeyRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;

import static org.zalando.compass.domain.persistence.model.enums.RevisionType.DELETE;

@Slf4j
@Component
class DeleteKey {

    private final Locking locking;
    private final KeyRepository repository;
    private final RevisionService revisionService;
    private final KeyRevisionRepository revisionRepository;

    @Autowired
    DeleteKey(
            final Locking locking,
            final KeyRepository repository,
            final RevisionService revisionService,
            final KeyRevisionRepository revisionRepository) {
        this.locking = locking;
        this.repository = repository;
        this.revisionService = revisionService;
        this.revisionRepository = revisionRepository;
    }

    void delete(final String id) {
        final KeyLock lock = locking.lockKey(id);

        @Nullable final Key key = lock.getKey();

        if (key == null) {
            throw new NotFoundException();
        }

        repository.delete(id);
        log.info("Deleted key [{}]", id);

        // TODO expect comment
        final String comment = "..";
        final Revision rev = revisionService.create(comment).withType(DELETE);
        final KeyRevision revision = key.toRevision(rev);
        revisionRepository.create(revision);
        log.info("Created key revision [{}]", revision);
    }

}
