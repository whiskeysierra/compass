package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.persistence.DimensionRepository;

@Component
class DeleteDimension {

    private final DimensionRepository repository;

    @Autowired
    DeleteDimension(final DimensionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void delete(final String id) {
        repository.delete(id);
    }

}
