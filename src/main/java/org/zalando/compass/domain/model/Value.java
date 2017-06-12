package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.Wither;

@lombok.Value
public class Value {

    @Wither
    ImmutableMap<String, JsonNode> dimensions;

    JsonNode value;

}
