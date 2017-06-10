package org.zalando.compass.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.Wither;

import static com.google.common.base.MoreObjects.firstNonNull;

@lombok.Value
public final class Value {

    @Wither
    private final ImmutableMap<String, JsonNode> dimensions;

    private final JsonNode value;

    @JsonCreator
    public Value(final ImmutableMap<String, JsonNode> dimensions, final JsonNode value) {
        this.dimensions = firstNonNull(dimensions, ImmutableMap.of());
        this.value = value;
    }


}
