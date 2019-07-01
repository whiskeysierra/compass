package org.zalando.compass.core.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.kernel.domain.model.Value;
import org.zalando.compass.kernel.domain.model.event.ValueDeleted;
import org.zalando.compass.core.domain.spi.repository.ValueRepository;

import javax.annotation.Nullable;
import java.util.Map;

import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DeleteValue {

    private final ValueLocking locking;
    private final ValueRepository repository;
    private final RevisionService revisionService;
    private final ApplicationEventPublisher publisher;

    void delete(final String keyId, final Map<String, JsonNode> filter, @Nullable final String comment) {
        final ValueLock lock = locking.lock(keyId, filter);

        final Key key = lock.getKey();
        final Value value = lock.getValue();

        if (value == null) {
            throw new NotFoundException();
        }

        final Revision rev = revisionService.create(comment).withType(DELETE);

        delete(key, value, rev);
    }

    void delete(final Key key, final Value value, final Revision rev) {
        repository.delete(key.getId(), value.getDimensions());
        log.info("Deleted value [{}, {}]", key.getId(), value.getDimensions());
        publisher.publishEvent(new ValueDeleted(key, value, rev));
    }

}
