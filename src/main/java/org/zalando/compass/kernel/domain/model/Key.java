package org.zalando.compass.kernel.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.Wither;
import org.zalando.compass.revision.domain.model.KeyRevision;

@lombok.Value
public final class Key {

    @Wither
    String id;
    JsonNode schema;
    String description;

    public KeyRevision toRevision(final Revision revision) {
        return new KeyRevision(id, revision, schema, description);
    }

}
