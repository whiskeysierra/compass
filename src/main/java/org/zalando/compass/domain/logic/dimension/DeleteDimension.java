package org.zalando.compass.domain.logic.dimension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueCriteria;
import org.zalando.compass.domain.persistence.ValueRepository;

import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;

@Slf4j
@Component
class DeleteDimension {

    private final DimensionRepository repository;
    private final ValueRepository valueRepository;

    @Autowired
    DeleteDimension(final DimensionRepository repository,
            final ValueRepository valueRepository) {
        this.repository = repository;
        this.valueRepository = valueRepository;
    }

    public void delete(final String id) {
        checkArgument(valueRepository.findAll(ValueCriteria.byDimension(id)).isEmpty(),
                "Dimension [%s] is still in use", id);

        if (repository.delete(id)) {
            log.info("Deleted dimension [{}]", id);
        } else {
            throw new NotFoundException();
        }
    }

}
