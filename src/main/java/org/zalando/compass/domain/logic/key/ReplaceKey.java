package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.KeyRepository;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReplaceKey {

    private final ValidationService validator;
    private final KeyRepository repository;
    private final ValueService valueService;

    @Autowired
    ReplaceKey(
            final ValidationService validator,
            final KeyRepository repository,
            final ValueService valueService) {
        this.validator = validator;
        this.repository = repository;
        this.valueService = valueService;
    }

    /**
     *
     * @param key the key to replace
     * @return true if key was created, false if an existing one was updated
     */
    @Transactional
    public boolean replace(final Key key) {

        @Nullable final Key current = repository.lock(key.getId()).orElse(null);

        if (current == null) {
            repository.create(key);
            return true;
        } else {
            // TODO detect changes and validate accordingly
            // TODO lock values (order by id)

            validateValuesIfNecessary(current, key);
            repository.update(key);
            return false;
        }
    }

    private void validateValuesIfNecessary(final Key current, final Key next) {
        if (current.getSchema().equals(next.getSchema())) {
            return;
        }

        final List<Value> values = valueService.readAllByKey(next.getId());
        validator.validate(next, values);
    }

}
