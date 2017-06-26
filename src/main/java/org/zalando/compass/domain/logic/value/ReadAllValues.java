package org.zalando.compass.domain.logic.value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Component
class ReadAllValues {

    private final ValueRepository repository;

    @Autowired
    ReadAllValues(final ValueRepository repository) {
        this.repository = repository;
    }

    public Map<Key, List<Value>> readAll(@Nullable final String keyPattern) {
        return repository.listAll(keyPattern);
    }

}
