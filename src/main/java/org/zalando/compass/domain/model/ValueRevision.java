package org.zalando.compass.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import org.zalando.compass.library.Internal;

@lombok.Value
public class ValueRevision {

    @Internal
    @JsonIgnore
    Long id;

    ImmutableMap<String, JsonNode> dimensions;

    @Internal
    @JsonIgnore
    Long index;

    Revision revision;
    JsonNode value;

}
