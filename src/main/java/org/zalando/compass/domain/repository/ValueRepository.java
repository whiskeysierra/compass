package org.zalando.compass.domain.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ValueRepository {

    Value create(String key, Value value);

    List<Value> findAll(ValueCriteria criteria);

    void update(String key, Value value);

    void delete(String key, Map<String, JsonNode> dimensions);

}
