package org.zalando.compass.domain.logic.dimension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

@Slf4j
@Component
class DeleteDimension {

    private final DimensionRepository repository;

    @Autowired
    DeleteDimension(final DimensionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void delete(final String id) {
        if (repository.delete(id)) {
            log.info("Deleted dimension [{}]", id);
        } else {
            throw new NotFoundException();
        }
    }

}
