package org.zalando.compass.revision.infrastructure.http;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.core.infrastructure.http.RevisionRepresentation;
import org.zalando.compass.revision.domain.model.DimensionRevision;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class DimensionRevisionRepresentation {

    String id;
    RevisionRepresentation revision;
    JsonNode schema;
    String relation;
    String description;

    static DimensionRevisionRepresentation valueOf(final DimensionRevision dimension) {
        return new DimensionRevisionRepresentation(
                dimension.getId(),
                RevisionRepresentation.valueOf(dimension.getRevision()),
                dimension.getSchema(),
                dimension.getRelation(),
                dimension.getDescription()
        );
    }

}
