package org.zalando.compass.revision.infrastructure.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.model.event.KeyCreated;
import org.zalando.compass.core.domain.model.event.KeyDeleted;
import org.zalando.compass.core.domain.model.event.KeyReplaced;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.revision.domain.api.KeyRevisionService;
import org.zalando.compass.revision.domain.api.RevisionService;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.infrastructure.database.model.enums.RevisionType;

import javax.annotation.Nullable;

import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.CREATE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.UPDATE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class KeyEventsAdapter {

    private final RevisionService revisionService;
    private final KeyRevisionService keyRevisionService;
    private final ValueEventsAdapter adapter;

    @EventListener
    public void onKeyCreated(final KeyCreated event) {
        final Key key = event.getKey();
        final Revision revision = revisionService.create(event.getComment());

        createRevision(key, revision, CREATE);
    }

    @EventListener
    public void onKeyReplaced(final KeyReplaced event) {
        final Revision revision = revisionService.create(event.getComment());

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
        final Revision revision = revisionService.create(event.getComment());
        final Key key = event.getKey();

        // TODO find a better way to delegate/share that
        adapter.onKeyDeleted(revision, event);
        createRevision(key, revision, DELETE);
    }

    private void createRevision(final Key key, final Revision revision, final RevisionType update) {
        final KeyRevision keyRevision = new KeyRevision(
                key.getId(),
                revision.withType(update),
                key.getSchema(),
                key.getDescription()
        );
        keyRevisionService.create(keyRevision);
        log.info("Created key revision [{}]", keyRevision);
    }

}
