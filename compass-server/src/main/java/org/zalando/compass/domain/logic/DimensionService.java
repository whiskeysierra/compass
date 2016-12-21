package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Dimensions;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.springframework.dao.support.DataAccessUtils.singleResult;

@Service
public class DimensionService {

    private final SchemaValidator validator;
    private final DimensionRepository dimensionRepository;
    private final ValueRepository valueRepository;

    @Autowired
    public DimensionService(final SchemaValidator validator, final DimensionRepository dimensionRepository,
            final ValueRepository valueRepository) {
        this.validator = validator;
        this.dimensionRepository = dimensionRepository;
        this.valueRepository = valueRepository;
    }

    public boolean createOrUpdate(final Dimension dimension) throws IOException {
        // TODO validate relation

        if (dimensionRepository.create(dimension)) {
            return true;
        }

        validateDimensionValues(dimension);
        dimensionRepository.update(dimension);
        return false;
    }

    private void validateDimensionValues(final Dimension dimension) {
        validator.validate(dimension, valueRepository.readAllByDimension(dimension.getId()));
    }

    @Nullable
    public Dimension read(final String id) {
        final List<Dimension> dimensions = dimensionRepository.read(singleton(id));

        @Nullable final Dimension dimension = singleResult(dimensions);

        if (dimension == null) {
            throw new NotFoundException();
        }

        return dimension;
    }

    public Dimensions readAll() {
        return new Dimensions(dimensionRepository.readAll());
    }

    public void reorder(final List<Dimension> dimensions) {
        dimensionRepository.reorder(dimensions.stream()
                .map(Dimension::getId).collect(toList()));
    }

    public void delete(final String id) {
        if (!dimensionRepository.delete(id)) {
            throw new NotFoundException();
        }
    }

}
