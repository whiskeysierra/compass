package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.library.pagination.PageQuery;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface ValueService {

    boolean replace(String key, List<Value> values, @Nullable String comment);

    boolean replace(String key, Value value, @Nullable String comment);

    List<Value> readPage(String key, Map<String, JsonNode> filter);

    Value read(String key, Map<String, JsonNode> filter);

    PageResult<Revision> readPageRevisions(String key, final PageQuery<Long> query);

    PageRevision<Value> readPageAt(String key, Map<String, JsonNode> filter, long revision);

    // TODO make clear that dimensions have to match 100%
    PageResult<Revision> readRevisions(String key, Map<String, JsonNode> dimensions, final PageQuery<Long> query);

    // TODO make clear that dimensions have to match 100%
    ValueRevision readAt(String key, Map<String, JsonNode> dimensions, long revision);

    void delete(String key, Map<String, JsonNode> filter, @Nullable String comment);

}
