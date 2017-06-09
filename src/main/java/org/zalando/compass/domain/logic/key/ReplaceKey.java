package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.List;

import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

@Component
class ReplaceKey {

    private final ValidationService validator;
    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;

    @Autowired
    ReplaceKey(
            final ValidationService validator,
            final KeyRepository repository,
            final ValueRepository valueRepository) {
        this.validator = validator;
        this.keyRepository = repository;
        this.valueRepository = valueRepository;
    }

    /**
     *
     * @param key the key to replace
     * @return true if key was created, false if an existing one was updated
     */
    @Transactional
    public boolean replace(final Key key) {
        if (keyRepository.create(key)) {
            return true;
        }

        validateAllValues(key);
        keyRepository.update(key);
        return false;
    }

    private void validateAllValues(final Key key) {
        final List<Value> values = valueRepository.findAll(byKey(key.getId()));
        validator.validate(key, values);
    }

}
