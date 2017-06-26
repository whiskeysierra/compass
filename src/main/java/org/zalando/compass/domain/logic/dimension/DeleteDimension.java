package org.zalando.compass.domain.logic.dimension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.model.DimensionLock;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;

@Slf4j
@Component
class DeleteDimension {

    private final Locking locking;
    private final DimensionRepository repository;

    @Autowired
    DeleteDimension(final Locking locking, final DimensionRepository repository) {
        this.repository = repository;
        this.locking = locking;
    }

    public void delete(final String id) {
        final DimensionLock lock = locking.lockDimensions(id);

        if (lock.getDimension() == null) {
            throw new NotFoundException();
        }

        checkArgument(lock.getValues().isEmpty(), "Dimension [%s] is still in use", id);

        repository.delete(id);
        log.info("Deleted dimension [{}]", id);
    }

}
