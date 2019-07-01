package org.zalando.compass.kernel.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.Wither;
import org.zalando.compass.revision.domain.model.DimensionRevision;

@lombok.Value
public final class Dimension {

    @Wither
    String id;
    JsonNode schema;
    String relation;
    String description;

    public DimensionRevision toRevision(final Revision revision) {
        return new DimensionRevision(id, revision, schema, relation, description);
    }

}
