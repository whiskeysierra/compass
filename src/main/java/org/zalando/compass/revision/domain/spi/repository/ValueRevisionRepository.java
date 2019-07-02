package org.zalando.compass.revision.domain.spi.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.revision.domain.model.ValueRevision;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.revision.domain.model.ValueRevisions;

import java.util.List;
import java.util.Map;

public interface ValueRevisionRepository {
    void create(String key, ValueRevision value);

    List<Revision> findPageRevisions(String key, Pagination<Long> query);

    ValueRevisions findPage(String key, long revisionId);

    ValueRevisions findValueRevisions(String key, long revisionId);

    List<Revision> findRevisions(String key, Map<Dimension, JsonNode> dimensions,
            Pagination<Long> query);
}
