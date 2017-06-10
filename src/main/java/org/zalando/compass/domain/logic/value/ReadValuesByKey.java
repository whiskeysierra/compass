package org.zalando.compass.domain.logic.value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.List;

import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

@Component
class ReadValuesByKey {

    private final ValueRepository repository;

    @Autowired
    ReadValuesByKey(final ValueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Value> read(final String key) {
        return repository.findAll(byKey(key));
    }

}
