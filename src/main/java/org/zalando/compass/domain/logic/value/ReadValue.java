package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.List;
import java.util.Map;

import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

@Component
class ReadValue {

    private final ValueRepository repository;
    private final ValueMatcher matcher;

    @Autowired
    ReadValue(final ValueRepository repository, final ValueMatcher matcher) {
        this.repository = repository;
        this.matcher = matcher;
    }

    @Transactional(readOnly = true)
    public Value read(final String key, final Map<String, JsonNode> filter) {
        final List<Value> values = repository.findAll(byKey(key));
        final List<Value> matched = matcher.match(values, filter);

        return matched.stream()
                .findFirst().orElseThrow(NotFoundException::new);
    }

}
