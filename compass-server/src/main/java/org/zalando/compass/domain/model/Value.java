package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;

import static com.google.common.base.MoreObjects.firstNonNull;

@lombok.Value
public final class Value {

    private final ImmutableMap<String, JsonNode> dimensions;
    private final JsonNode value;

    public Value(@Nullable final ImmutableMap<String, JsonNode> dimensions, final JsonNode value) {
        this.dimensions = firstNonNull(dimensions, ImmutableMap.of());
        this.value = value;
    }

}
