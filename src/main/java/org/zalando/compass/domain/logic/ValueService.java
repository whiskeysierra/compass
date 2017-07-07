package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;

import java.util.List;
import java.util.Map;

public interface ValueService {

    boolean replace(String key, List<Value> values);

    boolean replace(String key, Value value);

    List<Value> readPage(String key, Map<String, JsonNode> filter);

    Value read(String key, Map<String, JsonNode> filter);

    List<Revision> readPageRevisions(String key);

    PageRevision<Value> readPageAt(String key, Map<String, JsonNode> filter, long revision);

    // TODO make clear that dimensions have to match 100%
    List<Revision> readRevisions(String key, Map<String, JsonNode> dimensions);

    // TODO make clear that dimensions have to match 100%
    ValueRevision readAt(String key, Map<String, JsonNode> dimensions, long revision);

    void delete(String key, Map<String, JsonNode> filter);

}
