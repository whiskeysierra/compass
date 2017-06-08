package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.RelationRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.zalando.compass.domain.persistence.ValueCriteria.byDimension;

@Service
public class DimensionService {

    private final SchemaValidator validator;
    private final RelationRepository relationRepository;
    private final DimensionRepository dimensionRepository;
    private final ValueRepository valueRepository;

    @Autowired
    public DimensionService(final SchemaValidator validator, final RelationRepository relationRepository,
            final DimensionRepository dimensionRepository,
            final ValueRepository valueRepository) {
        this.validator = validator;
        this.relationRepository = relationRepository;
        this.dimensionRepository = dimensionRepository;
        this.valueRepository = valueRepository;
    }

    // TODO require primitive dimension value type (schema)
    public boolean createOrUpdate(final Dimension dimension) throws IOException {
        verifyRelationExists(dimension);

        @Nullable final Dimension current = dimensionRepository.find(dimension.getId())
                .orElse(null);

        if (current == null) {
            if (dimensionRepository.create(dimension)) {
                return true;
            }
        } else {
            validateDimensionValuesIfNecessary(dimension, current);
            dimensionRepository.update(dimension);
        }

        return false;
    }

    private void verifyRelationExists(final Dimension dimension) throws IOException {
        // TODO 400 Bad Request
        checkArgument(relationRepository.exists(dimension.getRelation()),
                "Unknown relation '%s'", dimension.getRelation());
    }

    private void validateDimensionValuesIfNecessary(final Dimension next, final Dimension current) throws IOException {
        if (current.getSchema().equals(next.getSchema())) {
            return;
        }

        final List<Value> values = valueRepository.findAll(byDimension(next.getId()));
        validator.validate(next, values);
    }

    public void delete(final String id) throws IOException {
        dimensionRepository.delete(id);
    }

}
