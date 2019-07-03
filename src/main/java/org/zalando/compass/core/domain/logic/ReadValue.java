package org.zalando.compass.core.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.KeyService;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.PageRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.model.Revisioned;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.Values;
import org.zalando.compass.core.domain.spi.repository.ValueRepository;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.revision.domain.api.ValueRevisionService;
import org.zalando.compass.revision.domain.model.ValueRevision;

import java.util.List;
import java.util.Map;

import static org.zalando.compass.core.domain.spi.repository.ValueCriteria.byKey;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReadValue {

    private final ValueRepository valueRepository;
    private final KeyService keyService;
    private final ValueRevisionService revisionService;

    Revisioned<List<Value>> readPage(final String key, final Map<Dimension, JsonNode> filter) {
        final List<Value> values = readAllOnly(key, filter);

        final PageResult<Revision> revisions = revisionService.readPageRevisions(key,
                Cursor.<Long, Void>initial().with(null, 1).paginate());

        if (revisions.getElements().isEmpty()) {
            return Revisioned.create(values, null);
        }

        final PageRevision<ValueRevision> revision = revisionService.readPageAt(key, filter, revisions.getHead().getId());
        return Revisioned.create(values, revision.getRevision());
    }

    private List<Value> readAllOnly(final String key, final Map<Dimension, JsonNode> filter) {
        final Values values = valueRepository.findAll(byKey(key));

        if (values.isEmpty()) {
            // the fact that we can delay this check (foreign key constraint) should not be known to this layer...
            keyService.readOnly(key);
            return values;
        }

        if (filter.isEmpty()) {
            // special case, just for reading many values
            return values;
        }

        return values.select(filter);
    }

    Value readOnly(final String key, final Map<Dimension, JsonNode> filter) {
        return valueRepository.findAll(byKey(key)).selectOne(filter);
    }

    Revisioned<Value> read(final String key, final Map<Dimension, JsonNode> filter) {
        final Value value = readOnly(key, filter);
        final Revision revision = readLatestRevision(key, filter);
        return Revisioned.create(value, revision);
    }

    // TODO this is rather inefficient
    private Revision readLatestRevision(final String key, final Map<Dimension, JsonNode> dimensions) {
        final Pagination<Long> pagination = Cursor.<Long, Void>initial().with(null, 1).paginate();
        final Revision revision = revisionService.readPageRevisions(key, pagination).getHead();
        return revisionService.readAt(key, dimensions, revision.getId()).getRevision();
    }

}
