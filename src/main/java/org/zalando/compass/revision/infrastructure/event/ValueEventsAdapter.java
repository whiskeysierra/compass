package org.zalando.compass.revision.infrastructure.event;

import com.google.common.collect.MapDifference.ValueDifference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.event.KeyDeleted;
import org.zalando.compass.core.domain.model.event.ValueCreated;
import org.zalando.compass.core.domain.model.event.ValueDeleted;
import org.zalando.compass.core.domain.model.event.ValueReplaced;
import org.zalando.compass.core.domain.model.event.ValuesReplaced;
import org.zalando.compass.core.infrastructure.database.model.enums.RevisionType;
import org.zalando.compass.revision.domain.api.RevisionService;
import org.zalando.compass.revision.domain.api.ValueRevisionService;
import org.zalando.compass.revision.domain.model.ValueRevision;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.CREATE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.UPDATE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ValueEventsAdapter {

    private final RevisionService revisionService;
    private final ValueRevisionService valueRevisionService;

    @EventListener
    public void onValueCreated(final ValueCreated event) {
        final var key = event.getKey();
        final var value = event.getValue();
        final var revision = revisionService.create(event.getComment());

        createRevision(key, value, revision, CREATE);
    }

    @EventListener
    public void onValueReplaced(final ValueReplaced event) {
        final var key = event.getKey();
        @Nullable final var before = event.getBefore();
        final var after = event.getAfter();
        final var revision = revisionService.create(event.getComment());

        if (before == null) {
            createRevision(key, after, revision, CREATE);
        } else {
            createRevision(key, after, revision, UPDATE);
        }
    }

    @EventListener
    public void onValuesReplaced(final ValuesReplaced event) {
        final var key = event.getKey();
        final var creates = event.getCreates();
        final var updates = event.getUpdates();
        final var deletes = event.getDeletes();
        final var revision = revisionService.create(event.getComment());

        creates.forEach(value ->
                createRevision(key, value, revision, CREATE));

        updates.forEach(pair ->
                createRevision(key, pair.rightValue(), revision, UPDATE));

        deletes.forEach(value ->
                createRevision(key, value, revision, DELETE));
    }

    // TOOD there needs to be a cleaner way
    void onKeyDeleted(final Revision revision, final KeyDeleted event) {
        final var key = event.getKey();
        final var values = event.getValues();

        values.forEach(value ->
                createRevision(key, value, revision, DELETE));
    }

    @EventListener
    public void onValueDeleted(final ValueDeleted event) {
        final var key = event.getKey();
        final var value = event.getValue();
        final var revision = revisionService.create(event.getComment());

        createRevision(key, value, revision, DELETE);
    }

    private void createRevision(final Key key, final Value value, final Revision revision, final RevisionType type) {
        final var valueRevision = new ValueRevision(
                value.getDimensions(),
                value.getIndex(),
                revision.withType(type),
                value.getValue()
        );
        valueRevisionService.create(key, valueRevision);
        log.info("Created value revision [{}]", valueRevision);
    }

}
