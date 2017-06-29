package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Value;

import java.util.List;
import java.util.Map;

public interface ValueService {

    void replace(String key, List<Value> values);

    boolean replace(String key, Map<String, JsonNode> dimensions, Value value);

    List<Value> readAll(String key, Map<String, JsonNode> filter);

    Value read(String key, Map<String, JsonNode> filter);

    void delete(String key, Map<String, JsonNode> filter);

}
