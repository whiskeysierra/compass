package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

@lombok.Value
public final class Key {

    @NotReserved
    String id;

    JsonNode schema;
    String description;

    public KeyRevision toRevision(final Revision revision) {
        return new KeyRevision(id, revision, schema, description);
    }

}
