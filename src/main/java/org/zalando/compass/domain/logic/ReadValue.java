package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Revisioned;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.List;
import java.util.Map;

import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

@Component
class ReadValue {

    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;
    private final ValueSelector selector;
    private final ReadValueRevision readRevision;

    @Autowired
    ReadValue(final KeyRepository keyRepository, final ValueRepository valueRepository,
            final ValueSelector selector, final ReadValueRevision readRevision) {
        this.keyRepository = keyRepository;
        this.valueRepository = valueRepository;
        this.selector = selector;
        this.readRevision = readRevision;
    }

    List<Value> readAll(final String key, final Map<String, JsonNode> filter) {
        final List<Value> values = valueRepository.findAll(byKey(key));

        if (values.isEmpty()) {
            // the fact that we can delay this check (foreign key constraint) should not be known to this layer...
            keyRepository.find(key).orElseThrow(NotFoundException::new);
            return values;
        }

        if (filter.isEmpty()) {
            // special case, just for reading many values
            return values;
        }

        return selector.select(values, filter);
    }

    Revisioned<Value> read(final String key, final Map<String, JsonNode> filter) {
        final List<Value> values = valueRepository.findAll(byKey(key));
        final List<Value> matched = selector.select(values, filter);

        return matched.stream()
                .findFirst()
                .map(value -> {
                    final Revision revision = readRevision.readLatestRevision(key, filter);
                    return Revisioned.create(value, revision);
                })
                .orElseThrow(NotFoundException::new);
    }

}
