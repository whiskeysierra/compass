package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.KeyRepository;

import javax.annotation.Nullable;
import java.util.List;

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
    @Transactional
    public boolean replace(final Key key) {
        final Locking.KeyLock lock = locking.lock(key);

        @Nullable final Key current = lock.getKey();

        if (current == null) {
            repository.create(key);
            return true;
        } else {
            // TODO detect changes and validate accordingly
            final List<Value> values = lock.getValues();

            validateValuesIfNecessary(current, key, values);
            repository.update(key);
            return false;
        }
    }

    private void validateValuesIfNecessary(final Key current, final Key next, final List<Value> values) {
        if (current.getSchema().equals(next.getSchema())) {
            return;
        }

        validator.validate(next, values);
    }

}
