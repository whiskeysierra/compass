package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

@Component
class ReplaceKey {

    private final ValidationService validator;
    private final KeyRepository repository;
    private final ValueRepository valueRepository;

    @Autowired
    ReplaceKey(
            final ValidationService validator,
            final KeyRepository repository,
            final ValueRepository valueRepository) {
        this.validator = validator;
        this.repository = repository;
        this.valueRepository = valueRepository;
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

        final List<Value> values = valueRepository.findAll(byKey(next.getId()));
        validator.validate(next, values);
    }

}
