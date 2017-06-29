package org.zalando.compass.domain.logic.key;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RevisionService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyLock;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.KeyRevisionRepository;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.domain.model.Revision.Type.CREATE;
import static org.zalando.compass.domain.model.Revision.Type.UPDATE;
import static org.zalando.compass.library.Changed.changed;

@Slf4j
@Component
class ReplaceKey {

    private final Locking locking;
    private final ValidationService validator;
    private final KeyRepository repository;
    private final RevisionService revisionService;
    private final KeyRevisionRepository revisionRepository;

    @Autowired
    ReplaceKey(
            final Locking locking,
            final ValidationService validator,
            final KeyRepository repository,
            final RevisionService revisionService,
            final KeyRevisionRepository revisionRepository) {
        this.locking = locking;
        this.validator = validator;
        this.repository = repository;
        this.revisionService = revisionService;
        this.revisionRepository = revisionRepository;
    }

    /**
     *
     * @param key the key to replace
     * @return true if key was created, false if an existing one was updated
     */
    boolean replace(final Key key) {
        final KeyLock lock = locking.lockKey(key.getId());
        @Nullable final Key current = lock.getKey();

        // TODO expect comment
        final String comment = "..";

        // TODO make sure this is transactional
        if (current == null) {

            repository.create(key);
            log.info("Created key [{}]", key);

            final Revision rev = revisionService.create(CREATE, comment);
            final KeyRevision revision = key.toRevision(rev);
            revisionRepository.create(revision);
            log.info("Created key revision [{}]", revision);

            return true;
        } else {
            if (changed(Key::getSchema, current, key)) {
                final List<Value> values = lock.getValues();
                validator.check(key, values);
            }

            repository.update(key);
            log.info("Updated key [{}]", key);

            final Revision rev = revisionService.create(UPDATE, comment);
            final KeyRevision revision = key.toRevision(rev);
            revisionRepository.create(revision);
            log.info("Created key revision [{}]", revision);

            return false;
        }
    }

}
