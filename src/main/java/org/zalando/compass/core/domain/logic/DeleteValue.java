package org.zalando.compass.core.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.event.ValueDeleted;
import org.zalando.compass.core.domain.spi.event.EventPublisher;
import org.zalando.compass.core.domain.spi.repository.ValueRepository;

import javax.annotation.Nullable;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DeleteValue {

    private final ValueLocking locking;
    private final ValueRepository repository;
    private final EventPublisher publisher;

    void delete(final String keyId, final Map<Dimension, JsonNode> filter, @Nullable final String comment) {
        final ValueLock lock = locking.lock(keyId, filter);

        final Key key = lock.getKey();
        final Value value = lock.getValue();

        if (value == null) {
            throw new NotFoundException();
        }

        delete(key, value);

        publisher.publish(new ValueDeleted(key, value, comment));
    }

    void delete(final Key key, final Value value) {
        repository.delete(key.getId(), value.getDimensions());
        log.info("Deleted value [{}, {}]", key.getId(), value.getDimensions());
    }

}
