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
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.KeyRevisionRepository;
import org.zalando.compass.domain.persistence.ValueRevisionRepository;

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
    private final ValueRevisionRepository valueRevisionRepository;

    @Autowired
    ReplaceKey(
            final Locking locking,
            final ValidationService validator,
            final KeyRepository repository,
            final RevisionService revisionService,
            final KeyRevisionRepository revisionRepository,
            final ValueRevisionRepository valueRevisionRepository) {
        this.locking = locking;
        this.validator = validator;
        this.repository = repository;
        this.revisionService = revisionService;
        this.revisionRepository = revisionRepository;
        this.valueRevisionRepository = valueRevisionRepository;
    }

    /**
     *
     * @param key the key to replace
     * @return true if key was created, false if an existing one was updated
     */
    boolean replace(final Key key) {
        final KeyLock lock = locking.lockKey(key.getId());
        @Nullable final Key current = lock.getKey();
        final List<Value> values = lock.getValues();

        // TODO expect comment
        final String comment = "..";
        final Revision revision = revisionService.create(comment);

        // TODO make sure this is transactional
        if (current == null) {

            repository.create(key);
            log.info("Created key [{}]", key);

            final KeyRevision keyRevision = key.toRevision(revision.withType(CREATE));
            revisionRepository.create(keyRevision);
            log.info("Created key revision [{}]", keyRevision);

            return true;
        } else {
            if (changed(Key::getSchema, current, key)) {
                validator.check(key, values);
            }

            repository.update(key);
            log.info("Updated key [{}]", key);

            final Revision update = revision.withType(UPDATE);
            final KeyRevision keyRevision = key.toRevision(update);
            revisionRepository.create(keyRevision);
            log.info("Created key revision [{}]", keyRevision);

            return false;
        }
    }

}
