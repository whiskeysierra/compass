package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ListMultimap;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface ValueService {

    void create(Value value);

    Value read(String key, Map<String, JsonNode> filter);

    List<Value> readAllByKey(String key, Map<String, JsonNode> filter);

    ListMultimap<String, Value> readAllByKeyPattern(@Nullable String keyPattern);

    void delete(String key, Map<String, JsonNode> filter);

}
