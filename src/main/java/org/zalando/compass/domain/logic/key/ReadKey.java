package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

@Component
class ReadKey {

    private final KeyRepository repository;

    @Autowired
    ReadKey(final KeyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    Key read(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

}
