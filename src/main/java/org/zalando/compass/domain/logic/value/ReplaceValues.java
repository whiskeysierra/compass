package org.zalando.compass.domain.logic.value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueId;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.List;

@Component
class ReplaceValues {

    private final ValueRepository repository;
    private final ReplaceValue replace;

    @Autowired
    ReplaceValues(final ValueRepository repository, final ReplaceValue replace) {
        this.repository = repository;
        this.replace = replace;
    }

    @Transactional
    public void replace(final String key, final List<Value> values) {
        // TODO repository.delete(byKey(key));
        values.forEach(value ->
            repository.delete(new ValueId(key, value.getDimensions())));

        // TODO batch create
        values.forEach(value ->
            replace.replace(key, value));
    }

}
