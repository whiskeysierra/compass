package org.zalando.compass.domain.logic.key;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

@Slf4j
@Component
class DeleteKey {

    private final KeyRepository repository;

    @Autowired
    DeleteKey(final KeyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void delete(final String id) {
        if (repository.delete(id)) {
            log.info("Deleted key [{}]", id);
        } else {
            throw new NotFoundException();
        }
    }

}
