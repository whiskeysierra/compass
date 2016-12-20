package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Dimensions;
import org.zalando.compass.domain.persistence.DimensionRepository;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.springframework.dao.support.DataAccessUtils.singleResult;

@Service
public class DimensionService {

    private final DimensionRepository repository;

    @Autowired
    public DimensionService(final DimensionRepository repository) {
        this.repository = repository;
    }

    public boolean createOrUpdate(final Dimension dimension) throws IOException {
        if (repository.create(dimension)) {
            return true;
        }

        repository.update(dimension);
        return false;
    }

    @Nullable
    public Dimension read(final String id) {
        final List<Dimension> dimensions = repository.read(singleton(id));

        @Nullable final Dimension dimension = singleResult(dimensions);

        if (dimension == null) {
            throw new NotFoundException();
        }

        return dimension;
    }

    public Dimensions readAll() {
        return new Dimensions(repository.readAll());
    }

    public void reorder(final List<Dimension> dimensions) {
        repository.reorder(dimensions.stream()
                .map(Dimension::getId).collect(toList()));
    }

    public void delete(final String id) {
        if (!repository.delete(id)) {
            throw new NotFoundException();
        }
    }

}
