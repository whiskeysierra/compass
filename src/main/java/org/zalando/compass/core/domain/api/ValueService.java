package org.zalando.compass.core.domain.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Revisioned;
import org.zalando.compass.core.domain.model.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

// TODO should take Key instead of String
public interface ValueService {

    boolean replace(String key, List<Value> values, @Nullable String comment);

    void create(String key, List<Value> values, @Nullable String comment);

    boolean replace(String key, Value value, @Nullable String comment);

    void create(String key, Value value, @Nullable String comment) throws EntityAlreadyExistsException;

    // TODO should return Revisioned<Values>
    Revisioned<List<Value>> readPage(String key, Map<Dimension, JsonNode> filter);

    Revisioned<Value> read(String key, Map<Dimension, JsonNode> filter);

    Value readOnly(String key, Map<Dimension, JsonNode> filter);

    void delete(String key, Map<Dimension, JsonNode> filter, @Nullable String comment);

}
