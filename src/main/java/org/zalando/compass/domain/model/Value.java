package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.Wither;
import org.zalando.compass.domain.model.revision.ValueRevision;

@lombok.Value
public class Value implements Dimensional {

    @Wither
    ImmutableMap<String, JsonNode> dimensions;

    @Wither
    Long index;

    JsonNode value;

    public ValueRevision toRevision(final Revision revision) {
        return new ValueRevision(dimensions, index, revision, value);
    }

}
