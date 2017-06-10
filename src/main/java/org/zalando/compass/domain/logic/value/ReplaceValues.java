package org.zalando.compass.domain.logic.value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.List;

@Component
class ReplaceValues {

    private final ReplaceValue replace;
    private final ValueRepository repository;

    @Autowired
    ReplaceValues(final ReplaceValue replace, final ValueRepository repository) {
        this.replace = replace;
        this.repository = repository;
    }

    // TODO optimize this
    @Transactional
    public void replace(final List<Value> values) {
        values.forEach(replace::replace);
        repository.update(values);
    }

}
