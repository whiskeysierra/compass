package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.RelationRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.zalando.compass.domain.persistence.ValueCriteria.byDimension;

@Component
class ReplaceDimension {

    private final ValidationService validator;
    private final DimensionRepository repository;

    // TODO don't use other repos here?
    private final RelationRepository relationRepository;
    private final ValueRepository valueRepository;

    @Autowired
    ReplaceDimension(
            final ValidationService validator,
            final DimensionRepository repository,
            final RelationRepository relationRepository,
            final ValueRepository valueRepository) {
        this.validator = validator;
        this.repository = repository;
        this.relationRepository = relationRepository;
        this.valueRepository = valueRepository;
    }

    /**
     *
     * @param dimension the dimension to replace
     * @return true if dimension was created, false if an existing one was updated
     */
    @Transactional
    public boolean replace(final Dimension dimension) {
        // TODO require primitive dimension value type (schema)
        verifyRelationExists(dimension);

        @Nullable final Dimension current = repository.lock(dimension.getId()).orElse(null);

        if (current == null) {
            return repository.create(dimension);
        } else {
            validateDimensionValuesIfNecessary(current, dimension);
            repository.update(dimension);
            return false;
        }
    }

    private void verifyRelationExists(final Dimension dimension) {
        // TODO 400 Bad Request
        checkArgument(relationRepository.exists(dimension.getRelation()),
                "Unknown relation '%s'", dimension.getRelation());
    }

    private void validateDimensionValuesIfNecessary(final Dimension current, final Dimension next) {
        if (current.getSchema().equals(next.getSchema())) {
            return;
        }

        final List<Value> values = valueRepository.findAll(byDimension(current.getId()));
        validator.validate(next, values);
    }

}
