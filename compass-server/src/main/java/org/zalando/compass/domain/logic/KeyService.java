package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Keys;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.singleton;
import static org.springframework.dao.support.DataAccessUtils.singleResult;

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

    private void validateAllValues(final Key key) {
        validator.validate(key, valueRepository.readAllByKey(key.getId()));
    }

    public Key read(final String id) {
        final List<Key> keys = keyRepository.read(singleton(id));

        @Nullable final Key key = singleResult(keys);

        if (key == null) {
            throw new NotFoundException();
        }

        return key;
    }

    public Keys readAll() {
        return new Keys(keyRepository.readAll());
    }

    public boolean exists(final String id) {
        return keyRepository.exists(id);
    }

    public void delete(final String id) {
        if (!keyRepository.delete(id)) {
            throw new NotFoundException();
        }
    }

}
