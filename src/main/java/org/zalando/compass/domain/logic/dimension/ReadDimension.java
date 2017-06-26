package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

@Component
class ReadDimension {

    private final DimensionRepository repository;

    @Autowired
    ReadDimension(final DimensionRepository repository) {
        this.repository = repository;
    }

    Dimension read(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

}
