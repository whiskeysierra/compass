package org.zalando.compass.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.Wither;

import javax.annotation.Nullable;

import static com.google.common.base.MoreObjects.firstNonNull;

@lombok.Value
public final class Value {

    @JsonIgnore
    @Wither
    private final String key;
    private final ImmutableMap<String, JsonNode> dimensions;
    private final JsonNode value;

    @JsonCreator
    private Value(@Nullable final ImmutableMap<String, JsonNode> dimensions, final JsonNode value) {
        this(null, dimensions, value);
    }

    public Value(@Nullable final String key, @Nullable final ImmutableMap<String, JsonNode> dimensions,
            final JsonNode value) {
        this.key = key;
        this.dimensions = firstNonNull(dimensions, ImmutableMap.of());
        this.value = value;
    }

}
