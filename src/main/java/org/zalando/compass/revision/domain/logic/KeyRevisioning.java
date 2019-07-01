package org.zalando.compass.revision.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zalando.compass.kernel.domain.model.event.KeyCreated;
import org.zalando.compass.kernel.domain.model.event.KeyDeleted;
import org.zalando.compass.kernel.domain.model.event.KeyReplaced;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.revision.domain.spi.repository.KeyRevisionRepository;
import org.zalando.compass.core.infrastructure.database.model.enums.RevisionType;

import javax.annotation.Nullable;

import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.CREATE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.UPDATE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class KeyRevisioning {

    private final KeyRevisionRepository repository;

    @EventListener
    public void onKeyCreated(final KeyCreated event) {
        final Key key = event.getKey();
        final Revision revision = event.getRevision();

        createRevision(key, revision, CREATE);
    }

    @EventListener
    public void onKeyReplaced(final KeyReplaced event) {
        final Revision revision = event.getRevision();

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
        final Revision revision = event.getRevision();
        final Key key = event.getKey();

        createRevision(key, revision, DELETE);
    }

    private void createRevision(final Key key, final Revision revision, final RevisionType update) {
        final KeyRevision keyRevision = key.toRevision(revision.withType(update));
        repository.create(keyRevision);
        log.info("Created key revision [{}]", keyRevision);
    }

}
