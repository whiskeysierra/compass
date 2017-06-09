package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.ValueId;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.Map;

@Component
class DeleteValue {

    private final ValueRepository repository;

    @Autowired
    DeleteValue(final ValueRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void delete(final String key, final Map<String, JsonNode> filter) {
        repository.delete(new ValueId(key, filter));
    }

}
