package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.persistence.KeyRepository;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReadAllKeys {

    private final KeyRepository repository;

    @Autowired
    ReadAllKeys(final KeyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    List<Key> read(@Nullable final String keyPattern) {
        return repository.findAll(keyPattern);
    }

}
