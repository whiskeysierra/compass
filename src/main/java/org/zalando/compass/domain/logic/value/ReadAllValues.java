package org.zalando.compass.domain.logic.value;

import com.google.common.collect.ListMultimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.ValueCriteria;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.domain.persistence.ValueCriteria.byKeyPattern;
import static org.zalando.compass.domain.persistence.ValueCriteria.withoutCriteria;

@Component
class ReadAllValues {

    private final ValueRepository repository;

    @Autowired
    ReadAllValues(final ValueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ListMultimap<String, Value> readAll(@Nullable final String keyPattern) {
        final ValueCriteria criteria = keyPattern == null ? withoutCriteria() : byKeyPattern(keyPattern);
        final List<Value> values = repository.findAll(criteria);

        // TODO return ArrayListMultimap.create(index(values, Value::getKey));
        throw new UnsupportedOperationException();
    }

}
