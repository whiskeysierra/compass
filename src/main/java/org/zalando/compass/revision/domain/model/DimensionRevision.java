package org.zalando.compass.revision.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.core.domain.model.Relation;
import org.zalando.compass.core.domain.model.Revision;

@lombok.Value
public final class DimensionRevision {

    String id;
    Revision revision;
    JsonNode schema;
    Relation relation;
    String description;

}
