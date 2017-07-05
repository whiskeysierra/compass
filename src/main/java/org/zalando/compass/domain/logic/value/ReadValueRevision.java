package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRevisionRepository;

import java.util.List;
import java.util.Map;

@Component
class ReadValueRevision {

    private final ValueRevisionRepository repository;

    @Autowired
    ReadValueRevision(final ValueRevisionRepository repository) {
        this.repository = repository;
    }

    public List<Revision> readAll(final String key, final Map<String, JsonNode> filter) {
        return repository.findAll(key, filter);
    }

    public ValueRevision read(final String key, final Map<String, JsonNode> filter, final long revision) {
        return repository.find(key, filter, revision)
                .orElseThrow(NotFoundException::new);
    }
}
