package org.zalando.compass.domain.logic.key;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyLock;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.KeyRepository;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.library.Changed.changed;

@Slf4j
@Component
class ReplaceKey {

    private final ValidationService validator;
    private final KeyRepository repository;
    private final Locking locking;

    @Autowired
    ReplaceKey(
            final ValidationService validator,
            final KeyRepository repository,
            final Locking locking) {
        this.validator = validator;
        this.repository = repository;
        this.locking = locking;
    }

    /**
     *
     * @param key the key to replace
     * @return true if key was created, false if an existing one was updated
     */
    public boolean replace(final Key key) {
        final KeyLock lock = locking.lockKey(key.getId());

        @Nullable final Key current = lock.getKey();

        // TODO make sure this is transactional
        if (current == null) {
            repository.create(key);
            log.info("Created key [{}]", key);

            return true;
        } else {
            if (changed(Key::getSchema, current, key)) {
                final List<Value> values = lock.getValues();
                validator.check(key, values);
            }

            repository.update(key);
            log.info("Updated key [{}]", key);

            return false;
        }
    }

}
