package org.zalando.compass.core.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;
import org.zalando.compass.revision.domain.model.ValueRevision;

@lombok.Value
@AllArgsConstructor
public class Value implements Dimensional {

    @Wither
    ImmutableMap<Dimension, JsonNode> dimensions;

    // TODO is this actually needed within the domain model?
    @Wither
    Long index;

    JsonNode value;

    public Value(final ImmutableMap<Dimension, JsonNode> dimensions, final JsonNode value) {
        this(dimensions, null, value);
    }

}
