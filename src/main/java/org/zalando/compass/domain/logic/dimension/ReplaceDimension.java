package org.zalando.compass.domain.logic.dimension;

import com.networknt.schema.JsonType;
import com.networknt.schema.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.zalando.compass.library.Changed.changed;

@Component
class ReplaceDimension {

    private final ValidationService validator;
    private final DimensionRepository repository;
    private final RelationService relationService;
    private final Locking locking;

    @Autowired
    ReplaceDimension(
            final ValidationService validator,
            final DimensionRepository repository,
            final RelationService relationService,
            final Locking locking) {
        this.validator = validator;
        this.repository = repository;
        this.relationService = relationService;
        this.locking = locking;
    }

    /**
     *
     * @param dimension the dimension to replace
     * @return true if dimension was created, false if an existing one was updated
     */
    @Transactional
    public boolean replace(final Dimension dimension) {

        final Locking.DimensionLock lock = locking.lock(dimension);
        @Nullable final Dimension current = lock.getDimension();

        if (current == null) {
            validateRelation(dimension);

            repository.create(dimension);
            return true;
        } else {
            if (changed(Dimension::getSchema, current, dimension)) {
                final List<Value> values = lock.getValues();
                validator.validate(dimension, values);
            }

            if (changed(Dimension::getRelation, current, dimension)) {
                validateRelation(dimension);
            }

            repository.update(dimension);
            return false;
        }
    }

    private void validateRelation(final Dimension dimension) {
        final Relation relation = readRelation(dimension);

        final Set<JsonType> types = relation.supports();
        // TODO move this somewhere else?
        final JsonType type = TypeFactory.getSchemaNodeType(dimension.getSchema().get("type"));

        checkArgument(types.contains(type), "Relation '%s' only supports %s", relation, types);
    }

    private Relation readRelation(final Dimension dimension) {
        try {
            return relationService.read(dimension.getRelation());
        } catch (final NotFoundException e) {
            // TODO should result in 400 Bad Request
            throw new IllegalArgumentException(e);
        }
    }

}
