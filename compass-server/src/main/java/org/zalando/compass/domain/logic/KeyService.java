package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.io.IOException;

import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

@Service
public class KeyService {

    private final SchemaValidator validator;
    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;

    @Autowired
    public KeyService(final SchemaValidator validator, final KeyRepository keyRepository,
            final ValueRepository valueRepository) {
        this.validator = validator;
        this.keyRepository = keyRepository;
        this.valueRepository = valueRepository;
    }

    public boolean createOrUpdate(final Key key) throws IOException {
        if (keyRepository.create(key)) {
            return true;
        }

        validateAllValues(key);
        keyRepository.update(key);
        return false;
    }

    private void validateAllValues(final Key key) throws IOException {
        validator.validate(key, valueRepository.findAll(byKey(key.getId())));
    }

    public void delete(final String id) {
        keyRepository.delete(id);
    }

}
