package org.zalando.compass.revision.domain.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.PageRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.revision.domain.model.ValueRevision;

import javax.annotation.Nullable;
import java.util.Map;

public interface ValueRevisionService {
    PageResult<Revision> readPageRevisions(String key, Pagination<Long> query);

    PageRevision<ValueRevision> readPageAt(String key, Map<Dimension, JsonNode> filter, long revision);

    // TODO make clear that dimensions have to match 100%
    PageResult<Revision> readRevisions(String key, @Nullable Map<Dimension, JsonNode> dimensions, Pagination<Long> query);

    // TODO make clear that dimensions have to match 100%
    ValueRevision readAt(String key, Map<Dimension, JsonNode> dimensions, long revision);
}
