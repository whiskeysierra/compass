package org.zalando.compass.domain.logic.dimension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.BadArgumentException;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.logic.RevisionService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionLock;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.DimensionRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.domain.model.Revision.Type.CREATE;
import static org.zalando.compass.domain.model.Revision.Type.UPDATE;
import static org.zalando.compass.library.Changed.changed;

@Slf4j
@Component
class ReplaceDimension {

    private final Locking locking;
    private final RelationService relationService;
    private final ValidationService validator;
    private final DimensionRepository repository;
    private final RevisionService revisionService;
    private final DimensionRevisionRepository revisionRepository;

    @Autowired
    ReplaceDimension(
            final Locking locking,
            final RelationService relationService,
            final ValidationService validator,
            final DimensionRepository repository,
            final RevisionService revisionService,
            final DimensionRevisionRepository revisionRepository) {
        this.locking = locking;
        this.relationService = relationService;
        this.validator = validator;
        this.repository = repository;
        this.revisionService = revisionService;
        this.revisionRepository = revisionRepository;
    }

    /**
     *
     * @param dimension the dimension to replace
     * @return true if dimension was created, false if an existing one was updated
     */
    boolean replace(final Dimension dimension) {
        final DimensionLock lock = locking.lockDimensions(dimension.getId());
        @Nullable final Dimension current = lock.getDimension();
        final List<Value> values = lock.getValues();

        // TODO expect comment
        final String comment = "..";
        final Revision revision = revisionService.create(comment);

        // TODO make sure this is transactional
        if (current == null) {
            validateRelation(dimension);

            repository.create(dimension);
            log.info("Created dimension [{}]", dimension);

            final DimensionRevision dimensionRevision = dimension.toRevision(revision.withType(CREATE));
            revisionRepository.create(dimensionRevision);
            log.info("Created dimension revision [{}]", dimensionRevision);

            return true;
        } else {
            if (changed(Dimension::getSchema, current, dimension)) {
                validator.validate(dimension, values);
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

    private void validateRelation(final Dimension dimension) {
        final Relation relation = readRelation(dimension);
        validator.check(dimension, relation);
    }

    private Relation readRelation(final Dimension dimension) {
        try {
            return relationService.read(dimension.getRelation());
        } catch (final NotFoundException e) {
            throw new BadArgumentException(e);
        }
    }

}
