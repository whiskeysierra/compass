package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.persistence.DimensionRepository;

import java.util.List;
import java.util.Set;

@Component
class ReadDimensions {

    private final DimensionRepository repository;

    @Autowired
    ReadDimensions(final DimensionRepository repository) {
        this.repository = repository;
    }

    List<Dimension> read(final Set<String> ids) {
        return repository.findAll(ids);
    }

}
