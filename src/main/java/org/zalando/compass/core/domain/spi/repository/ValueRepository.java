package org.zalando.compass.core.domain.spi.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.kernel.domain.model.Value;

import java.util.List;
import java.util.Map;

public interface ValueRepository {

    Value create(String key, Value value);

    List<Value> findAll(ValueCriteria criteria);

    void update(String key, Value value);

    void delete(String key, Map<String, JsonNode> dimensions);

}
