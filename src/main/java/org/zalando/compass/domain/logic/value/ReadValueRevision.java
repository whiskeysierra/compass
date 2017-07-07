package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRevisionRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
class ReadValueRevision {

    private final ValueRevisionRepository repository;
    private final ValueSelector selector;

    @Autowired
    ReadValueRevision(final ValueRevisionRepository repository, final ValueSelector selector) {
        this.repository = repository;
        this.selector = selector;
    }

    public List<Revision> readPageRevisions(final String key) {
        return repository.findPageRevisions(key).stream()
                .map(Revision::withTypeUpdate)
                .collect(toList());
    }

    public PageRevision<Value> readPageAt(final String key, final Map<String, JsonNode> filter, final long revision) {
        final PageRevision<Value> page = repository.findPage(key, revision, true)
                .orElseThrow(NotFoundException::new)
                .withRevisionTypeUpdate()
                .map(ValueRevision::toValue);

        if (filter.isEmpty()) {
            // special case, just for reading many values
            return page;
        }

        return page.withElements(selector.select(page.getElements(), filter));
    }

    public List<Revision> readRevisions(final String key, final Map<String, JsonNode> dimensions) {
        return repository.findRevisions(key, dimensions);
    }

    public ValueRevision readAt(final String key, final Map<String, JsonNode> dimensions, final long revision) {
        final PageRevision<ValueRevision> page = repository.findPage(key, revision, false)
                .orElseThrow(NotFoundException::new);

        final List<ValueRevision> matched = selector.select(page.getElements(), dimensions);

        return matched.stream()
                .findFirst().orElseThrow(NotFoundException::new);
    }
}
