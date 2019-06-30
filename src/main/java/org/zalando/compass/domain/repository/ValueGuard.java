package org.zalando.compass.domain.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ValueGuard {

    List<Value> lockAll(ValueCriteria criteria);

    Optional<Value> lock(String key, Map<String, JsonNode> dimensions);

}
