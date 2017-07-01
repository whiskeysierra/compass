package org.zalando.compass.domain.logic.dimension;

import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
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
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.DimensionRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRevisionRepository;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.zalando.compass.domain.model.Revision.Type.CREATE;
import static org.zalando.compass.domain.model.Revision.Type.UPDATE;
import static org.zalando.compass.library.Changed.changed;

@Slf4j
@Validated
@Component
class ReplaceDimension {

    private final Locking locking;
    private final RelationService relationService;
    private final ValidationService validator;
    private final DimensionRepository repository;
    private final RevisionService revisionService;
    private final DimensionRevisionRepository revisionRepository;
    private final ValueRevisionRepository valueRevisionRepository;

    @Autowired
    ReplaceDimension(
            final Locking locking,
            final RelationService relationService,
            final ValidationService validator,
            final DimensionRepository repository,
            final RevisionService revisionService,
            final DimensionRevisionRepository revisionRepository,
            final ValueRevisionRepository valueRevisionRepository) {
        this.locking = locking;
        this.relationService = relationService;
        this.validator = validator;
        this.repository = repository;
        this.revisionService = revisionService;
        this.revisionRepository = revisionRepository;
        this.valueRevisionRepository = valueRevisionRepository;
    }

    /**
     *
     * @param dimension the dimension to replace
     * @return true if dimension was created, false if an existing one was updated
     */
    boolean replace(@Valid final Dimension dimension) {
        final DimensionLock lock = locking.lockDimensions(dimension.getId());
        @Nullable final Dimension current = lock.getDimension();
        final Multimap<String, Value> values = lock.getValues();

        // TODO expect comment
        final String comment = "..";

        // TODO make sure this is transactional
        if (current == null) {
            validateRelation(dimension);

            repository.create(dimension);
            log.info("Created dimension [{}]", dimension);

            final Revision rev = revisionService.create(CREATE, comment);
            final DimensionRevision revision = dimension.toRevision(rev);
            revisionRepository.create(revision);
            log.info("Created dimension revision [{}]", revision);

            return true;
        } else {
            if (changed(Dimension::getSchema, current, dimension)) {
                validator.validate(dimension, values.values());
            }

            if (changed(Dimension::getRelation, current, dimension)) {
                validateRelation(dimension);
            }

            repository.update(dimension);
            log.info("Updated dimension [{}]", dimension);

            final Revision rev = revisionService.create(null, comment);

            final DimensionRevision revision = dimension.toRevision(rev.withType(UPDATE));
            revisionRepository.create(revision);
            log.info("Created dimension revision [{}]", revision);

            values.forEach((key, value) -> {
                final ValueRevision valueRevision = value.toRevision(rev.withType(UPDATE));
                // TODO find key valueRevisionRepository.create(key, valueRevision);
                log.info("Created value revision [{}]", valueRevision);
            });

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
