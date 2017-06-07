package org.zalando.compass.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.Wither;
import org.zalando.compass.domain.persistence.model.tables.pojos.ValueRow;

import javax.annotation.Nullable;

import static com.google.common.base.MoreObjects.firstNonNull;

@lombok.Value
public final class Value {

    @JsonIgnore
    @Wither
    private final String key;
    private final ImmutableMap<String, JsonNode> dimensions;
    private final JsonNode value;

    @JsonCreator
    private Value(final JsonNode value) {
        this(null, null, value);
    }

    @JsonCreator
    private Value(final ImmutableMap<String, JsonNode> dimensions, final JsonNode value) {
        this(null, dimensions, value);
    }

    public Value(@Nullable final String key, @Nullable final ImmutableMap<String, JsonNode> dimensions,
            final JsonNode value) {
        this.key = key;
        this.dimensions = firstNonNull(dimensions, ImmutableMap.of());
        this.value = value;
    }

    public ValueRow toRow() {
        // TODO implement proper conversion!
        return new ValueRow(key, null, value);
    }

    public static Value fromRow(final ValueRow row) {
        // TODO implement proper conversion!
        return new Value(row.getKey(), null, row.getValue());
    }

}
