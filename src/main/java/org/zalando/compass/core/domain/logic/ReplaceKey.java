package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.EntityAlreadyExistsException;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.event.KeyCreated;
import org.zalando.compass.core.domain.model.event.KeyReplaced;
import org.zalando.compass.core.domain.spi.event.EventPublisher;
import org.zalando.compass.core.domain.spi.repository.KeyRepository;
import org.zalando.compass.core.domain.spi.validation.ValidationService;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.core.domain.logic.Changed.changed;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReplaceKey {

    private final KeyLocking locking;
    private final ValidationService validator;
    private final KeyRepository repository;
    private final EventPublisher publisher;

    /**
     *
     * @param key the key to replace
     * @param comment the revision comment
     * @return true if key was created, false if an existing one was updated
     */
    boolean replace(final Key key, @Nullable final String comment) {
        final KeyLock lock = locking.lock(key.getId());
        @Nullable final Key current = lock.getKey();
        final List<Value> values = lock.getValues();

        if (current == null) {
            create(key);

            publisher.publish(new KeyReplaced(null, key, comment));
            return true;
        } else {
            if (changed(Key::getSchema, current, key)) {
                validator.check(key, values);
            }

            repository.update(key);
            log.info("Updated key [{}]", key);

            publisher.publish(new KeyReplaced(current, key, comment));
            return false;
        }
    }

    void create(final Key key, @Nullable final String comment) {
        final KeyLock lock = locking.lock(key.getId());
        @Nullable final Key current = lock.getKey();

        if (current == null) {
            create(key);
            publisher.publish(new KeyCreated(key, comment));
        } else {
            throw new EntityAlreadyExistsException("Key " + key.getId() + " already exists");
        }
    }

    private void create(final Key key) {
        repository.create(key);
        log.info("Created key [{}]", key);
    }

}
