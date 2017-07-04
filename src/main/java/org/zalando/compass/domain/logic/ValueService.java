package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;

import java.util.List;
import java.util.Map;

public interface ValueService {

    boolean replace(String key, List<Value> values);

    boolean replace(String key, Value value);

    List<Value> readAll(String key, Map<String, JsonNode> filter);

    Value read(String key, Map<String, JsonNode> filter);

    List<ValueRevision> readRevisions(String key, Map<String, JsonNode> filter);

    void delete(String key, Map<String, JsonNode> filter);

}
