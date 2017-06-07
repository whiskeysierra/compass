package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.Wither;
import org.zalando.compass.domain.persistence.model.tables.pojos.KeyRow;

@lombok.Value
public final class Key {

    @Wither
    private final String id;
    private final JsonNode schema;
    private final String description;

    public KeyRow toRow() {
        return new KeyRow(id, schema, description);
    }

    public static Key fromRow(final KeyRow row) {
        return new Key(row.getId(), row.getSchema(), row.getDescription());
    }

}
