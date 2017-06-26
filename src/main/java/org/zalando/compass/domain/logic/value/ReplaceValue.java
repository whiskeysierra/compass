package org.zalando.compass.domain.logic.value;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;

@Slf4j
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

    public boolean replace(final String key, final Value value) {
        final ValueLock lock = locking.lock(key, value);
        @Nullable final Value current = lock.getValue();

        validator.validate(lock.getDimensions(), value);
        validator.validate(lock.getKey(), value);

        if (current == null) {
            repository.create(key, value);
            log.info("Created value for key [{}]: [{}]", key, value);

            return true;
        } else {
            repository.update(key, value);
            log.info("Updated value for key [{}]: [{}]", key, value);

            return false;
        }
    }

}
