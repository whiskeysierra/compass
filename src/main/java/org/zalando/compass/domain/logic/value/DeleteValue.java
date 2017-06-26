package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.Map;

@Slf4j
@Component
class DeleteValue {

    private final Locking locking;
    private final ValueRepository repository;

    @Autowired
    DeleteValue(final Locking locking, final ValueRepository repository) {
        this.locking = locking;
        this.repository = repository;
    }

    void delete(final String key, final Map<String, JsonNode> filter) {
        final ValueLock lock = locking.lockValue(key, filter);

        if (lock.getValue() == null) {
            throw new NotFoundException();
        }

        repository.delete(key, filter);
        log.info("Deleted value [{}, {}]", key, filter);
    }

}
