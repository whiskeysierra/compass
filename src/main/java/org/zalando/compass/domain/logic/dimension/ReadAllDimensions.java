package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.persistence.DimensionRepository;

import java.util.List;

@Component
class ReadAllDimensions {

    private final DimensionRepository repository;

    @Autowired
    ReadAllDimensions(final DimensionRepository repository) {
        this.repository = repository;
    }

    List<Dimension> read() {
        return repository.findAll();
    }

}
