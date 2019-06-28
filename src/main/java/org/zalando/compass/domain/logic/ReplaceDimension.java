package org.zalando.compass.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.BadArgumentException;
import org.zalando.compass.domain.EntityAlreadyExistsException;
import org.zalando.compass.domain.NotFoundException;
import org.zalando.compass.domain.RelationService;
import org.zalando.compass.domain.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionLock;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.repository.DimensionRepository;
import org.zalando.compass.domain.repository.DimensionRevisionRepository;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.domain.logic.Changed.changed;
import static org.zalando.compass.infrastructure.database.model.enums.RevisionType.CREATE;
import static org.zalando.compass.infrastructure.database.model.enums.RevisionType.UPDATE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReplaceDimension {

    private final Locking locking;
    private final RelationService relationService;
    private final ValidationService validator;
    private final DimensionRepository repository;
    private final RevisionService revisionService;
    private final DimensionRevisionRepository revisionRepository;

    /**
     *
     * @param dimension the dimension to replace
     * @param comment the revision comment
     * @return true if dimension was created, false if an existing one was updated
     */
    boolean replace(final Dimension dimension, @Nullable final String comment) {
        final DimensionLock lock = locking.lockDimension(dimension.getId());
        @Nullable final Dimension current = lock.getDimension();
        final List<Value> values = lock.getValues();

        final Revision revision = revisionService.create(comment);

        if (current == null) {
            create(dimension, revision);

            return true;
        } else {
            if (changed(Dimension::getSchema, current, dimension)) {
                validator.check(dimension, values);
            }

            if (changed(Dimension::getRelation, current, dimension)) {
                validateRelation(dimension);
            }

            repository.update(dimension);
            log.info("Updated dimension [{}]", dimension);

            final Revision update = revision.withType(UPDATE);
            final DimensionRevision dimensionRevision = dimension.toRevision(update);
            revisionRepository.create(dimensionRevision);
            log.info("Created dimension revision [{}]", dimensionRevision);

            return false;
        }
    }

    void create(final Dimension dimension, @Nullable final String comment) {
        final DimensionLock lock = locking.lockDimension(dimension.getId());
        @Nullable final Dimension current = lock.getDimension();

        final Revision revision = revisionService.create(comment);

        if (current == null) {
            create(dimension, revision);
        } else {
            throw new EntityAlreadyExistsException("Dimension " + dimension.getId() + " already exists");
        }
    }

    private void create(final Dimension dimension, final Revision revision) {
        validateRelation(dimension);

        repository.create(dimension);
        log.info("Created dimension [{}]", dimension);

        final DimensionRevision dimensionRevision = dimension.toRevision(revision.withType(CREATE));
        revisionRepository.create(dimensionRevision);
        log.info("Created dimension revision [{}]", dimensionRevision);
    }

    private void validateRelation(final Dimension dimension) {
        try {
            relationService.read(dimension.getRelation());
        } catch (final NotFoundException e) {
            throw new BadArgumentException(e);
        }
    }

}
