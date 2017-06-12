package org.zalando.compass.domain.logic.value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.List;

@Component
class ReplaceValues {

    private final ValueRepository repository;
    private final ValidationService validator;
    private final Locking locking;

    @Autowired
    ReplaceValues(
            final ValueRepository repository,
            final ValidationService validator,
            final Locking locking) {
        this.repository = repository;
        this.validator = validator;
        this.locking = locking;
    }

    @Transactional
    public void replace(final String key, final List<Value> values) {
        // TODO does it make sense to split this up into create, update + delete?

        repository.deleteByKey(key);

        final Locking.ValuesLock lock = locking.lock(key, values);

        validator.validate(lock.getDimensions(), values);
        validator.validate(lock.getKey(), values);

        // TODO batch create
        values.forEach(value ->
                repository.create(key, value));
    }

}
