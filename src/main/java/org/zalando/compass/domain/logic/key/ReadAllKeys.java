package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.persistence.KeyRepository;

import java.util.List;

@Component
class ReadAllKeys {

    private final KeyRepository repository;

    @Autowired
    ReadAllKeys(final KeyRepository repository) {
        this.repository = repository;
    }

    List<Key> read() {
        return repository.findAll();
    }

}
