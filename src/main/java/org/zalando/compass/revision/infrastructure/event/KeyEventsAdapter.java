package org.zalando.compass.revision.infrastructure.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.event.KeyCreated;
import org.zalando.compass.core.domain.model.event.KeyDeleted;
import org.zalando.compass.core.domain.model.event.KeyReplaced;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.revision.domain.api.RevisionService;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.revision.domain.model.ValueRevision;
import org.zalando.compass.revision.domain.spi.repository.KeyRevisionRepository;
import org.zalando.compass.core.infrastructure.database.model.enums.RevisionType;
import org.zalando.compass.revision.domain.spi.repository.ValueRevisionRepository;

import javax.annotation.Nullable;

import java.util.List;

import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.CREATE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.UPDATE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class KeyEventsAdapter {

    private final RevisionService service;
    private final KeyRevisionRepository keyRevisionRepository;
    private final ValueRevisionRepository valueRevisionRepository;

    @EventListener
    public void onKeyCreated(final KeyCreated event) {
        final Key key = event.getKey();
        final Revision revision = service.create(event.getComment());

        createRevision(key, revision, CREATE);
    }

    @EventListener
    public void onKeyReplaced(final KeyReplaced event) {
        final Revision revision = service.create(event.getComment());

        @Nullable final Key before = event.getBefore();
        final Key after = event.getAfter();

        if (before == null) {
            createRevision(after, revision, CREATE);
        } else {
            createRevision(after, revision, UPDATE);
        }
    }

    @EventListener
    public void onKeyDeleted(final KeyDeleted event) {
        final Revision revision = service.create(event.getComment());
        final Key key = event.getKey();
        final List<Value> values = event.getValues();

        values.forEach(value ->
                createRevision(key, value, revision, DELETE));

        createRevision(key, revision, DELETE);
    }

    private void createRevision(final Key key, final Revision revision, final RevisionType update) {
        final KeyRevision keyRevision = key.toRevision(revision.withType(update));
        keyRevisionRepository.create(keyRevision);
        log.info("Created key revision [{}]", keyRevision);
    }

    // TODO same as in ValueEventsAdapter
    private void createRevision(final Key key, final Value value, final Revision revision, final RevisionType type) {
        final ValueRevision valueRevision = value.toRevision(revision.withType(type));
        valueRevisionRepository.create(key.getId(), valueRevision);
        log.info("Created value revision [{}]", valueRevision);
    }

}
