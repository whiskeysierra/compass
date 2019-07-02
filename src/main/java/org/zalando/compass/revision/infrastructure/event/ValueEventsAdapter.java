package org.zalando.compass.revision.infrastructure.event;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zalando.compass.kernel.domain.model.event.ValuesReplaced;
import org.zalando.compass.revision.domain.api.RevisionService;
import org.zalando.compass.kernel.domain.model.event.ValueCreated;
import org.zalando.compass.kernel.domain.model.event.ValueDeleted;
import org.zalando.compass.kernel.domain.model.event.ValueReplaced;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.kernel.domain.model.Value;
import org.zalando.compass.revision.domain.model.ValueRevision;
import org.zalando.compass.revision.domain.spi.repository.ValueRevisionRepository;
import org.zalando.compass.core.infrastructure.database.model.enums.RevisionType;

import javax.annotation.Nullable;

import java.util.Collection;

import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.CREATE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.UPDATE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ValueEventsAdapter {

    private final RevisionService service;
    // TODO should be service, not repository!
    private final ValueRevisionRepository repository;

    @EventListener
    public void onValueCreated(final ValueCreated event) {
        final Key key = event.getKey();
        final Value value = event.getValue();
        final Revision revision = service.create(event.getComment());

        createRevision(key, value, revision, CREATE);
    }

    @EventListener
    public void onValueReplaced(final ValueReplaced event) {
        final Key key = event.getKey();
        @Nullable final Value before = event.getBefore();
        final Value after = event.getAfter();
        final Revision revision = service.create(event.getComment());

        if (before == null) {
            createRevision(key, after, revision, CREATE);
        } else {
            createRevision(key, after, revision, UPDATE);
        }
    }

    @EventListener
    public void onValuesReplaced(final ValuesReplaced event) {
        final Key key = event.getKey();
        final Collection<Value> creates = event.getCreates();
        final Collection<ValueDifference<Value>> updates = event.getUpdates();
        final Collection<Value> deletes = event.getDeletes();
        final Revision revision = service.create(event.getComment());

        creates.forEach(value ->
                createRevision(key, value, revision, CREATE));

        updates.forEach(pair ->
                createRevision(key, pair.rightValue(), revision, UPDATE));

        deletes.forEach(value ->
                createRevision(key, value, revision, DELETE));
    }

    @EventListener
    public void onValueDeleted(final ValueDeleted event) {
        final Key key = event.getKey();
        final Value value = event.getValue();
        final Revision revision = service.create(event.getComment());

        createRevision(key, value, revision, DELETE);
    }

    private void createRevision(final Key key, final Value value, final Revision revision, final RevisionType type) {
        final ValueRevision valueRevision = value.toRevision(revision.withType(type));
        repository.create(key.getId(), valueRevision);
        log.info("Created value revision [{}]", valueRevision);
    }

}
