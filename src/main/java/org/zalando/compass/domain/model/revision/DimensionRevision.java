package org.zalando.compass.domain.model.revision;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Revision;

@lombok.Value
public final class DimensionRevision {

    String id;
    Revision revision;
    JsonNode schema;
    String relation;
    String description;

}
