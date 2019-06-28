package org.zalando.compass.domain;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Revisioned;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface ValueService {

    boolean replace(String key, List<Value> values, @Nullable String comment);

    void create(String key, List<Value> values, @Nullable String comment);

    boolean replace(String key, Value value, @Nullable String comment);

    void create(String key, Value value, @Nullable String comment) throws EntityAlreadyExistsException;

    Revisioned<List<Value>> readPage(String key, Map<String, JsonNode> filter);

    Revisioned<Value> read(String key, Map<String, JsonNode> filter);

    Value readOnly(String key, Map<String, JsonNode> filter);

    void delete(String key, Map<String, JsonNode> filter, @Nullable String comment);

}
