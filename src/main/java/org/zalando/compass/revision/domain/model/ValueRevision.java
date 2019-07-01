package org.zalando.compass.revision.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import org.zalando.compass.kernel.domain.model.Dimensional;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.kernel.domain.model.Value;

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
