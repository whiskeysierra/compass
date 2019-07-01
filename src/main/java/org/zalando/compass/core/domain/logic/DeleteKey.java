package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.kernel.domain.model.Value;
import org.zalando.compass.kernel.domain.model.event.KeyDeleted;
import org.zalando.compass.core.domain.spi.repository.KeyRepository;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DeleteKey {

    private final KeyLocking locking;
    private final KeyRepository keyRepository;
    private final RevisionService revisionService;
    private final DeleteValue deleteValue;
    private final ApplicationEventPublisher publisher;

    void delete(final String id, @Nullable final String comment) {
        final KeyLock lock = locking.lock(id);

        @Nullable final Key key = lock.getKey();
        final List<Value> values = lock.getValues();

        if (key == null) {
            throw new NotFoundException();
        }

        final Revision revision = revisionService.create(comment);

        deleteValues(key, values, revision);
        deleteKey(key, revision);
    }

    private void deleteValues(final Key key, final List<Value> values, final Revision revision) {
        values.forEach(value ->
                // TODO there should be a better way to share this
                deleteValue.delete(key, value, revision.withType(DELETE)));
    }

    private void deleteKey(final Key key, final Revision revision) {
        keyRepository.delete(key);
        log.info("Deleted key [{}]", key.getId());

        publisher.publishEvent(new KeyDeleted(key, revision));
    }

}