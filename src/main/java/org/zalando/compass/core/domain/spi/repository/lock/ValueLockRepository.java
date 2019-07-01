package org.zalando.compass.core.domain.spi.repository.lock;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.kernel.domain.model.Value;
import org.zalando.compass.core.domain.spi.repository.ValueCriteria;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ValueLockRepository {

    List<Value> lockAll(ValueCriteria criteria);

    Optional<Value> lock(String key, Map<String, JsonNode> dimensions);

}
