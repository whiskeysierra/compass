package org.zalando.compass.revision.domain.spi.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.revision.domain.model.ValueRevision;
import org.zalando.compass.library.pagination.Pagination;

import java.util.List;
import java.util.Map;

public interface ValueRevisionRepository {
    void create(String key, ValueRevision value);

    List<Revision> findPageRevisions(String key, Pagination<Long> query);

    List<ValueRevision> findPage(String key, long revisionId);

    List<ValueRevision> findValueRevisions(String key, long revisionId);

    List<Revision> findRevisions(String key, Map<String, JsonNode> dimensions,
            Pagination<Long> query);
}
