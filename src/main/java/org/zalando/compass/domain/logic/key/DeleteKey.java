package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

@Component
class DeleteKey {

    private final KeyRepository repository;

    @Autowired
    DeleteKey(final KeyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void delete(final String id) {
        if (!repository.delete(id)) {
            throw new NotFoundException();
        }
    }

}
