package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.NotFoundException;
import org.zalando.compass.domain.ValueRevisionService;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Revisioned;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.repository.KeyRepository;
import org.zalando.compass.domain.repository.ValueRepository;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import java.util.List;
import java.util.Map;

import static org.zalando.compass.domain.repository.ValueCriteria.byKey;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReadValue {

    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;
    private final ValueRevisionService revisionService;
    private final ValueSelector selector;

    Revisioned<List<Value>> readPage(final String key, final Map<String, JsonNode> filter) {
        final List<Value> values = readAllOnly(key, filter);

        final PageResult<Revision> revisions = revisionService.readPageRevisions(key,
                Cursor.<Long, Void>initial().with(null, 1).paginate());

        if (revisions.getElements().isEmpty()) {
            return Revisioned.create(values, null);
        }

        final PageRevision<Value> revision = revisionService.readPageAt(key, filter, revisions.getHead().getId());
        return Revisioned.create(values, revision.getRevision());
    }

    private List<Value> readAllOnly(final String key, final Map<String, JsonNode> filter) {
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

    Value readOnly(final String key, final Map<String, JsonNode> filter) {
        final List<Value> values = valueRepository.findAll(byKey(key));
        final List<Value> matched = selector.select(values, filter);

        return matched.stream()
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    Revisioned<Value> read(final String key, final Map<String, JsonNode> filter) {
        final Value value = readOnly(key, filter);
        final Revision revision = readLatestRevision(key, filter);
        return Revisioned.create(value, revision);
    }

    // TODO this is rather inefficient
    private Revision readLatestRevision(final String key, final Map<String, JsonNode> dimensions) {
        final Pagination<Long> pagination = Cursor.<Long, Void>initial().with(null, 1).paginate();
        final Revision revision = revisionService.readPageRevisions(key, pagination).getHead();
        return revisionService.readAt(key, dimensions, revision.getId()).getRevision();
    }

}
