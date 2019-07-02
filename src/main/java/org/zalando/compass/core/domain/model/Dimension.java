package org.zalando.compass.core.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.experimental.Wither;
import org.zalando.compass.revision.domain.model.DimensionRevision;

@lombok.Value
@EqualsAndHashCode(of = "id")
public final class Dimension {

    @Wither
    String id;
    JsonNode schema;
    Relation relation;
    String description;

    // TODO move!
    public DimensionRevision toRevision(final Revision revision) {
        return new DimensionRevision(id, revision, schema, relation, description);
    }

    @Override
    public String toString() {
        return id;
    }

}
