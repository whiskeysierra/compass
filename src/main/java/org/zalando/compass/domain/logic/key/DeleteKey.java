package org.zalando.compass.domain.logic.key;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.model.KeyLock;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

@Slf4j
@Component
class DeleteKey {

    private final Locking locking;
    private final KeyRepository repository;

    @Autowired
    DeleteKey(final Locking locking, final KeyRepository repository) {
        this.locking = locking;
        this.repository = repository;
    }

    public void delete(final String id) {
        final KeyLock lock = locking.lockKey(id);

        if (lock.getKey() == null) {
            throw new NotFoundException();
        }

        repository.delete(id);
        log.info("Deleted key [{}]", id);
    }

}
