package org.zalando.compass.domain.logic.value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;

@Component
class ReplaceValue {

    private final ValidationService validator;
    private final ValueRepository repository;
    private final Locking locking;

    @Autowired
    ReplaceValue(
            final ValidationService validator,
            final ValueRepository repository,
            final Locking locking) {
        this.validator = validator;
        this.repository = repository;
        this.locking = locking;
    }

    @Transactional
    public boolean replace(final String key, final Value value) {
        final Locking.ValueLock lock = locking.lock(key, value);
        @Nullable final Value current = lock.getValue();

        validator.validate(lock.getDimensions(), value);
        validator.validate(lock.getKey(), value);

        if (current == null) {
            repository.create(key, value);
            return true;
        } else {
            repository.update(key, value);
            return false;
        }
    }

}
