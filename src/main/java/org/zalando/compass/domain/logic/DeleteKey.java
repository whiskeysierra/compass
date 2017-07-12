package org.zalando.compass.domain.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyLock;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.KeyRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.domain.persistence.model.enums.RevisionType.DELETE;

@Slf4j
@Component
class DeleteKey {

    private final Locking locking;
    private final KeyRepository keyRepository;
    private final RevisionService revisionService;
    private final DeleteValue deleteValue;
    private final KeyRevisionRepository keyRevisionRepository;

    @Autowired
    DeleteKey(
            final Locking locking,
            final KeyRepository keyRepository,
            final RevisionService revisionService,
            final DeleteValue deleteValue,
            final KeyRevisionRepository keyRevisionRepository) {
        this.locking = locking;
        this.keyRepository = keyRepository;
        this.revisionService = revisionService;
        this.deleteValue = deleteValue;
        this.keyRevisionRepository = keyRevisionRepository;
    }

    void delete(final String id, @Nullable final String comment) {
        final KeyLock lock = locking.lockKey(id);

        @Nullable final Key key = lock.getKey();
        final List<Value> values = lock.getValues();

        if (key == null) {
            throw new NotFoundException();
        }

        final Revision revision = revisionService.create(comment).withType(DELETE);

        deleteValues(key, values, revision);
        deleteKey(key, revision);
    }

    private void deleteValues(final Key key, final List<Value> values, final Revision rev) {
        values.forEach(value ->
                deleteValue.delete(key.getId(), value, rev));
    }

    private void deleteKey(final Key key, final Revision rev) {
        keyRepository.delete(key.getId());
        log.info("Deleted key [{}]", key.getId());

        final KeyRevision revision = key.toRevision(rev);
        keyRevisionRepository.create(revision);
        log.info("Created key revision [{}]", revision);
    }

}
