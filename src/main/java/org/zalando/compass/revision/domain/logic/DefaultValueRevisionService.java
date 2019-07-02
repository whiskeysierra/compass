package org.zalando.compass.revision.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.revision.domain.api.ValueRevisionService;
import org.zalando.compass.core.domain.model.PageRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.revision.domain.model.ValueRevision;
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
    public PageRevision<ValueRevision> readPageAt(final String key, final Map<Dimension, JsonNode> filter, final long revision) {
        return readRevision.readPageAt(key, filter, revision);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readRevisions(final String key, final Map<Dimension, JsonNode> dimensions,
            final Pagination<Long> query) {
        return readRevision.readRevisions(key, dimensions, query);
    }

    @Transactional(readOnly = true)
    @Override
    public ValueRevision readAt(final String key, final Map<Dimension, JsonNode> dimensions, final long revision) {
        return readRevision.readAt(key, dimensions, revision);
    }

}
