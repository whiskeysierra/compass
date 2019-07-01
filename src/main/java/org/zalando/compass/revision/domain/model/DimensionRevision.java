package org.zalando.compass.revision.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.kernel.domain.model.Revision;

@lombok.Value
public final class DimensionRevision {

    String id;
    Revision revision;
    JsonNode schema;
    String relation;
    String description;

}
