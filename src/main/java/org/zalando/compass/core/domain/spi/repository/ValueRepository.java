package org.zalando.compass.core.domain.spi.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.Values;

import java.util.Map;

public interface ValueRepository {

    Value create(String key, Value value);

    Values findAll(ValueCriteria criteria);

    void update(String key, Value value);

    void delete(String key, Map<Dimension, JsonNode> dimensions);

}
