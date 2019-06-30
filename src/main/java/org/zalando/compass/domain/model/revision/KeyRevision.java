package org.zalando.compass.domain.model.revision;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Revision;

@lombok.Value
public final class KeyRevision {

    String id;
    Revision revision;
    JsonNode schema;
    String description;

}
