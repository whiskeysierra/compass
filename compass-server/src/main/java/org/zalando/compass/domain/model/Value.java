package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

@lombok.Value
public final class Value {

    private final ImmutableMap<String, JsonNode> dimensions;
    private final JsonNode value;

}
