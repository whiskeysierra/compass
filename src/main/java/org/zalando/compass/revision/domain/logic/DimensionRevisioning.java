package org.zalando.compass.revision.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zalando.compass.kernel.domain.model.event.DimensionCreated;
import org.zalando.compass.kernel.domain.model.event.DimensionDeleted;
import org.zalando.compass.kernel.domain.model.event.DimensionReplaced;
import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.revision.domain.model.DimensionRevision;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.revision.domain.spi.repository.DimensionRevisionRepository;
import org.zalando.compass.core.infrastructure.database.model.enums.RevisionType;

import javax.annotation.Nullable;

import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.CREATE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.UPDATE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DimensionRevisioning {

    private final DimensionRevisionRepository repository;

    @EventListener
    public void onDimensionCreated(final DimensionCreated event) {
        final Dimension dimension = event.getDimension();
        final Revision revision = event.getRevision();

        createRevision(dimension, revision, CREATE);
    }

    @EventListener
    public void onDimensionReplaced(final DimensionReplaced event) {
        final Revision revision = event.getRevision();

        @Nullable final Dimension before = event.getBefore();
        final Dimension after = event.getAfter();

        if (before == null) {
            createRevision(after, revision, CREATE);
        } else {
            createRevision(after, revision, UPDATE);
        }
    }

    @EventListener
    public void ondDimensionDeleted(final DimensionDeleted event) {
        final Revision revision = event.getRevision();
        final Dimension dimension = event.getDimension();

        createRevision(dimension, revision, DELETE);
    }

    private void createRevision(final Dimension dimension, final Revision revision, final RevisionType update) {
        final DimensionRevision dimensionRevision = dimension.toRevision(revision.withType(update));
        repository.create(dimensionRevision);
        log.info("Created dimension revision [{}]", dimensionRevision);
    }

}
