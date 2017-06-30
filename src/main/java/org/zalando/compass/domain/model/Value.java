package org.zalando.compass.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.Wither;
import org.zalando.compass.library.Internal;

@lombok.Value
public class Value {

    @Wither
    ImmutableMap<String, JsonNode> dimensions;

    @Internal
    @JsonIgnore
    @Wither
    Long index;

    JsonNode value;

    public ValueRevision toRevision(final Revision revision) {
        return new ValueRevision(dimensions, index, revision, value);
    }

}
