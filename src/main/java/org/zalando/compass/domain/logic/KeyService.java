package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.ValueRepository;
import org.zalando.compass.domain.persistence.model.tables.pojos.KeyRow;
import org.zalando.compass.domain.persistence.model.tables.pojos.ValueRow;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
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
        final KeyRow row = key.toRow();

        if (keyRepository.create(row)) {
            return true;
        }

        validateAllValues(key);
        keyRepository.update(row);
        return false;
    }

    private void validateAllValues(final Key key) throws IOException {
        final List<ValueRow> rows = valueRepository.findAll(byKey(key.getId()));
        validator.validate(key, rows.stream().map(Value::fromRow).collect(toList()));
    }

    public void delete(final String id) {
        keyRepository.delete(id);
    }

}
