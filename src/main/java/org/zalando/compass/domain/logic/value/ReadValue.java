package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRepository;
import org.zalando.compass.domain.persistence.ValueRevisionRepository;

import java.util.List;
import java.util.Map;

import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

@Component
class ReadValue {

    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;
    private final ValueSelector selector;
    private final ValueRevisionRepository revisionRepository;

    @Autowired
    ReadValue(final KeyRepository keyRepository, final ValueRepository valueRepository,
            final ValueSelector selector, final ValueRevisionRepository revisionRepository) {
        this.keyRepository = keyRepository;
        this.valueRepository = valueRepository;
        this.selector = selector;
        this.revisionRepository = revisionRepository;
    }

    Value read(final String key, final Map<String, JsonNode> filter) {
        final List<Value> values = valueRepository.findAll(byKey(key));
        final List<Value> matched = selector.select(values, filter);

        return matched.stream()
                .findFirst().orElseThrow(NotFoundException::new);
    }

    List<Value> readAll(final String key, final Map<String, JsonNode> filter) {
        final List<Value> values = valueRepository.findAll(byKey(key));

        if (values.isEmpty()) {
            // the fact that we can delay this check (foreign key constraint) should not be known to this layer...
            keyRepository.find(key).orElseThrow(NotFoundException::new);
            return values;
        }

        if (filter.isEmpty()) {
            // special case, just for read many values
            return values;
        }

        return selector.select(values, filter);
    }

    public List<Revision> readRevisions(final String key, final Map<String, JsonNode> filter) {
        return revisionRepository.findAll(key, filter);
    }

    public ValueRevision readRevision(final String key, final Map<String, JsonNode> filter, final long revision) {
        return revisionRepository.find(key, filter, revision)
                .orElseThrow(NotFoundException::new);
    }
}
