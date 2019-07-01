package org.zalando.compass.core.infrastructure.http.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.revision.domain.model.DimensionRevision;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class DimensionRevisionRepresentation {

    String id;
    RevisionRepresentation revision;
    JsonNode schema;
    String relation;
    String description;

    public static DimensionRevisionRepresentation valueOf(final DimensionRevision dimension) {
        return new DimensionRevisionRepresentation(
                dimension.getId(),
                RevisionRepresentation.valueOf(dimension.getRevision()),
                dimension.getSchema(),
                dimension.getRelation(),
                dimension.getDescription()
        );
    }

}
