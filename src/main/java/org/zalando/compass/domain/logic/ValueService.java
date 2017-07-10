package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface ValueService {

    boolean replace(String key, List<Value> values, @Nullable String comment);

    boolean replace(String key, Value value, @Nullable String comment);

    Page<Value> readPage(String key, Map<String, JsonNode> filter);

    Value read(String key, Map<String, JsonNode> filter);

    Page<Revision> readPageRevisions(String key);

    PageRevision<Value> readPageAt(String key, Map<String, JsonNode> filter, long revision);

    // TODO make clear that dimensions have to match 100%
    Page<Revision> readRevisions(String key, Map<String, JsonNode> dimensions);

    // TODO make clear that dimensions have to match 100%
    ValueRevision readAt(String key, Map<String, JsonNode> dimensions, long revision);

    void delete(String key, Map<String, JsonNode> filter, @Nullable String comment);

}
