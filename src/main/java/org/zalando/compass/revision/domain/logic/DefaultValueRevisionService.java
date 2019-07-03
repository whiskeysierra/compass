package org.zalando.compass.revision.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.revision.domain.api.ValueRevisionService;
import org.zalando.compass.core.domain.model.PageRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.revision.domain.model.ValueRevision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.Map;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DefaultValueRevisionService implements ValueRevisionService {

    private final CreateValueRevision create;
    private final ReadValueRevision read;

    @Transactional(propagation = REQUIRED)
    @Override
    public void create(final Key key, final ValueRevision revision) {
        create.create(key, revision);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readPageRevisions(final String key, final Pagination<Long> query) {
        return read.readPageRevisions(key, query);
    }

    @Transactional(readOnly = true)
    @Override
    public PageRevision<ValueRevision> readPageAt(final String key, final Map<Dimension, JsonNode> filter, final long revision) {
        return read.readPageAt(key, filter, revision);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readRevisions(final String key, @Nullable final Map<Dimension, JsonNode> dimensions,
            final Pagination<Long> query) {
        return read.readRevisions(key, dimensions, query);
    }

    @Transactional(readOnly = true)
    @Override
    public ValueRevision readAt(final String key, final Map<Dimension, JsonNode> dimensions, final long revision) {
        return read.readAt(key, dimensions, revision);
    }

}
