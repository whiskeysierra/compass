package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.event.KeyDeleted;
import org.zalando.compass.core.domain.spi.event.EventPublisher;
import org.zalando.compass.core.domain.spi.repository.KeyRepository;

import javax.annotation.Nullable;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DeleteKey {

    private final KeyLocking locking;
    private final KeyRepository keyRepository;
    private final DeleteValue deleteValue;
    private final EventPublisher publisher;

    void delete(final Key key, @Nullable final String comment) {
        final KeyLock lock = locking.lock(key.getId());
        final List<Value> values = lock.getValues();

        deleteValues(key, values);
        deleteKey(key);

        publisher.publish(new KeyDeleted(key, values, comment));
    }

    private void deleteValues(final Key key, final List<Value> values) {
        values.forEach(value ->
                deleteValue.delete(key, value));
    }

    private void deleteKey(final Key key) {
        keyRepository.delete(key);
        log.info("Deleted key [{}]", key.getId());
    }

}
