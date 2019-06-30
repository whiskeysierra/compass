package org.zalando.compass.domain;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.revision.ValueRevision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import java.util.Map;

public interface ValueRevisionService {
    PageResult<Revision> readPageRevisions(String key, Pagination<Long> query);

    PageRevision<Value> readPageAt(String key, Map<String, JsonNode> filter, long revision);

    // TODO make clear that dimensions have to match 100%
    PageResult<Revision> readRevisions(String key, Map<String, JsonNode> dimensions, Pagination<Long> query);

    // TODO make clear that dimensions have to match 100%
    ValueRevision readAt(String key, Map<String, JsonNode> dimensions, long revision);
}
