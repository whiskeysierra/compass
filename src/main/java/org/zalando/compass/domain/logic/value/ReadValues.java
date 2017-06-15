package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.List;
import java.util.Map;

import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

@Component
class ReadValues {

    private final ValueRepository repository;
    private final ValueSelector selector;

    @Autowired
    ReadValues(final ValueRepository repository, final ValueSelector selector) {
        this.repository = repository;
        this.selector = selector;
    }

    @Transactional(readOnly = true)
    public List<Value> read(final String key, final Map<String, JsonNode> filter) {
        final List<Value> values = repository.findAll(byKey(key));

        if (filter.isEmpty()) {
            // special case, just for read many values
            return values;
        }

        return selector.select(values, filter);
    }

}
