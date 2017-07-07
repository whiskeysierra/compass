package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

@lombok.Value
public class ValueRevision implements Dimensional {

    ImmutableMap<String, JsonNode> dimensions;
    Long index;
    Revision revision;
    JsonNode value;

    public Value toValue() {
        return new Value(dimensions, index, value);
    }

}
