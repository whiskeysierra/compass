package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

@lombok.Value
public final class Dimension {

    String id;
    JsonNode schema;
    String relation;
    String description;

    public DimensionRevision toRevision(final Revision revision) {
        return new DimensionRevision(id, revision, schema, relation, description);
    }

}
