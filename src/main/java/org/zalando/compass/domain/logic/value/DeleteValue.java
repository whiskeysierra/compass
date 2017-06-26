package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.Map;

@Slf4j
@Component
class DeleteValue {

    private final ValueRepository repository;

    @Autowired
    DeleteValue(final ValueRepository repository) {
        this.repository = repository;
    }

    public void delete(final String key, final Map<String, JsonNode> filter) {
        if (repository.delete(key, filter)) {
            log.info("Deleted value [{}, {}]", key, filter);
        } else {
            throw new NotFoundException();
        }
    }

}
