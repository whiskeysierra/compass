package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

@lombok.Value
class RichValue {
    ImmutableMap<RichDimension, JsonNode> dimensions;
    JsonNode value;
}
