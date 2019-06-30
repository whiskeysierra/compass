package org.zalando.compass.domain.logic.revision;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.ValueRevisionService;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.revision.ValueRevision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import java.util.Map;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DefaultValueRevisionService implements ValueRevisionService {

    private final ReadValueRevision readRevision;

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readPageRevisions(final String key, final Pagination<Long> query) {
        return readRevision.readPageRevisions(key, query);
    }

    @Transactional(readOnly = true)
    @Override
    public PageRevision<Value> readPageAt(final String key, final Map<String, JsonNode> filter, final long revision) {
        return readRevision.readPageAt(key, filter, revision);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readRevisions(final String key, final Map<String, JsonNode> dimensions,
            final Pagination<Long> query) {
        return readRevision.readRevisions(key, dimensions, query);
    }

    @Transactional(readOnly = true)
    @Override
    public ValueRevision readAt(final String key, final Map<String, JsonNode> dimensions, final long revision) {
        return readRevision.readAt(key, dimensions, revision);
    }

}
